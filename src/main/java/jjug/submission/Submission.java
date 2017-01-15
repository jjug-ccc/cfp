package jjug.submission;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.NotEmpty;

import jjug.conference.Conference;
import jjug.speaker.Speaker;
import jjug.submission.enums.*;
import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@DynamicUpdate
public class Submission implements Serializable {
	@Id
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	@GeneratedValue(generator = "uuid")
	@Column(columnDefinition = "binary(16)")
	private UUID submissionId;
	@NotEmpty
	@Size(max = 255)
	private String title;
	@Lob
	@NotEmpty
	@Size(max = 5120)
	private String description;
	@NotEmpty
	@Size(max = 255)
	private String target;
	@ManyToOne(cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.DETACH,
			CascadeType.REFRESH })
	@NotNull
	@JoinColumn(name = "speaker_id")
	private Speaker speaker;
	@NotNull
	private Category category;
	@NotNull
	private Level level;
	@NotNull
	private TalkType talkType;
	@NotNull
	private Language language;
	@NotNull
	private SubmissionStatus submissionStatus;
	@ManyToOne
	@JoinColumn(name = "conference_id")
	@NotNull
	private Conference conference;
	@Column(insertable = false, updatable = false)
	private Instant createdAt;
	@Column(insertable = false, updatable = false)
	private Instant updatedAt;
}
