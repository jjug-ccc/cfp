package jjug;

import am.ik.marked4j.Marked;
import am.ik.marked4j.MarkedBuilder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EntityScan(basePackageClasses = { CfpApplication.class, Jsr310JpaConverters.class })
@EnableAsync
public class CfpApplication {

	public static void main(String[] args) {
		SpringApplication.run(CfpApplication.class, args);
	}

	@Bean
	Marked marked() {
		return new MarkedBuilder().gfm(true).tables(true).sanitize(true).build();
	}
}
