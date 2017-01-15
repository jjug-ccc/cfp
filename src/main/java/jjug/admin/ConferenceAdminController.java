package jjug.admin;

import static jjug.submission.enums.SubmissionStatus.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import jjug.conference.Conference;
import jjug.conference.ConferenceRepository;
import jjug.submission.Submission;
import jjug.vote.VoteRepository;
import jjug.vote.VoteSummary;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ConferenceAdminController {
	private final ConferenceRepository conferenceRepository;
	private final VoteRepository voteRepository;

	@ModelAttribute
	ConferenceForm conferenceForm() {
		return new ConferenceForm();
	}

	@GetMapping("admin/conferences/{confId}")
	String getConf(@PathVariable UUID confId, Model model, ConferenceForm form) {
		Conference conference = conferenceRepository.findOne(confId).get();
		BeanUtils.copyProperties(conference, form);
		model.addAttribute("conference", conference);
		List<Submission> submissions = conference.getSessions();
		model.addAttribute("submittedSubmissions",
				submissions.stream().filter(s -> s.getSubmissionStatus() == SUBMITTED)
						.collect(Collectors.toList()));
		model.addAttribute("draftSubmissions",
				submissions.stream().filter(s -> s.getSubmissionStatus() == DRAFT)
						.collect(Collectors.toList()));
		model.addAttribute("withdrawnSubmissions",
				submissions.stream().filter(s -> s.getSubmissionStatus() == WITHDRAWN)
						.collect(Collectors.toList()));
		List<VoteSummary> voteSummaries = voteRepository.reportSummary(confId);
		model.addAttribute("voteSummaries", voteSummaries);
		return "admin/conference";
	}

	@PostMapping("admin/conferences/{confId}")
	String postConf(@PathVariable UUID confId, Model model,
			@Validated ConferenceForm form, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return getConf(confId, model, form);
		}
		Conference conference = conferenceRepository.findOne(confId).get();
		BeanUtils.copyProperties(form, conference);
		conferenceRepository.save(conference);
		return "redirect:/";
	}

}
