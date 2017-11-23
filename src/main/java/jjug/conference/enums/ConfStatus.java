package jjug.conference.enums;

import jjug.DisplayMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

@AllArgsConstructor
@Getter
public enum ConfStatus implements DisplayMessage {
	DRAFT_CFP(0, false, false, false), CFP(5, true, false, false), DRAFT_VOTE(10, false,
			false, false), VOTE(15, false, true, true), SELECTION(20, false, true,
					false), OPEN(25, false, true, false), CLOSED(30, false, true, false);
	private final int value;
	private final boolean openCfp;
	private final boolean submissionPublished;
	private final boolean openVote;

	public static ConfStatus valueOf(int v) {
		return Stream.of(values()).filter(x -> x.getValue() == v).findAny()
				.orElseThrow(() -> new IllegalArgumentException(v + " is illegal!"));
	}

	public boolean isFixedCfp() {
		return this.value >= SELECTION.getValue();
	}
}
