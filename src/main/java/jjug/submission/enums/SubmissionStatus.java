package jjug.submission.enums;

import java.util.stream.Stream;

import jjug.DisplayMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SubmissionStatus implements DisplayMessage {
	DRAFT(0, false), SUBMITTED(5, true), ACCEPTED(10, true), SPONSORED(15,
			true), REJECTED(20, true), WITHDRAWN(25, false);
	private final int value;
	private final boolean published;

	public static SubmissionStatus valueOf(int v) {
		return Stream.of(values()).filter(x -> x.getValue() == v).findAny()
				.orElseThrow(() -> new IllegalArgumentException(v + " is illegal!"));
	}
}
