package jjug.conference;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import jjug.conference.enums.ConfStatus;
import jjug.submission.Submission;
import jjug.submission.SubmissionRepository;
import jjug.submission.enums.SubmissionStatus;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ConferenceController {
	private final ConferenceRepository conferenceRepository;
	private final SubmissionRepository submissionRepository;

	@GetMapping("conferences/{confId}")
	String conference(@PathVariable UUID confId, Model model) {
		Conference conference = conferenceRepository.findOne(confId).get();
		if (!(conference.getConfStatus() == ConfStatus.OPEN
				|| conference.getConfStatus() == ConfStatus.CLOSED)) {
			throw new ConferenceClosedException();
		}
		List<Submission> submissions = submissionRepository
				.findByConference_ConfIdAndSubmissionStatus(confId,
						SubmissionStatus.ACCEPTED);
		model.addAttribute("conference", conference);
		model.addAttribute("submissions", submissions);
		return "conference/accepted";
	}

}
