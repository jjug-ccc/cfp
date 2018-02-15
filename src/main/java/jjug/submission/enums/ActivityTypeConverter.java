package jjug.submission.enums;

import jjug.speaker.enums.ActivityType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Optional;

@Converter(autoApply = true)
public class ActivityTypeConverter implements AttributeConverter<ActivityType, Integer> {
	@Override
	public Integer convertToDatabaseColumn(ActivityType attribute) {
		return Optional.ofNullable(attribute).map(ActivityType::getValue).orElse(null);
	}

	@Override
	public ActivityType convertToEntityAttribute(Integer dbData) {
		return Optional.ofNullable(dbData).map(ActivityType::valueOf).orElse(null);
	}
}
