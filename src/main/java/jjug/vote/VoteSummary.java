package jjug.vote;

import java.time.Instant;
import java.util.UUID;

import jjug.submission.enums.*;

public interface VoteSummary {
	UUID getSubmissionId();

	String getTitle();

	Category getCategory();

	Level getLevel();

	TalkType getTalkType();

	SubmissionStatus getStatus();

	Language getLanguage();

	Instant getUpdatedAt();

	long getCount();
}
