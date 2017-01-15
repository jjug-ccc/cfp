package jjug.submission.enums;

import java.util.stream.Stream;

import jjug.DisplayMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Language implements DisplayMessage {
	JAPANESE(0), ENGLISH(5), OTHER(9999);
	private final int value;

	public static Language valueOf(int v) {
		return Stream.of(values()).filter(x -> x.getValue() == v).findAny()
				.orElseThrow(() -> new IllegalArgumentException(v + " is illegal!"));
	}
}
