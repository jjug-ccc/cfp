package jjug.sponsor;

import java.util.List;
import java.util.Objects;

import jjug.CfpUser;
import jjug.submission.Submission;
import jjug.submission.enums.SubmissionStatus;

// This class extends CfpUser in order to leverage `@AuthenticationPrincipal CfpUser`.
// This is not good design :(
public class SponsorUser extends CfpUser {
	private final Sponsor sponsor;

	public SponsorUser(Sponsor sponsor) {
		super(sponsor.getSponsorName(), "", "", "");
		this.sponsor = sponsor;

		List<SponsoredSubmission> sponsoredSubmissions = sponsor
				.getSponsoredSubmissions();
		if (sponsoredSubmissions != null) {
			sponsoredSubmissions.forEach(SponsoredSubmission::getSubmission); // load
																				// explicitly
		}
	}

	public Sponsor getSponsor() {
		return sponsor;
	}

	@Override
	public boolean isPublishedUser(Submission submission) {
		return this.sponsor.getSponsoredSubmissions().stream()
				.map(SponsoredSubmission::getSubmission) //
				.anyMatch(s -> Objects.equals(s.getSubmissionId(),
						submission.getSubmissionId()));
	}

	@Override
	public SubmissionStatus submittedStatus() {
		return SubmissionStatus.SPONSORED;
	}
}
