package jjug.mail;

import java.util.stream.Collectors;

import jjug.attendee.Attendee;
import jjug.speaker.Speaker;
import jjug.submission.Submission;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public class Mails {
	private final String from;
	private String message;

	private Mails(String from) {
		this.from = from;
	}

	public static Mails from(String from) {
		return new Mails(from);
	}

	public Mails message(String message) {
		this.message = message;
		return this;
	}

	public SimpleMailMessage to(Submission submission) {
		String[] to = submission.getSpeakers().stream().map(Speaker::getEmail)
				.toArray(String[]::new);
		SimpleMailMessage message = new SimpleMailMessage();
		String editUrl = ServletUriComponentsBuilder
				.fromCurrentContextPath().pathSegment("submissions",
						submission.getSubmissionId().toString(), "form")
				.build().toUriString();
		message.setTo(to);
		message.setFrom(this.from);
		message.setSubject(
				"[" + submission.getConference().getConfName() + "] " + this.message);
		message.setText(submission.getSpeakers().stream() //
				.map(Speaker::getName) //
				.map(s -> s + "様") //
				.collect(Collectors.joining(", ")) + "\n" //
				+ this.message + " \n" //
				+ "タイトル: " + submission.getTitle() + " \n" //
				+ "\n" //
				+ "応募内容は下記URLより変更可能です。" //
				+ "\n" //
				+ editUrl);
		return message;
	}

	public SimpleMailMessage attendee2018Spring(Attendee attendee) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(attendee.getEmail());
		message.setFrom(this.from);
		message.setSubject(
				"[" + attendee.getConference().getConfName() + "] " + this.message);
		String url = "https://jjug-ccc-2018-spring-survey.cfapps.io/#/sessions?id="
				+ attendee.getAttendeeId();
		message.setText(this.message + " \n" //
				+ "\n" //
				+ "回答内容は下記URLより変更可能です。" //
				+ "\n" //
				+ url);
		return message;
	}
}
