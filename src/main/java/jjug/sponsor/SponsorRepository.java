package jjug.sponsor;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface SponsorRepository extends CrudRepository<Sponsor, String> {
	Optional<Sponsor> findBySponsorId(String sponsorId);

	long countBySponsorId(String sponsorId);
}
