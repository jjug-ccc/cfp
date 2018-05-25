package jjug.admin;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import jjug.conference.Conference;
import jjug.conference.ConferenceRepository;
import jjug.speaker.Speaker;
import jjug.speaker.Speakers;
import jjug.submission.Submission;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DownloadController {
	private final Charset windows31j = Charset.forName("Windows-31J");
	private final MediaType textCsv = new MediaType("text", "csv", windows31j);
	private final ConferenceRepository conferenceRepository;

	public DownloadController(ConferenceRepository conferenceRepository) {
		this.conferenceRepository = conferenceRepository;
	}

	@GetMapping(path = "admin/conferences/{confId}.csv")
	public ResponseEntity download(@PathVariable UUID confId) throws IOException {
		Path tempFile = Files.createTempFile("download-", ".csv");
		File file = tempFile.toFile();
		try (CSVPrinter csvPrinter = CSVFormat.RFC4180
				.withHeader("Status", "Title", "Name", "Github", "Email", "Category",
						"Level", "Type", "Language", "TransportationAllowance")
				.print(file, windows31j)) {
			Conference conference = conferenceRepository.findOne(confId).get();
			List<Submission> sessions = conference.getSessions();
			for (Submission submission : sessions) {
				Speakers speakers = submission.speakers();
				csvPrinter.printRecord(submission.getSubmissionStatus(),
						submission.getTitle(), speakers.getName(), speakers.getGithub(),
						speakers.getEmail(), submission.getCategory(),
						submission.getLevel(), submission.getTalkType(),
						submission.getLanguage(),
						submission.getSpeakers().stream()
								.map(Speaker::isTransportationAllowance)
								.map(Object::toString).collect(Collectors.joining(",")));
			}
			return ResponseEntity.ok().contentType(textCsv)
					.body(new FileSystemResource(file));
		}
	}
}
