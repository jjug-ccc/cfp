package jjug;

import java.util.Locale;

import org.springframework.context.MessageSource;

public interface DisplayMessage {
	default String message(MessageSource messageSource, Locale locale) {
		String prefix = this.getClass().getSimpleName().toLowerCase();
		String name = ((Enum) this).name().replace('_', '-').toLowerCase();
		return messageSource.getMessage(prefix + "." + name, null, locale);
	}
}
