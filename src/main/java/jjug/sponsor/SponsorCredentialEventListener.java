package jjug.sponsor;

import jjug.sponsor.event.SponsorCredentialResetEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class SponsorCredentialEventListener {
	private static final Logger log = LoggerFactory
			.getLogger(SponsorCredentialEventListener.class);
	private final SponsorCredentialResetRepository sponsorCredentialResetRepository;

	public SponsorCredentialEventListener(
			SponsorCredentialResetRepository sponsorCredentialResetRepository) {
		this.sponsorCredentialResetRepository = sponsorCredentialResetRepository;
	}

	@TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
	public void onPasswordReset(SponsorCredentialResetEvent event) {
		Sponsor sponsor = event.getCredential().getSponsor();
		SponsorCredentialReset reset = event.getReset();
		log.info("Credentials reset {}/{}", sponsor.getSponsorName(), reset.getResetId());
		this.sponsorCredentialResetRepository.delete(reset);
	}
}
