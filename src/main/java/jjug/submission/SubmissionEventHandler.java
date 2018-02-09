package jjug.submission;

import jjug.mail.MailService;
import jjug.mail.Mails;
import jjug.submission.event.SubmissionCreatedEvent;
import jjug.submission.event.SubmissionUpdatedBySpeakerEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class SubmissionEventHandler {
	private static final Logger log = LoggerFactory
			.getLogger(SubmissionEventHandler.class);
	private final MailService mailService;

	public SubmissionEventHandler(MailService mailService) {
		this.mailService = mailService;
	}

	@TransactionalEventListener
	public void onCreated(SubmissionCreatedEvent event) {
		Submission submission = event.submission();
		log.info("Created {}({})", submission.getTitle(), submission.getSubmissionId());
		SimpleMailMessage mailMessage = Mails.from("office@java-users.jp") //
				.message("Call for Paperへのご応募ありがとうございます。") //
				.to(submission);
		this.mailService.sendMail(mailMessage);
	}

	@TransactionalEventListener
	public void onUpdatedBySpeaker(SubmissionUpdatedBySpeakerEvent event) {
		Submission submission = event.submission();
		log.info("Updated by speaker {}({})", submission.getTitle(),
				submission.getSubmissionId());
		SimpleMailMessage mailMessage = Mails.from("office@java-users.jp") //
				.message("Call for Paperが変更されました。") //
				.to(submission);
        this.mailService.sendMail(mailMessage);
    }
}
