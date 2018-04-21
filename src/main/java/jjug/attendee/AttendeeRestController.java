package jjug.attendee;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import lombok.Data;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
public class AttendeeRestController {
	private final AttendeeRepository attendeeRepository;
	private final AttendeeService attendeeService;

	public AttendeeRestController(AttendeeRepository attendeeRepository,
			AttendeeService attendeeService) {
		this.attendeeRepository = attendeeRepository;
		this.attendeeService = attendeeService;
	}

	@PostMapping(path = "v1/conferences/{confId}/attendees")
	public ResponseEntity<?> attend(@PathVariable("confId") UUID confId,
			@RequestBody AttendRequest request, UriComponentsBuilder builder) {
		Attendee attend = this.attendeeService.attend(request.getEmail(), confId,
				request.getIds());
		URI uri = builder.pathSegment("v1", "conferences", confId.toString(), "attendees",
				attend.getAttendeeId().toString()).build().toUri();
		return ResponseEntity.created(uri).body(attend);
	}

	@PostMapping(path = "v1/conferences/{confId}/attendees/{attendeeId}")
	public ResponseEntity<?> attend(@PathVariable("confId") UUID confId,
			@RequestBody AttendRequest request,
			@PathVariable("attendeeId") UUID attendeeId) {
		Attendee attend = this.attendeeService.update(attendeeId, request.getIds());
		return ResponseEntity.ok(attend);
	}

	@GetMapping(path = "v1/conferences/{confId}/attendees/{attendeeId}")
	public ResponseEntity<?> attendee(@PathVariable("confId") UUID confId,
			@PathVariable("attendeeId") UUID attendeeId) {
		return this.attendeeRepository.findByAttendeeId(attendeeId)
				.map(ResponseEntity::<Object>ok)
				.orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(Collections.singletonMap("message", "The given attendeeId("
								+ attendeeId + ") is not found.")));
	}

	@Data
	public static class AttendRequest {
		private String email;
		private List<UUID> ids;
	}
}
