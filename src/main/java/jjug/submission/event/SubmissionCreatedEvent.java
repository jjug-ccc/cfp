package jjug.submission.event;

import java.io.Serializable;

import jjug.submission.Submission;

public class SubmissionCreatedEvent implements Serializable {
	private final Submission submission;

	public SubmissionCreatedEvent(Submission submission) {
		this.submission = submission;
	}

	public Submission submission() {
		return submission;
	}
}
