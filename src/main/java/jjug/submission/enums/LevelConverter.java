package jjug.submission.enums;

import java.util.Optional;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class LevelConverter implements AttributeConverter<Level, Integer> {
	@Override
	public Integer convertToDatabaseColumn(Level attribute) {
		return Optional.ofNullable(attribute).map(Level::getValue).orElse(null);
	}

	@Override
	public Level convertToEntityAttribute(Integer dbData) {
		return Optional.ofNullable(dbData).map(Level::valueOf).orElse(null);
	}
}
