package jjug.sponsor;

import javax.persistence.*;

import jjug.submission.Submission;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@DynamicUpdate
public class SponsoredSubmission {
	@EmbeddedId
	private SponsoredSubmissionId sponsoredSubmissionId;

	@ManyToOne
	@JoinColumn(name = "submission_id")
	@MapsId("submission")
	private Submission submission;

	@ManyToOne
	@JoinColumn(name = "sponsor_id")
	@MapsId("sponsorId")
	private Sponsor sponsor;
}
