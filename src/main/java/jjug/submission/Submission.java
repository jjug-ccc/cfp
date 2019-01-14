package jjug.submission;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jjug.attendee.Attendee;
import jjug.conference.Conference;
import jjug.speaker.Speaker;
import jjug.speaker.Speakers;
import jjug.submission.enums.*;
import jjug.submission.event.SubmissionCreatedEvent;
import jjug.submission.event.SubmissionUpdatedBySpeakerEvent;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.domain.AbstractAggregateRoot;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@ToString(exclude = { "conference", "attendees" })
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@DynamicUpdate
public class Submission extends AbstractAggregateRoot implements Serializable {
	@Id
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
	@JoinTable(name = "submission_speaker", joinColumns = @JoinColumn(name = "submission_id"), inverseJoinColumns = @JoinColumn(name = "speaker_id"))
	@OrderBy("email ASC")
	private List<Speaker> speakers;
	@NotNull
	private Category category;
	@NotNull
	private Level level;
	@NotNull
	private TalkType talkType;
	@NotNull
	private Language language;
	@JsonIgnore
	@Lob
	@Size(max = 5120)
	private String sessionNote;
	@NotNull
	@JsonIgnore
	private SubmissionStatus submissionStatus;
	@ManyToOne
	@JoinColumn(name = "conference_id")
	@NotNull
	@JsonIgnore
	private Conference conference;
	@ManyToMany(cascade = { CascadeType.ALL })
	@JoinTable(name = "submission_attendee", joinColumns = @JoinColumn(name = "submission_id"), inverseJoinColumns = @JoinColumn(name = "attendee_id"))
	@JsonIgnore
	public Set<Attendee> attendees;
	@Column(insertable = false, updatable = false)
	private Instant createdAt;
	@Column(insertable = false, updatable = false)
	private Instant updatedAt;

	public Speakers speakers() {
		return new Speakers(this.speakers);
	}

	public Submission created() {
		super.registerEvent(new SubmissionCreatedEvent(this));
		return this;
	}

	public Submission updatedBySpeaker() {
		super.registerEvent(new SubmissionUpdatedBySpeakerEvent(this));
		return this;
	}

	@JsonIgnore
	public boolean isInSelection() {
		if (this.submissionStatus == SubmissionStatus.SPONSORED) {
			return false;
		}
		return this.conference.isInSelection();
	}
}
