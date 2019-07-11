package jjug.submission.enums;

import java.util.stream.Stream;

import jjug.DisplayMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TalkType implements DisplayMessage {
	STANDARD(0), SHORT(5), BEGINNER_STANDARD(6), BEGINNER_SHORT(7), WORKSHOP(10), STEPUP_STANDARD(15), STEPUP_SHORT(16), LT(20);
	private final int value;

	public static TalkType valueOf(int v) {
		return Stream.of(values()).filter(x -> x.getValue() == v).findAny()
				.orElseThrow(() -> new IllegalArgumentException(v + " is illegal!"));
	}
}
