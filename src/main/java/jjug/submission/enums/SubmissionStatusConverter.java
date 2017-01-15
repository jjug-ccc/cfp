package jjug.submission.enums;

import java.util.Optional;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class SubmissionStatusConverter
		implements AttributeConverter<SubmissionStatus, Integer> {
	@Override
	public Integer convertToDatabaseColumn(SubmissionStatus attribute) {
		return Optional.ofNullable(attribute).map(SubmissionStatus::getValue)
				.orElse(null);
	}

	@Override
	public SubmissionStatus convertToEntityAttribute(Integer dbData) {
		return Optional.ofNullable(dbData).map(SubmissionStatus::valueOf).orElse(null);
	}
}
