package jjug.conference.enums;

import java.util.Optional;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class ConfStatusConveter implements AttributeConverter<ConfStatus, Integer> {
	@Override
	public Integer convertToDatabaseColumn(ConfStatus attribute) {
		return Optional.ofNullable(attribute).map(ConfStatus::getValue).orElse(null);
	}

	@Override
	public ConfStatus convertToEntityAttribute(Integer dbData) {
		return Optional.ofNullable(dbData).map(ConfStatus::valueOf).orElse(null);
	}
}
