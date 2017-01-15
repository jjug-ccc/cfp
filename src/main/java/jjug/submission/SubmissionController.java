package jjug.submission;

import static java.lang.String.format;
import static jjug.submission.enums.SubmissionStatus.*;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jjug.CfpUser;
import jjug.conference.Conference;
import jjug.conference.ConferenceRepository;
import jjug.speaker.Speaker;
import jjug.speaker.SpeakerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
		submissionForm.setName(user.getName());
		submissionForm.setActivities(format("https://github.com/%s", user.getGithub()));
		submissionForm.setProfileUrl(format(
				"https://avatars.githubusercontent.com/%s?size=120", user.getGithub()));
		submissionForm.setEmail(user.getEmail());
		return submissionForm;
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
		Speaker speaker = speakerRepository.findByGithub(user.getGithub())
				.orElseGet(() -> Speaker.builder().github(user.getGithub()).build());
		Submission submission = new Submission();
		BeanUtils.copyProperties(submissionForm, speaker);
		BeanUtils.copyProperties(submissionForm, submission);
		submission.setConference(conference);
		submission.setSpeaker(speaker);
		submission.setSubmissionStatus(draft.map(d -> DRAFT).orElse(SUBMITTED));
		log.info("Submit {}", submission);
		submissionRepository.save(submission);
		return "redirect:/";
	}

	@GetMapping("submissions/{submissionId}/form")
	String editForm(@PathVariable UUID submissionId, Model model,
			SubmissionForm submissionForm) {
		Submission submission = submissionRepository.findOne(submissionId).get();
		BeanUtils.copyProperties(submission, submissionForm);
		BeanUtils.copyProperties(submission.getSpeaker(), submissionForm);
		model.addAttribute("submission", submission);
		model.addAttribute("conference", submission.getConference());
		return "submission/submissionForm";
	}

	@PostMapping("submissions/{submissionId}/form")
	String editSubmission(@PathVariable UUID submissionId, Model model,
			@RequestParam Optional<String> draft, @RequestParam Optional<String> withdraw,
			@Validated SubmissionForm submissionForm, BindingResult bindingResult,
			@AuthenticationPrincipal CfpUser user) {
		if (bindingResult.hasErrors()) {
			return editForm(submissionId, model, submissionForm);
		}
		Submission submission = submissionRepository.findOne(submissionId).get();
		Speaker speaker = speakerRepository.findByGithub(user.getGithub())
				.orElseGet(() -> Speaker.builder().github(user.getGithub()).build());
		BeanUtils.copyProperties(submissionForm, speaker);
		BeanUtils.copyProperties(submissionForm, submission);
		submission.setSpeaker(speaker);
		submission.setSubmissionStatus(draft.map(d -> DRAFT)
				.orElseGet(() -> withdraw.map(w -> WITHDRAWN).orElse(SUBMITTED)));
		log.info("Edit {}", submission);
		submissionRepository.save(submission);
		return "redirect:/submissions/{submissionId}/form";
	}

	void checkIfCfpIsOpen(Conference conference) {
		if (!conference.getConfStatus().isOpenCfp()) {
			throw new CfpClosedException();
		}
	}
}
