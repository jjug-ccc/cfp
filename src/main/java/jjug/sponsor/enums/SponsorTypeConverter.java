package jjug.sponsor.enums;

import java.util.Optional;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class SponsorTypeConverter implements AttributeConverter<SponsorType, Integer> {
	@Override
	public Integer convertToDatabaseColumn(SponsorType attribute) {
		return Optional.ofNullable(attribute).map(SponsorType::getValue).orElse(null);
	}

	@Override
	public SponsorType convertToEntityAttribute(Integer dbData) {
		return Optional.ofNullable(dbData).map(SponsorType::valueOf).orElse(null);
	}
}
