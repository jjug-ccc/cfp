package jjug.sponsor;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@DynamicUpdate
public class SponsorCredentialReset {
	@Id
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	@GeneratedValue(generator = "uuid")
	@Column(columnDefinition = "binary(16)")
	private UUID resetId;
	@NotNull
	private Instant createdAt;
	@OneToOne
	@NotNull
	@JoinColumn(name = "sponsor_id")
	@JsonIgnore
	private Sponsor sponsor;

	@JsonIgnore
	@Transient
	public boolean isValid() {
		Instant now = Instant.now();
		return now.isBefore(expiry());
	}

	public Instant expiry() {
		return this.createdAt.plus(14, ChronoUnit.DAYS);
	}
}
