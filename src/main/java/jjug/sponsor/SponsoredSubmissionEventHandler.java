package jjug.sponsor;

import java.util.Optional;

import jjug.submission.Submission;
import jjug.submission.event.SubmissionCreatedEvent;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class SponsoredSubmissionEventHandler {
	private final SponsoredSubmissionRepository sponsoredSubmissionRepository;
	private final SponsorRepository sponsorRepository;

	public SponsoredSubmissionEventHandler(
			SponsoredSubmissionRepository sponsoredSubmissionRepository,
			SponsorRepository sponsorRepository) {
		this.sponsoredSubmissionRepository = sponsoredSubmissionRepository;
		this.sponsorRepository = sponsorRepository;
	}

	@TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
	public void beforeCommitOnCreateSubmission(SubmissionCreatedEvent event) {

		sponsor().ifPresent(sponsor -> {
			Submission submission = event.submission();
			SponsoredSubmission sponsoredSubmission = SponsoredSubmission.builder()
					.sponsoredSubmissionId(SponsoredSubmissionId.builder()
							.submissionId(submission.getSubmissionId()) //
							.sponsorId(sponsor.getSponsorId()) //
							.build()) //
					.sponsor(sponsor) //
					.submission(submission) //
					.build();
			this.sponsoredSubmissionRepository.save(sponsoredSubmission);
		});
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void afterCommitOnCreateSubmission(SubmissionCreatedEvent event) {
		sponsor().map(Sponsor::getSponsorId)
				.flatMap(this.sponsorRepository::findBySponsorId).ifPresent(sponsor -> {
					UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
							new SponsorUser(sponsor), "",
							AuthorityUtils.createAuthorityList("ROLE_SPONSOR"));
					// Reset Authentication
					SecurityContextHolder.getContext().setAuthentication(token);
				});
	}

	Optional<Sponsor> sponsor() {
		Authentication authentication = SecurityContextHolder.getContext()
				.getAuthentication();
		if (authentication == null) {
			return Optional.empty();
		}

		Object principal = authentication.getPrincipal();
		if (!(principal instanceof SponsorUser)) {
			return Optional.empty();
		}
		Sponsor sponsor = ((SponsorUser) principal).getSponsor();
		return Optional.of(sponsor);
	}
}