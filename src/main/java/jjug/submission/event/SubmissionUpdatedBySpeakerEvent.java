package jjug.submission.event;

import java.io.Serializable;

import jjug.submission.Submission;

public class SubmissionUpdatedBySpeakerEvent implements Serializable {
	private final Submission submission;

	public SubmissionUpdatedBySpeakerEvent(Submission submission) {
		this.submission = submission;
	}

	public Submission submission() {
		return submission;
	}
}
