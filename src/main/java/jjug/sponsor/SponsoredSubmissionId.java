package jjug.sponsor;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Embeddable
public class SponsoredSubmissionId implements Serializable {
	@Column(name = "sponsor_id")
	private String sponsorId;
	@Column(name = "submission_id")
	private UUID submissionId;
}
