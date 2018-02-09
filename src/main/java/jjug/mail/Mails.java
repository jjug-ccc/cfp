package jjug.mail;

import java.util.stream.Collectors;

import jjug.speaker.Speaker;
import jjug.submission.Submission;

import org.springframework.mail.SimpleMailMessage;

public class Mails {
	private final String from;

	public Mails(String from) {
		this.from = from;
	}

	public static Mails from(String from) {
		return new Mails(from);
	}

	public SimpleMailMessage to(Submission submission) {
		String[] to = submission.getSpeakers().stream().map(Speaker::getEmail)
				.toArray(String[]::new);
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setFrom(this.from);
		message.setSubject(
				"[" + submission.getConference().getConfName() + "] CFPご応募ありがとうございます。");
		message.setText(submission.getSpeakers().stream() //
				.map(Speaker::getName) //
				.map(s -> s + "様") //
				.collect(Collectors.joining(",")) + "\n" //
				+ "CFPへご応募ありがとうございます。 \n" //
				+ "タイトル: " + submission.getTitle());
		return message;
	}
}
