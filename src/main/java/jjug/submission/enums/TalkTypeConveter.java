package jjug.submission.enums;

import java.util.Optional;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class TalkTypeConveter implements AttributeConverter<TalkType, Integer> {
	@Override
	public Integer convertToDatabaseColumn(TalkType attribute) {
		return Optional.ofNullable(attribute).map(TalkType::getValue).orElse(null);
	}

	@Override
	public TalkType convertToEntityAttribute(Integer dbData) {
		return Optional.ofNullable(dbData).map(TalkType::valueOf).orElse(null);
	}
}
