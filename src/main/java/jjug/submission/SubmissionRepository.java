package jjug.submission;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface SubmissionRepository extends Repository<Submission, UUID> {
	Optional<Submission> findOne(UUID id);

	Submission save(Submission submission);

	List<Submission> findBySpeaker_GithubOrderByConference_ConfDateDescSubmissionStatusAscCreatedAtAsc(
			@Param("github") String github);
}
