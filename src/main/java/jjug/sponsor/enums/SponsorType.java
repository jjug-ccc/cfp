package jjug.sponsor.enums;

import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SponsorType {
	DIAMOND(0, true), PLATINUM(5, true), GOLD(10, true), BOOTH(15, false), BEER(20,
			false), COFFEE(25, false), SUSHI(30, false), DRINK(35, false), LUNCH(40, false),
	MANAGEMENT(45, false);

	private final int value;
	private final boolean canSubmit;

	boolean canSubmit() {
		return this.canSubmit;
	}

	public static SponsorType valueOf(int v) {
		return Stream.of(values()).filter(x -> x.getValue() == v).findAny()
				.orElseThrow(() -> new IllegalArgumentException(v + " is illegal!"));
	}
}
