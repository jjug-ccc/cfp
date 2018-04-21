package jjug.attendee;

import jjug.attendee.event.AttendeeRegisteredEvent;
import jjug.attendee.event.AttendeeUpdatedEvent;
import jjug.mail.MailService;
import jjug.mail.Mails;
import jjug.slack.SlackNotifier;
import jjug.slack.SlackWebhookPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class AttendeeEventHandler {
	private final MailService mailService;
	private final SlackNotifier slackNotifier;
	private static final Logger log = LoggerFactory.getLogger(AttendeeEventHandler.class);

	public AttendeeEventHandler(MailService mailService, SlackNotifier slackNotifier) {
		this.mailService = mailService;
		this.slackNotifier = slackNotifier;
	}

	@TransactionalEventListener
	public void onAttendeeRegistered(AttendeeRegisteredEvent event) {
		Attendee attendee = event.attendee();
		log.info("Registered {}", attendee);
		SimpleMailMessage message = Mails.from("office@java-users.jp") //
				.message("アンケート回答ありがとうございます。") //
				.attendee2018Spring(attendee);
		this.mailService.sendMail(message);
		SlackWebhookPayload payload = SlackWebhookPayload.builder() //
				.text(String.format("%sさんからアンケート回答がありました", attendee.getEmail())) //
				.build();
		this.slackNotifier.notify(payload);
	}

	@TransactionalEventListener
	public void onAttendeeUpdated(AttendeeUpdatedEvent event) {
		Attendee attendee = event.attendee();
		log.info("Updated {}", attendee);
		SimpleMailMessage message = Mails.from("office@java-users.jp") //
				.message("アンケート回答内容が変更されました。") //
				.attendee2018Spring(attendee);
		this.mailService.sendMail(message);
	}
}
