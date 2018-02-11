package jjug.sponsor;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import jjug.sponsor.event.SponsorCredentialResetEvent;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.NotEmpty;

import org.springframework.data.domain.AbstractAggregateRoot;

@Getter
@Setter
@EqualsAndHashCode(exclude = "sponsor")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@DynamicUpdate
@ToString(exclude = "sponsor")
public class SponsorCredential extends AbstractAggregateRoot implements Serializable {
	@Id
	@NotNull
	@Size(max = 255)
	private String sponsorId;
	@NotEmpty
	@Size(max = 255)
	private String credential;
	@OneToOne
	@NotNull
	@JoinColumn(name = "sponsor_id")
	private Sponsor sponsor;

	public SponsorCredential reset(SponsorCredentialReset reset) {
		super.registerEvent(new SponsorCredentialResetEvent(this, reset));
		return this;
	}
}
