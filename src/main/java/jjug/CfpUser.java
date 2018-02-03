package jjug;

import jjug.speaker.Speaker;
import lombok.*;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

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

	public boolean isPublishedUser(List<Speaker> speakers) {
		if (CollectionUtils.isEmpty(speakers)) {
			return false;
		}
		return speakers.stream()
				.anyMatch(speaker -> Objects.equals(speaker.getGithub(), getGithub()));
	}
}
