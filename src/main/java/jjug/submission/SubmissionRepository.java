package jjug.submission;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jjug.submission.enums.SubmissionStatus;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface SubmissionRepository extends Repository<Submission, UUID> {
	Optional<Submission> findOne(UUID id);

	Submission save(Submission submission);

	List<Submission> findBySpeakers_GithubOrderByConference_ConfDateDescSubmissionStatusAscCreatedAtAsc(
			@Param("github") String github);

	@Query("SELECT x FROM Submission x JOIN FETCH x.speakers WHERE x.submissionId IN (:ids)")
	List<Submission> findAll(@Param("ids") List<UUID> ids);

	List<Submission> findByConference_ConfIdAndSubmissionStatus(UUID confId,
			SubmissionStatus status);

	@Query(value = "SELECT DISTINCT x FROM Submission x JOIN FETCH x.speakers WHERE x.conference.confId = :confId AND x.submissionStatus IN (jjug.submission.enums.SubmissionStatus.ACCEPTED, jjug.submission.enums.SubmissionStatus.SPONSORED) AND x.talkType <> jjug.submission.enums.TalkType.LT")
	List<Submission> findAllAcceptedByConference(@Param("confId") UUID confId);

	@Query(value = "SELECT x FROM Submission x JOIN FETCH x.speakers WHERE x.conference.confId = :confId AND x.submissionId = :submissionId AND x.submissionStatus IN (jjug.submission.enums.SubmissionStatus.ACCEPTED, jjug.submission.enums.SubmissionStatus.SPONSORED)  AND x.talkType <> jjug.submission.enums.TalkType.LT")
	Optional<Submission> findAcceptedByConference(
			@Param("submissionId") UUID submissionId, @Param("confId") UUID confId);

	@Query(value = "SELECT s.submissionId AS submissionId, s.title AS title, count(a) AS count FROM Submission s LEFT JOIN s.attendees a WHERE s.conference.confId = :confId AND s.submissionStatus IN (jjug.submission.enums.SubmissionStatus.ACCEPTED, jjug.submission.enums.SubmissionStatus.SPONSORED)  AND s.talkType <> jjug.submission.enums.TalkType.LT GROUP BY s.submissionId, s.title ORDER BY count(a) DESC")
	List<SubmissionSurvey> reportSurvey(@Param("confId") UUID confId);
}
