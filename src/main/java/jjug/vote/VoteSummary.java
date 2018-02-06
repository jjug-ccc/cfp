package jjug.vote;

import jjug.submission.enums.Category;
import jjug.submission.enums.Level;
import jjug.submission.enums.SubmissionStatus;
import jjug.submission.enums.TalkType;

import java.time.Instant;
import java.util.UUID;

public interface VoteSummary {
	UUID getSubmissionId();

	String getTitle();

	Category getCategory();

	Level getLevel();

	TalkType getTalkType();

	SubmissionStatus getStatus();

	Instant getUpdatedAt();

	long getCount();
}
