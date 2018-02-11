package jjug.sponsor;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface SponsorCredentialRepository
		extends CrudRepository<SponsorCredential, String> {
	Optional<SponsorCredential> findBySponsor_SponsorId(String sponsorId);
}
