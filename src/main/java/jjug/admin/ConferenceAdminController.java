package jjug.admin;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import jjug.conference.Conference;
import jjug.conference.ConferenceRepository;
import jjug.speaker.Speakers;
import jjug.sponsor.Sponsor;
import jjug.submission.Submission;
import jjug.submission.SubmissionRepository;
import jjug.submission.SubmissionService;
import jjug.submission.SubmissionSurvey;
import jjug.vote.VoteRepository;
import jjug.vote.VoteSummary;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import static jjug.submission.enums.SubmissionStatus.*;

@Controller
@RequiredArgsConstructor
public class ConferenceAdminController {
	private final ConferenceRepository conferenceRepository;
	private final VoteRepository voteRepository;
	private final SubmissionService submissionService;
	private final SubmissionRepository submissionRepository;

	@ModelAttribute
	ConferenceForm conferenceForm() {
		return new ConferenceForm();
	}

	@GetMapping("admin/conferences")
	String createForm(Model model, ConferenceCreateForm form) {
		model.addAttribute("conferenceCreateForm", form);
		return "admin/conferenceCreateForm";
	}

	@PostMapping("admin/conferences")
	String create(Model model, ConferenceCreateForm form, BindingResult bindingResult) {
		return ConferenceCreateForm.validator.validateToEither(form) //
				.rightMap(ConferenceCreateForm::toConference) //
				.fold(violations -> {
					violations.apply(bindingResult::rejectValue);
					return createForm(model, form);
				}, conf -> {
					Conference created = conferenceRepository.save(conf);
					return "redirect:/admin/conferences/" + created.getConfId();
				});
	}

	@GetMapping("admin/conferences/{confId}")
	String getConf(@PathVariable UUID confId, Model model, ConferenceForm form) {
		Conference conference = conferenceRepository.findOne(confId).get();
		BeanUtils.copyProperties(conference, form);
		model.addAttribute("conference", conference);
		List<Submission> submissions = conference.getSessions();
		List<Sponsor> sponsors = conference.getSponsors();
		model.addAttribute("submittedSubmissions",
				submissions.stream().filter(s -> s.getSubmissionStatus() == SUBMITTED)
						.collect(Collectors.toList()));
		model.addAttribute("sponsoredSubmissions",
				submissions.stream().filter(s -> s.getSubmissionStatus() == SPONSORED)
						.collect(Collectors.toList()));
		model.addAttribute("draftSubmissions",
				submissions.stream().filter(s -> s.getSubmissionStatus() == DRAFT)
						.collect(Collectors.toList()));
		model.addAttribute("withdrawnSubmissions",
				submissions.stream().filter(s -> s.getSubmissionStatus() == WITHDRAWN)
						.collect(Collectors.toList()));
		model.addAttribute("speakers", submissions.stream().collect(Collectors
				.toMap(Submission::getSubmissionId, s -> new Speakers(s.getSpeakers()))));
		List<VoteSummary> voteSummaries = voteRepository.reportSummary(confId).stream()
				.sorted(Comparator.comparing(VoteSummary::getStatus))
				.collect(Collectors.toList());
		ChangeStatusForm changeStatusForm = new ChangeStatusForm(voteSummaries.stream()
				.map(s -> new SubmissionService.Status(s.getSubmissionId(),
						s.getStatus()))
				.collect(Collectors.toList()));
		model.addAttribute("voteSummaries", voteSummaries);
		model.addAttribute("sponsors", sponsors);
		model.addAttribute("changeStatusForm", changeStatusForm);
		return "admin/conference";
	}

	@PostMapping(path = "admin/conferences/{confId}", params = "changeConfStatus")
	String changeConfStatus(@PathVariable UUID confId, Model model,
			@Validated ConferenceForm form, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return getConf(confId, model, form);
		}
		Conference conference = conferenceRepository.findOne(confId).get();
		BeanUtils.copyProperties(form, conference);
		conferenceRepository.save(conference);
		return "redirect:/";
	}

	@PostMapping(path = "admin/conferences/{confId}", params = "changeSubmissionStatus")
	String changeSubmissionStatus(@PathVariable UUID confId, Model model,
			@Validated ChangeStatusForm form, BindingResult bindingResult) {
		List<SubmissionService.Status> statuses = form.getStatuses();
		submissionService.changeStatus(statuses);
		return "redirect:/admin/conferences/{confId}";
	}

	@GetMapping(path = "admin/conferences/{confId}/survey")
	String survey(@PathVariable UUID confId, Model model) {
		Conference conference = conferenceRepository.findOne(confId).get();
		List<SubmissionSurvey> surveys = this.submissionRepository.reportSurvey(confId);
		model.addAttribute("conference", conference);
		model.addAttribute("surveys", surveys);
		return "admin/survey";
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ChangeStatusForm {
		private List<SubmissionService.Status> statuses;
	}

}
