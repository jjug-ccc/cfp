package jjug.sponsor;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jjug.conference.Conference;
import jjug.sponsor.enums.SponsorType;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.NotEmpty;

@Getter
@Setter
@EqualsAndHashCode(exclude = { "conference", "credential", "sponsoredSubmissions" })
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@DynamicUpdate
@ToString(exclude = { "conference", "credential", "sponsoredSubmissions" })
public class Sponsor implements Serializable {
	@Id
	@Size(max = 255)
	private String sponsorId;
	@NotEmpty
	@Size(max = 255)
	private String sponsorName;
	@NotNull
	private SponsorType sponsorType;
	@NotNull
	@Size(max = 255)
	private String sponsorLogoUrl;
	@NotNull
	@Size(max = 255)
	private String sponsorUrl;
	@ManyToOne
	@JoinColumn(name = "conference_id")
	@NotNull
	@JsonIgnore
	private Conference conference;
	@OneToOne(mappedBy = "sponsor")
	@JsonIgnore
	private SponsorCredential credential;
	@OneToMany(mappedBy = "sponsor")
	@JsonIgnore
	private List<SponsoredSubmission> sponsoredSubmissions;
}
