package jjug;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import jjug.speaker.Speaker;
import jjug.submission.Submission;
import jjug.submission.enums.SubmissionStatus;
import lombok.*;

import org.springframework.util.CollectionUtils;

@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
@Builder
public class CfpUser implements Serializable {
	private final String name;
	private final String github;
	private final String email;
	private final String avatarUrl;

	public boolean isPublishedUser(Submission submission) {
		List<Speaker> speakers = submission.getSpeakers();
		if (CollectionUtils.isEmpty(speakers)) {
			return false;
		}
		return speakers.stream()
				.anyMatch(speaker -> Objects.equals(speaker.getGithub(), getGithub()));
	}

	public SubmissionStatus submittedStatus() {
		return SubmissionStatus.SUBMITTED;
	}
}
