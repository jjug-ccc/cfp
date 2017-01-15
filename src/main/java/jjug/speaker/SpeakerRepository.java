package jjug.speaker;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface SpeakerRepository extends Repository<Speaker, UUID> {
	Optional<Speaker> findByGithub(@Param("github") String github);

	Speaker save(Speaker speaker);
}
