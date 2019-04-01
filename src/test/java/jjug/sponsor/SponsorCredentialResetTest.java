package jjug.sponsor;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SponsorCredentialResetTest {

	@Test
	public void isValid() {
		Instant createdAt = Instant.now().minus(3, ChronoUnit.DAYS).plus(1,
				ChronoUnit.MINUTES);
		SponsorCredentialReset reset = SponsorCredentialReset.builder()
				.createdAt(createdAt).build();
		assertThat(reset.isValid()).isTrue();
	}

	@Test
	public void isInValid() {
		Instant createdAt = Instant.now().minus(30, ChronoUnit.DAYS).minus(1,
				ChronoUnit.MINUTES);
		SponsorCredentialReset reset = SponsorCredentialReset.builder()
				.createdAt(createdAt).build();
		assertThat(reset.isValid()).isFalse();
	}
}
