package jjug.submission;

import jjug.CfpUser;
import jjug.vote.VoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Slf4j
public class SubmissionViewController {
	private final SubmissionRepository submissionRepository;
	private final VoteRepository voteRepository;

	@GetMapping("submissions/{submissionId}")
	String view(@PathVariable UUID submissionId, Model model,
			@AuthenticationPrincipal CfpUser user) {
		Submission submission = submissionRepository.findOne(submissionId).get();
		if (!submission.getSubmissionStatus().isPublished()
				|| !submission.getConference().getConfStatus().isSubmissionPublished()) {
			throw new UnpublishedSubmissionException();
		}
		model.addAttribute("voted", isVoted(submissionId, user));
		model.addAttribute("submission", submission);
		return "submission/submissionView";
	}

	@GetMapping("submissions/{submissionId}/preview")
	String preview(@PathVariable UUID submissionId, Model model,
			@AuthenticationPrincipal CfpUser user) {
		Submission submission = submissionRepository.findOne(submissionId).get();
		if (!user.isPublishedUser(submission.getSpeakers())) {
			throw new UnpublishedSubmissionException();
		}
		model.addAttribute("voted", isVoted(submissionId, user));
		model.addAttribute("submission", submission);
		return "submission/submissionView";
	}

	@GetMapping("admin/submissions/{submissionId}")
	String adminView(@PathVariable UUID submissionId, Model model,
			@AuthenticationPrincipal CfpUser user) {
		Submission submission = submissionRepository.findOne(submissionId).get();
		model.addAttribute("voted", isVoted(submissionId, user));
		model.addAttribute("submission", submission);
		return "submission/submissionView";
	}

	boolean isVoted(UUID submissionId, CfpUser user) {
		if (user == null) {
			return false;
		}
		return voteRepository.countBySubmission_SubmissionIdAndGithub(submissionId,
				user.getGithub()) > 0;
	}
}
