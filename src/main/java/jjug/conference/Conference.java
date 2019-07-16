package jjug.conference;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jjug.CfpUser;
import jjug.conference.enums.ConfStatus;
import jjug.sponsor.Sponsor;
import jjug.sponsor.SponsorUser;
import jjug.submission.Submission;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.NotEmpty;

@Getter
@Setter
@ToString(exclude = { "sessions", "sponsors" })
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@DynamicUpdate
public class Conference implements Serializable {
	@Id
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	@GeneratedValue(generator = "uuid")
	@Column(columnDefinition = "binary(16)")
	private UUID confId;
	@NotEmpty
	@Size(max = 255)
	private String confName;
	@NotNull
	private LocalDate confDate;
	@Lob
	@NotEmpty
	@Size(max = 20480)
	private String confCfpNote;
	@Lob
	@NotEmpty
	@Size(max = 20480)
	private String confVoteNote;
	@NotNull
	private ConfStatus confStatus;
	@OneToMany(mappedBy = "conference", orphanRemoval = true, cascade = CascadeType.ALL)
	@OrderBy("submissionStatus ASC, createdAt ASC")
	@JsonIgnore
	private List<Submission> sessions;
	@OneToMany(mappedBy = "conference", orphanRemoval = true, cascade = CascadeType.ALL)
	@OrderBy("sponsorType ASC, sponsorName ASC")
	@JsonIgnore
	private List<Sponsor> sponsors;

	@JsonIgnore
	@Transient
	public boolean isOpenCfpFor(CfpUser cfpUser) {
		if (cfpUser instanceof SponsorUser) {
			return !this.confStatus.isClosed();
		}
		return this.confStatus.isOpenCfp();
	}

	@JsonIgnore
	@Transient
	public boolean isFixedCfpFor(CfpUser cfpUser) {
		if (cfpUser instanceof SponsorUser) {
			return !this.confStatus.isClosed();
		}
		return this.confStatus.isFixedCfp();
	}

	@JsonIgnore
	public boolean isInSelection() {
		return this.confStatus.isInSelection();
	}
}
