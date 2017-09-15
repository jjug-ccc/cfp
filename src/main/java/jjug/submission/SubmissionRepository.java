package jjug.submission;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import jjug.submission.enums.SubmissionStatus;

public interface SubmissionRepository extends Repository<Submission, UUID> {
	Optional<Submission> findOne(UUID id);

	Submission save(Submission submission);

	List<Submission> findBySpeaker_GithubOrderByConference_ConfDateDescSubmissionStatusAscCreatedAtAsc(
			@Param("github") String github);

	@Query("SELECT x FROM Submission x JOIN FETCH x.speaker WHERE x.submissionId IN (:ids)")
	List<Submission> findAll(@Param("ids") List<UUID> ids);

	List<Submission> findByConference_ConfIdAndSubmissionStatus(UUID confId,
			SubmissionStatus status);

	@Query(value = "SELECT x FROM Submission x JOIN FETCH x.speaker WHERE x.conference.confId = :confId AND x.submissionStatus = jjug.submission.enums.SubmissionStatus.ACCEPTED")
	List<Submission> findAllAcceptedByConference(@Param("confId") UUID confId);

	@Query(value = "SELECT x FROM Submission x JOIN FETCH x.speaker WHERE x.conference.confId = :confId AND x.submissionId = :submissionId AND x.submissionStatus = jjug.submission.enums.SubmissionStatus.ACCEPTED")
	Optional<Submission> findAcceptedByConference(
			@Param("submissionId") UUID submissionId, @Param("confId") UUID confId);
}
