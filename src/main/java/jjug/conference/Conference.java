package jjug.conference;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.NotEmpty;

import jjug.conference.enums.ConfStatus;
import jjug.submission.Submission;
import lombok.*;

@Getter
@Setter
@ToString(exclude = { "sessions" })
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
	@Size(max = 5012)
	private String confCfpNote;
	@Lob
	@NotEmpty
	@Size(max = 5012)
	private String confVoteNote;
	@NotNull
	private ConfStatus confStatus;
	@OneToMany(mappedBy = "conference", orphanRemoval = true, cascade = CascadeType.ALL)
	@OrderBy("submissionStatus ASC, createdAt ASC")
	private List<Submission> sessions;
}
