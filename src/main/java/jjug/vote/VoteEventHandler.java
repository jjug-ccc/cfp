package jjug.vote;

import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import jjug.CfpUser;
import jjug.submission.Submission;
import jjug.submission.SubmissionRepository;
import lombok.RequiredArgsConstructor;

@Component
@RepositoryEventHandler(Vote.class)
@RequiredArgsConstructor
public class VoteEventHandler {
	private final SubmissionRepository submissionRepository;
	private final VoteRepository voteRepository;

	@HandleBeforeCreate
	@HandleBeforeSave
	public void before(Vote vote) {
		Submission submission = submissionRepository
				.findOne(vote.getSubmission().getSubmissionId()).get();
		if (!submission.getConference().getConfStatus().isOpenVote()) {
			throw new VoteClosedException();
		}
		CfpUser user = (CfpUser) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		if (voteRepository.countBySubmission_SubmissionIdAndGithub(
				submission.getSubmissionId(), user.getGithub()) > 0) {
			throw new AlreadyVotedException();
		}
		vote.setGithub(user.getGithub());
	}
}
