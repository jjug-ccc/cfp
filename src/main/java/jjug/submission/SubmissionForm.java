package jjug.submission;

import jjug.submission.enums.Category;
import jjug.submission.enums.Language;
import jjug.submission.enums.Level;
import jjug.submission.enums.TalkType;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Deque;

@Data
public class SubmissionForm implements Serializable {
	@NotEmpty
	@Size(max = 255)
	private String title;
	@NotEmpty
	@Size(max = 5120)
	private String description;
	@NotEmpty
	@Size(max = 255)
	private String target;
	@NotNull
	private Category category;
	@NotNull
	private Level level;
	@NotNull
	private TalkType talkType;
	@NotNull
	private Language language;
	@Size(max = 5120)
	private String sessionNote;
	@Valid
	@NotEmpty
	Deque<SpeakerForm> speakerForms;
}
