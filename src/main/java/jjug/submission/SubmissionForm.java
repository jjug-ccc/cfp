package jjug.submission;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;

import jjug.submission.enums.Category;
import jjug.submission.enums.Language;
import jjug.submission.enums.Level;
import jjug.submission.enums.TalkType;
import lombok.Data;

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
	@NotEmpty
	@Size(max = 255)
	private String name;
	@NotEmpty
	@Size(max = 255)
	private String companyOrCommunity;
	@NotEmpty
	@Size(max = 5120)
	private String bio;
	@NotEmpty
	@Size(max = 5120)
	private String activities;
	@NotEmpty
	@Size(max = 255)
	@URL
	private String profileUrl;
	@NotEmpty
	@Email
	@Size(max = 255)
	private String email;
	private boolean transportationAllowance = false;
	@Size(max = 255)
	private String city;
	@Size(max = 5120)
	private String note;
}
