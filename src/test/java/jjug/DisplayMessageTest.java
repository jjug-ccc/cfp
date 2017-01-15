package jjug;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;

import org.junit.Test;
import org.springframework.context.support.StaticMessageSource;

public class DisplayMessageTest {
	@Test
	public void getDisplayMessage() throws Exception {
		StaticMessageSource messageSource = new StaticMessageSource();
		messageSource.addMessage("foo.abc", Locale.JAPAN, "Abc");
		messageSource.addMessage("foo.de-f", Locale.JAPAN, "DeF");

		assertThat(Foo.ABC.message(messageSource, Locale.JAPAN)).isEqualTo("Abc");
		assertThat(Foo.DE_F.message(messageSource, Locale.JAPAN)).isEqualTo("DeF");
	}

	enum Foo implements DisplayMessage {
		ABC, DE_F
	}
}