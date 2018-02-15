package jjug.speaker;

import jjug.speaker.enums.ActivityType;
import lombok.*;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Embeddable
public class Activity {
	@NotNull
	private ActivityType activityType;
	@NotEmpty
	@Size(max = 255)
	private String url;
}
