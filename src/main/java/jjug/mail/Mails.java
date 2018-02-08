package jjug.mail;

import java.util.stream.Collectors;

import jjug.speaker.Speaker;
import jjug.submission.Submission;

import org.springframework.mail.SimpleMailMessage;

public class Mails {
	private final SimpleMailMessage message;

	private Mails(SimpleMailMessage message) {
		this.message = message;
	}

	public static Mails from(Submission submission) {
		String[] to = submission.getSpeakers().stream().map(Speaker::getEmail)
				.toArray(String[]::new);
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setFrom("office@java-users.jp");
		message.setSubject(
				"[" + submission.getConference().getConfName() + "] CFPご応募ありがとうございます。");
		message.setText(submission.getSpeakers().stream() //
				.map(Speaker::getName) //
				.map(s -> s + "様") //
				.collect(Collectors.joining(",")) + "\n" //
				+ "CFPへご応募ありがとうございます。 \n" //
				+ "タイトル: " + submission.getTitle());
		return new Mails(message);
	}

	public SimpleMailMessage to() {
		return this.message;
	}
}
