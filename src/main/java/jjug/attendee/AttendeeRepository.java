package jjug.attendee;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

public interface AttendeeRepository extends CrudRepository<Attendee, UUID> {
	Optional<Attendee> findByAttendeeId(UUID attendeeId);

	Optional<Attendee> findByEmailAndConference_ConfId(String email, UUID confId);
}
