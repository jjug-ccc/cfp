package jjug.vote;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.*;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import jjug.submission.Submission;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@DynamicUpdate
public class Vote implements Serializable {
	@Id
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	@GeneratedValue(generator = "uuid")
	@Column(columnDefinition = "binary(16)")
	private UUID voteId;
	@ManyToOne
	@JoinColumn(name = "submission_id")
	private Submission submission;
	private String github;
}
