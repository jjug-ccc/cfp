package jjug.attendee;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import jjug.conference.Conference;
import jjug.submission.Submission;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.Email;

@Getter
@Setter
@ToString(exclude = { "submissions", "conference" })
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@DynamicUpdate
public class Attendee implements Serializable {
	@Id
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	@GeneratedValue(generator = "uuid")
	@Column(columnDefinition = "binary(16)")
	private UUID attendeeId;

	@NotNull
	@Email
	private String email;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "conf_id")
	private Conference conference;

	@NotNull
	@ManyToMany(mappedBy = "attendees")
	private List<Submission> submissions;
}
