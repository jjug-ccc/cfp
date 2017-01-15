package jjug.submission.enums;

import java.util.stream.Stream;

import jjug.DisplayMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Category implements DisplayMessage {
	JAVA_SE(1), SERVER_SIDE_JAVA(5), JVM(10), EMBEDDED(15), ALTERNATE_LANGUAGES(
			20), DEV_OPS(25), CLOUD(30), MOBILE(35), OTHERS(9999);
	private final int value;

	public static Category valueOf(int v) {
		return Stream.of(values()).filter(x -> x.getValue() == v).findAny()
				.orElseThrow(() -> new IllegalArgumentException(v + " is illegal!"));
	}
}
