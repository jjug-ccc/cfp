package jjug.submission;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@CrossOrigin
public class SubmissionRestController {
	private final SubmissionRepository submissionRepository;

	@GetMapping(path = "v1/conferences/{confId}/submissions", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public Resources<Resource<Submission>> submissions(
			@PathVariable("confId") UUID confId, UriComponentsBuilder builder) {
		List<Submission> submissions = submissionRepository
				.findAllAcceptedByConference(confId);
		Resources<Resource<Submission>> resources = new Resources<>(submissions.stream() //
				.map(s -> toResource(s, confId, builder)) //
				.collect(Collectors.toList()));
		return resources;
	}

	@GetMapping(path = "v1/conferences/{confId}/submissions/{submissionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public Resource<Submission> submission(
			@PathVariable("submissionId") UUID submissionId,
			@PathVariable("confId") UUID confId, UriComponentsBuilder builder) {
		Optional<Submission> submission = submissionRepository
				.findAcceptedByConference(submissionId, confId);
		return submission.map(s -> toResource(s, confId, builder)).get();
	}

	Resource<Submission> toResource(Submission s, UUID confId,
			UriComponentsBuilder builder) {
		Resource<Submission> resource = new Resource<>(s);
		resource.add(new Link(
				builder.replacePath("v1/conferences/{confId}/submissions/{sessionId}")
						.buildAndExpand(confId, s.getSubmissionId()).toUriString(),
				"self"));
		return resource;
	}
}
