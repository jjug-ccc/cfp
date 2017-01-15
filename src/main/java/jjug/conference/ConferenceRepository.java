package jjug.conference;

import java.util.Optional;
import java.util.UUID;

import jjug.conference.enums.ConfStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface ConferenceRepository extends Repository<Conference, UUID> {
	Optional<Conference> findOne(UUID id);

	Page<Conference> findByConfStatus(@Param("confStatus") ConfStatus confStatus,
			Pageable pageable);

	Page<Conference> findAll(Pageable pageable);

	Conference save(Conference conference);
}
