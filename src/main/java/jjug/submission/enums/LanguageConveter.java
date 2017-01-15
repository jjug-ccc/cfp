package jjug.submission.enums;

import java.util.Optional;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class LanguageConveter implements AttributeConverter<Language, Integer> {
	@Override
	public Integer convertToDatabaseColumn(Language attribute) {
		return Optional.ofNullable(attribute).map(Language::getValue).orElse(null);
	}

	@Override
	public Language convertToEntityAttribute(Integer dbData) {
		return Optional.ofNullable(dbData).map(Language::valueOf).orElse(null);
	}
}
