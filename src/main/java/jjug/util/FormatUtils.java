package jjug.util;

import jjug.speaker.Speaker;

import java.util.List;
import java.util.stream.Collectors;

public class FormatUtils {

	public static String speakerName(List<Speaker> speakers) {
		return speakers.stream()
				.map(Speaker::getName)
				.collect(Collectors.joining("／"));
	}

	public static String speakerGithub(List<Speaker> speakers) {
		return speakers.stream()
				.map(Speaker::getGithub)
				.collect(Collectors.joining("／"));
	}

	public static String speakerEmail(List<Speaker> speakers) {
		return speakers.stream()
				.map(Speaker::getEmail)
				.collect(Collectors.joining("／"));
	}
}
