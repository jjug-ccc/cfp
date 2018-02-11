package jjug.sponsor.event;

import java.io.Serializable;

import jjug.sponsor.SponsorCredential;
import jjug.sponsor.SponsorCredentialReset;

public class SponsorCredentialResetEvent implements Serializable {
	private final SponsorCredential credential;
	private final SponsorCredentialReset reset;

	public SponsorCredentialResetEvent(SponsorCredential credential,
			SponsorCredentialReset reset) {
		this.credential = credential;
		this.reset = reset;
	}

	public SponsorCredential getCredential() {
		return credential;
	}

	public SponsorCredentialReset getReset() {
		return reset;
	}
}
