package jjug.submission;

import java.util.*;
import java.util.stream.Collectors;

import jjug.CfpUser;
import jjug.conference.Conference;
import jjug.conference.ConferenceRepository;
import jjug.speaker.Activity;
import jjug.speaker.Speaker;
import jjug.speaker.SpeakerRepository;
import jjug.speaker.enums.ActivityType;
import jjug.sponsor.SponsorUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.BeanUtils;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static java.util.Objects.nonNull;
import static jjug.submission.enums.SubmissionStatus.*;
import static org.springframework.util.StringUtils.hasLength;

@Controller
@RequiredArgsConstructor
@Slf4j
public class SubmissionController {
	private final SubmissionRepository submissionRepository;
	private final ConferenceRepository conferenceRepository;
	private final SpeakerRepository speakerRepository;
	private final SubmissionFormValidator submissionFormValidator;

	@InitBinder("submissionForm")
	public void initBinder(WebDataBinder binder) {
		binder.addValidators(submissionFormValidator);
	}

	@ModelAttribute
	SubmissionForm submissionForm(@AuthenticationPrincipal CfpUser user) {
		SubmissionForm submissionForm = new SubmissionForm();
		if (user != null) {
			SpeakerForm speakerForm = new SpeakerForm();
			speakerForm.setName(user.getName());
			speakerForm.setGithub(user.getGithub());
			ActivityForm activity = new ActivityForm();
			String profileUrl = "http://www.java-users.jp/ccc2017fall/assets/img/speakers/duke.jpg";
			if (StringUtils.hasLength(user.getGithub())) {
				activity.setActivityType(ActivityType.GITHUB);
				activity.setUrl(format("https://github.com/%s", user.getGithub()));
				profileUrl = format("https://avatars.githubusercontent.com/%s?size=120",
						user.getGithub());
			}
			speakerForm.setProfileUrl(profileUrl);
			List<ActivityForm> activityFormList = new ArrayList<>();
			activityFormList.add(activity);
			speakerForm.setActivityList(activityFormList);
			speakerForm.setEmail(user.getEmail());
			Deque<SpeakerForm> speakerForms = new LinkedList<>();
			speakerForms.add(speakerForm);
			submissionForm.setSpeakerForms(speakerForms);
			if (user instanceof SponsorUser) {
				SponsorUser sponsorUser = SponsorUser.class.cast(user);
				speakerForm
						.setCompanyOrCommunity(sponsorUser.getSponsor().getSponsorName());
			}
		}
		return submissionForm;
	}

	@GetMapping("conferences/{confId}/submissions")
	String showSubmissions(@PathVariable UUID confId, Model model,
			SubmissionForm submissionForm) {
		Conference conference = conferenceRepository.findOne(confId).get();
		checkIfCfpIsOpen(conference);
		model.addAttribute("conference", conference);
		return "submission/submissions";
	}

	@GetMapping("conferences/{confId}/submissions/form")
	String submitForm(@PathVariable UUID confId, Model model,
			SubmissionForm submissionForm, @AuthenticationPrincipal CfpUser user) {
		addModelForCreate(confId, model);
		speakerRepository.findByGithub(user.getGithub())
				.ifPresent(speaker -> copyToSpeakerForm(speaker, submissionForm));
		return "submission/submissionForm";
	}

	@PostMapping("conferences/{confId}/submissions/form")
	String submitSubmission(@PathVariable UUID confId, Model model,
			@RequestParam Optional<String> draft,
			@Validated SubmissionForm submissionForm, BindingResult bindingResult,
			@AuthenticationPrincipal CfpUser user) {
		if (bindingResult.hasErrors()) {
			addModelForCreate(confId, model);
			return "submission/submissionForm";
		}
		Conference conference = conferenceRepository.findOne(confId).get();
		checkIfCfpIsOpen(conference);

		Submission submission = new Submission();
		BeanUtils.copyProperties(submissionForm, submission);
		submission.setSubmissionId(UUID.randomUUID());
		submission.setConference(conference);
		submission.setSpeakers(copyToSpeakers(submissionForm.getSpeakerForms()));
		submission.setSubmissionStatus(draft.map(d -> DRAFT).orElse(SUBMITTED));
		log.info("Submit {}", submission);
		submissionRepository.save(submission.created());
		return "redirect:/";
	}

	private void addModelForCreate(UUID confId, Model model) {
		Conference conference = conferenceRepository.findOne(confId).get();
		checkIfCfpIsOpen(conference);
		model.addAttribute("conference", conference);
	}

	@PostMapping(value = "conferences/{confId}/submissions/form", params = "add-speaker")
	public String addSpeakerForCreate(SubmissionForm submissionForm,
			@PathVariable UUID confId, Model model) {
		submissionForm.getSpeakerForms().add(new SpeakerForm());
		Conference conference = conferenceRepository.findOne(confId).get();
		checkIfCfpIsOpen(conference);
		model.addAttribute("conference", conference);
		return "submission/submissionForm";
	}

	@PostMapping(value = "conferences/{confId}/submissions/form", params = "remove-speaker")
	public String removeSpeakerForCreate(SubmissionForm submissionForm,
			@PathVariable UUID confId, Model model) {
		submissionForm.getSpeakerForms().removeLast();
		Conference conference = conferenceRepository.findOne(confId).get();
		checkIfCfpIsOpen(conference);
		model.addAttribute("conference", conference);
		return "submission/submissionForm";
	}

	@GetMapping("submissions/{submissionId}/form")
	String editForm(@PathVariable UUID submissionId, Model model,
			SubmissionForm submissionForm) {
		Submission submission = addModelForEdit(submissionId, model);
		BeanUtils.copyProperties(submission, submissionForm);
		submissionForm.setSpeakerForms(copyToSpeakerForms(submission.getSpeakers()));
		return "submission/submissionEditForm";
	}

	@PostMapping("submissions/{submissionId}/form")
	String editSubmission(@PathVariable UUID submissionId, Model model,
			@RequestParam Optional<String> draft, @RequestParam Optional<String> withdraw,
			@Validated SubmissionForm submissionForm, BindingResult bindingResult,
			@AuthenticationPrincipal CfpUser user) {
		if (bindingResult.hasErrors()) {
			addModelForEdit(submissionId, model);
			return "submission/submissionEditForm";
		}
		Submission submission = submissionRepository.findOne(submissionId).get();
		if (submission.getConference().getConfStatus().isFixedCfp()) {
			if (draft.isPresent() || withdraw.isPresent()) {
				throw new CfpFixedException();
			}
		}
		else {
			submission.setSubmissionStatus(draft.map(d -> DRAFT)
					.orElseGet(() -> withdraw.map(w -> WITHDRAWN).orElse(SUBMITTED)));
		}

		BeanUtils.copyProperties(submissionForm, submission);
		submission.setSpeakers(copyToSpeakers(submissionForm.getSpeakerForms()));
		log.info("Edit {}", submission);
		submissionRepository.save(submission.updatedBySpeaker());
		return "redirect:/submissions/{submissionId}/form";
	}

	private Submission addModelForEdit(UUID submissionId, Model model) {
		Submission submission = submissionRepository.findOne(submissionId).get();
		model.addAttribute("submission", submission);
		model.addAttribute("conference", submission.getConference());
		return submission;
	}

	@PostMapping(value = "submissions/{submissionId}/form", params = "add-speaker")
	public String addSpeakerForEdit(SubmissionForm submissionForm,
			@PathVariable UUID submissionId, Model model) {
		submissionForm.getSpeakerForms().add(new SpeakerForm());
		model.addAttribute("submission",
				submissionRepository.findOne(submissionId).get());
		return "submission/submissionEditForm";
	}

	@PostMapping(value = "submissions/{submissionId}/form", params = "remove-speaker")
	public String removeSpeakerForEdit(SubmissionForm submissionForm,
			@PathVariable UUID submissionId, Model model) {
		submissionForm.getSpeakerForms().removeLast();
		model.addAttribute("submission",
				submissionRepository.findOne(submissionId).get());
		return "submission/submissionEditForm";
	}

	void checkIfCfpIsOpen(Conference conference) {
		if (!conference.getConfStatus().isOpenCfp()) {
			throw new CfpClosedException();
		}
	}

	void copyToSpeakerForm(Speaker speaker, SubmissionForm submissionForm) {
		SpeakerForm speakerForm = submissionForm.getSpeakerForms().getFirst();
		BeanUtils.copyProperties(speaker, speakerForm);

		List<ActivityForm> activityForms = speaker.getActivityList().stream()
				.map(activity -> {
					ActivityForm activityForm = new ActivityForm();
					BeanUtils.copyProperties(activity, activityForm);
					return activityForm;
				}).collect(Collectors.toList());
		speakerForm.setActivityList(activityForms);
		submissionForm.setSpeakerForms(new LinkedList<>(singletonList(speakerForm)));
	}

	Deque<SpeakerForm> copyToSpeakerForms(List<Speaker> speakers) {
		if (CollectionUtils.isEmpty(speakers)) {
			return new LinkedList<>();
		}
		Deque<SpeakerForm> speakerForms = new LinkedList<>();
		for (Speaker speaker : speakers) {
			SpeakerForm speakerForm = new SpeakerForm();
			BeanUtils.copyProperties(speaker, speakerForm);

			List<ActivityForm> activityList = speaker.getActivityList().stream()
					.map(activity -> {
						ActivityForm activityForm = new ActivityForm();
						BeanUtils.copyProperties(activity, activityForm);
						return activityForm;
					}).collect(Collectors.toList());
			speakerForm.setActivityList(activityList);
			speakerForms.add(speakerForm);
		}
		return speakerForms;
	}

	List<Speaker> copyToSpeakers(Deque<SpeakerForm> speakerForms) {
		if (CollectionUtils.isEmpty(speakerForms)) {
			return Collections.emptyList();
		}
		return speakerForms.stream()
				.map(
						speakerForm -> {
							Speaker speaker = speakerRepository.findByGithub(speakerForm.getGithub())
									.orElseGet(() -> Speaker.builder().github(speakerForm.getGithub()).build());
							BeanUtils.copyProperties(speakerForm, speaker);

							List<Activity> activityList = speakerForm.getActivityList().stream()
									.filter(activityForm -> nonNull(activityForm.getUrl()) && hasLength(activityForm.getUrl()))
									.distinct()
									.map(activityForm -> {
										Activity activity = new Activity();
										BeanUtils.copyProperties(activityForm, activity);
										return activity;
									}).collect(Collectors.toList());
							speaker.setActivityList(activityList);

							return speaker;
						}
				).collect(Collectors.toList());
	}

}
