package jjug.mail;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class MailService {
	private final MailSender mailSender;

	public MailService(MailSender mailSender) {
		this.mailSender = mailSender;
	}

	@Async
	public void sendMail(SimpleMailMessage... messages) {
		mailSender.send(messages);
	}
}
