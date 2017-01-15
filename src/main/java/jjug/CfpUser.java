package jjug;

import java.io.Serializable;

import lombok.*;

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
}