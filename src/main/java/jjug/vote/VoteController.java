package jjug.vote;

import static jjug.submission.enums.SubmissionStatus.SUBMITTED;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import jjug.conference.Conference;
import jjug.conference.ConferenceRepository;
import jjug.submission.Submission;
import jjug.submission.SubmissionRepository;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class VoteController {
	private final ConferenceRepository conferenceRepository;
	private final SubmissionRepository submissionRepository;

	@GetMapping("conferences/{confId}/votes")
	String submissions(@PathVariable UUID confId, Model model) {
		Conference conference = conferenceRepository.findOne(confId).get();
		checkIfVoteIsOpen(conference);
		List<Submission> submissions = conference.getSessions().stream()
				.filter(s -> s.getSubmissionStatus() == SUBMITTED)
				.collect(Collectors.toList());
		model.addAttribute("conference", conference);
		model.addAttribute("submissions", submissions);
		return "vote/votes";
	}

	void checkIfVoteIsOpen(Conference conference) {
		if (!conference.getConfStatus().isOpenVote()) {
			throw new VoteClosedException();
		}
	}
}
