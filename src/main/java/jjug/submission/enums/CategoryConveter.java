package jjug.submission.enums;

import java.util.Optional;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class CategoryConveter implements AttributeConverter<Category, Integer> {
	@Override
	public Integer convertToDatabaseColumn(Category attribute) {
		return Optional.ofNullable(attribute).map(Category::getValue).orElse(null);
	}

	@Override
	public Category convertToEntityAttribute(Integer dbData) {
		return Optional.ofNullable(dbData).map(Category::valueOf).orElse(null);
	}
}
