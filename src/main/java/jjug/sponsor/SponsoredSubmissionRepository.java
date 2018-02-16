package jjug.sponsor;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface SponsoredSubmissionRepository
		extends CrudRepository<SponsoredSubmission, SponsoredSubmissionId> {
	List<SponsoredSubmission> findBySponsor_SponsorId(String sponsorId);
}
