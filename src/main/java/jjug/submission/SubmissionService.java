package jjug.submission;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jjug.submission.enums.SubmissionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubmissionService {
	private final SubmissionRepository submissionRepository;

	@Transactional
	public void changeStatus(List<Status> statuses) {
		Map<UUID, SubmissionStatus> statusMap = statuses.stream()
				.collect(Collectors.toMap(Status::getSubmissionId, Status::getStatus));
		List<Submission> submissions = submissionRepository.findAll(statuses.stream()
				.map(Status::getSubmissionId).collect(Collectors.toList()));
		submissions
				.forEach(s -> s.setSubmissionStatus(statusMap.get(s.getSubmissionId())));
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Status {
		private UUID submissionId;
		private SubmissionStatus status;
	}
}
