package jjug.submission;

import jjug.speaker.enums.ActivityType;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
public class ActivityForm implements Serializable {
	private ActivityType activityType;
	@Size(max = 255)
	@URL
	private String url;
}
