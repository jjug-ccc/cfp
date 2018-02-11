package jjug.sponsor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

public interface SponsorCredentialResetRepository
		extends CrudRepository<SponsorCredentialReset, UUID> {
	Optional<SponsorCredentialReset> findByResetId(UUID resetId);

	List<SponsorCredentialReset> findBySponsor_SponsorIdOrderByCreatedAtDesc(
			String sponsorId);
}
