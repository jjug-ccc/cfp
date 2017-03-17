package jjug.vote;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

@RepositoryRestResource
public interface VoteRepository extends Repository<Vote, UUID> {
	Vote save(Vote vote);

	@RestResource(exported = false)
	long countBySubmission_SubmissionIdAndGithub(@Param("submissionId") UUID submissionId,
			@Param("github") String github);

	@RestResource(exported = false)
	@Query("SELECT s.submissionId AS submissionId, s.title AS title, sp.name AS name, sp.github AS github, sp.email AS email, s.category AS category, s.level AS level, s.talkType AS talkType, s.submissionStatus AS status, count(v) AS count FROM Vote v RIGHT OUTER JOIN v.submission s JOIN s.speaker sp WHERE s.conference.confId = :confId AND s.submissionStatus IN (jjug.submission.enums.SubmissionStatus.SUBMITTED, jjug.submission.enums.SubmissionStatus.ACCEPTED, jjug.submission.enums.SubmissionStatus.SPONSORED, jjug.submission.enums.SubmissionStatus.REJECTED) GROUP BY s.submissionId ORDER BY count(v) DESC, s.createdAt ASC")
	List<VoteSummary> reportSummary(@Param("confId") UUID confId);
}
