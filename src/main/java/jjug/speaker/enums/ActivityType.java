package jjug.speaker.enums;

import jjug.DisplayMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

@AllArgsConstructor
@Getter
public enum ActivityType implements DisplayMessage {
	TWITTER(1), FACEBOOK(5), SLIDE_SHARE(10), GITHUB(15), OTHER(999);
	private final int value;

	public static ActivityType valueOf(int v) {
		return Stream.of(values()).filter(x -> x.getValue() == v).findAny()
				.orElseThrow(() -> new IllegalArgumentException(v + " is illegal!"));
	}
}
