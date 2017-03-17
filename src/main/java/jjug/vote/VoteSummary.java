package jjug.vote;

import java.util.UUID;

import jjug.submission.enums.Category;
import jjug.submission.enums.Level;
import jjug.submission.enums.TalkType;

public interface VoteSummary {
	UUID getSubmissionId();

	String getTitle();

	String getName();

	String getGithub();

	String getEmail();

	Category getCategory();

	Level getLevel();

	TalkType getTalkType();

	long getCount();
}
