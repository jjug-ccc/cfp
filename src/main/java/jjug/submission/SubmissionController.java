package jjug.submission;

import jjug.CfpUser;
import jjug.conference.Conference;
import jjug.conference.ConferenceRepository;
import jjug.speaker.Speaker;
import jjug.speaker.SpeakerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static jjug.submission.enums.SubmissionStatus.*;

@Controller
@RequiredArgsConstructor
@Slf4j
public class SubmissionController {
	private final SubmissionRepository submissionRepository;
	private final ConferenceRepository conferenceRepository;
	private final SpeakerRepository speakerRepository;

	@ModelAttribute
	SubmissionForm submissionForm(@AuthenticationPrincipal CfpUser user) {
		SubmissionForm submissionForm = new SubmissionForm();
		if (user != null) {
			SpeakerForm speakerForm = new SpeakerForm();
			speakerForm.setName(user.getName());
			speakerForm.setGithub(user.getGithub());
			speakerForm
					.setActivities(format("https://github.com/%s", user.getGithub()));
			speakerForm.setProfileUrl(
					format("https://avatars.githubusercontent.com/%s?size=120",
							user.getGithub()));
			speakerForm.setEmail(user.getEmail());
			submissionForm.setSpeakerForms(new LinkedList<>(Collections.singletonList(speakerForm)));
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
		Conference conference = conferenceRepository.findOne(confId).get();
		checkIfCfpIsOpen(conference);
		speakerRepository.findByGithub(user.getGithub())
				.ifPresent(speaker -> BeanUtils.copyProperties(speaker, submissionForm));
		model.addAttribute("conference", conference);
		return "submission/submissionForm";
	}

	@PostMapping("conferences/{confId}/submissions/form")
	String submitSubmission(@PathVariable UUID confId, Model model,
			@RequestParam Optional<String> draft,
			@Validated SubmissionForm submissionForm, BindingResult bindingResult,
			@AuthenticationPrincipal CfpUser user) {
		if (bindingResult.hasErrors()) {
			return submitForm(confId, model, submissionForm, user);
		}
		Conference conference = conferenceRepository.findOne(confId).get();
		checkIfCfpIsOpen(conference);

		Submission submission = new Submission();
		BeanUtils.copyProperties(submissionForm, submission);
		submission.setConference(conference);
		submission.setSpeakers(copyToSpeakers(submissionForm.getSpeakerForms()));
		submission.setSubmissionStatus(draft.map(d -> DRAFT).orElse(SUBMITTED));
		log.info("Submit {}", submission);
		submissionRepository.save(submission);
		return "redirect:/";
	}

	@PostMapping(value = "conferences/{confId}/submissions/form", params = "add-speaker")
	public String addSpeakerForCreate(
			SubmissionForm submissionForm,
			@PathVariable UUID confId,
			Model model) {
		submissionForm.getSpeakerForms().add(new SpeakerForm());
		Conference conference = conferenceRepository.findOne(confId).get();
		checkIfCfpIsOpen(conference);
		model.addAttribute("conference", conference);
		return "submission/submissionForm";
	}

	@PostMapping(value = "conferences/{confId}/submissions/form", params = "remove-speaker")
	public String removeSpeakerForCreate(
			SubmissionForm submissionForm,
			@PathVariable UUID confId,
			Model model) {
		submissionForm.getSpeakerForms().removeLast();
		Conference conference = conferenceRepository.findOne(confId).get();
		checkIfCfpIsOpen(conference);
		model.addAttribute("conference", conference);
		return "submission/submissionForm";
	}

	@GetMapping("submissions/{submissionId}/form")
	String editForm(@PathVariable UUID submissionId, Model model,
			SubmissionForm submissionForm,
			boolean updated) {
		Submission submission = submissionRepository.findOne(submissionId).get();
		model.addAttribute("submission", submission);
		model.addAttribute("conference", submission.getConference());
		if (updated) {
			return "submission/submissionEditForm";
		}

		BeanUtils.copyProperties(submission, submissionForm);
		List<Speaker> speakers = submission.getSpeakers();
		Collections.reverse(speakers);
		Deque<SpeakerForm> speakerForms = new LinkedList<>();
		for (Speaker speaker : speakers) {
			SpeakerForm form = new SpeakerForm();
			BeanUtils.copyProperties(speaker, form);
			speakerForms.add(form);
		}
		submissionForm.setSpeakerForms(speakerForms);

		return "submission/submissionEditForm";
	}

	@PostMapping("submissions/{submissionId}/form")
	String editSubmission(@PathVariable UUID submissionId, Model model,
			@RequestParam Optional<String> draft, @RequestParam Optional<String> withdraw,
			@Validated SubmissionForm submissionForm, BindingResult bindingResult,
			@AuthenticationPrincipal CfpUser user) {
		if (bindingResult.hasErrors()) {
			return editForm(submissionId, model, submissionForm, true);
		}
		Submission submission = submissionRepository.findOne(submissionId).get();
		if (submission.getConference().getConfStatus().isFixedCfp()) {
			if (draft.isPresent() || withdraw.isPresent()) {
				throw new CfpFixedException();
			}
		} else {
			submission.setSubmissionStatus(draft.map(d -> DRAFT)
					.orElseGet(() -> withdraw.map(w -> WITHDRAWN).orElse(SUBMITTED)));
		}

		BeanUtils.copyProperties(submissionForm, submission);
		submission.setSpeakers(copyToSpeakers(submissionForm.getSpeakerForms()));
		log.info("Edit {}", submission);
		submissionRepository.save(submission);
		return "redirect:/submissions/{submissionId}/form";
	}

	@PostMapping(value = "submissions/{submissionId}/form", params = "add-speaker")
	public String addSpeakerForEdit(
			SubmissionForm submissionForm,
			@PathVariable UUID submissionId,
			Model model) {
		submissionForm.getSpeakerForms().add(new SpeakerForm());
		model.addAttribute("submission", submissionRepository.findOne(submissionId).get());
		return "submission/submissionEditForm";
	}

	@PostMapping(value = "submissions/{submissionId}/form", params = "remove-speaker")
	public String removeSpeakerForEdit(
			SubmissionForm submissionForm,
			@PathVariable UUID submissionId,
			Model model) {
		submissionForm.getSpeakerForms().removeLast();
		model.addAttribute("submission", submissionRepository.findOne(submissionId).get());
		return "submission/submissionEditForm";
	}

	void checkIfCfpIsOpen(Conference conference) {
		if (!conference.getConfStatus().isOpenCfp()) {
			throw new CfpClosedException();
		}
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
							return speaker;
						}
				).collect(Collectors.toList());
	}


}
