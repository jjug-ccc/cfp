package jjug.submission;

import java.util.UUID;

public interface SubmissionSurvey {
	UUID getSubmissionId();

	String getTitle();

	long getCount();
}
