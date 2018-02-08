package jjug.submission;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jjug.conference.Conference;
import jjug.speaker.Speaker;
import jjug.speaker.Speakers;
import jjug.submission.enums.*;
import lombok.*;
import org.hibernate.annotations.*;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.CascadeType;
import javax.persistence.*;
import javax.persistence.Entity;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.*;

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
	@ManyToMany(cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.DETACH,
			CascadeType.REFRESH })
	@NotNull
	@JoinTable(
			name = "submission_speaker",
			joinColumns = @JoinColumn(name = "submission_id"),
			inverseJoinColumns = @JoinColumn(name = "speaker_id")
	)
	private List<Speaker> speakers;
	@NotNull
	private Category category;
	@NotNull
	private Level level;
	@NotNull
	private TalkType talkType;
	@NotNull
	private Language language;
	@NotNull
	@JsonIgnore
	private SubmissionStatus submissionStatus;
	@ManyToOne
	@JoinColumn(name = "conference_id")
	@NotNull
	@JsonIgnore
	private Conference conference;
	@Column(insertable = false, updatable = false)
	private Instant createdAt;
	@Column(insertable = false, updatable = false)
	private Instant updatedAt;

	public Speakers speakers() {
		return new Speakers(this.speakers);
	}
}
