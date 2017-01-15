package jjug.submission.enums;

import java.util.stream.Stream;

import jjug.DisplayMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Level implements DisplayMessage {
	BEGINNER(0), INTERMEDIATE(5), ADVANCED(10);
	private final int value;

	public static Level valueOf(int v) {
		return Stream.of(values()).filter(x -> x.getValue() == v).findAny()
				.orElseThrow(() -> new IllegalArgumentException(v + " is illegal!"));
	}
}
