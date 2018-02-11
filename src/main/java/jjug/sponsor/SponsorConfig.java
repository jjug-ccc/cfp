package jjug.sponsor;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

@Configuration
public class SponsorConfig {
	@Bean
	public SponsorAuthenticationProcessingFilter sponsorAuthenticationFilter(
			SponsorCredentialRepository sponsorCredentialRepository) {
		SponsorAuthenticationProcessingFilter sponsorFilter = new SponsorAuthenticationProcessingFilter(
				"/sponsors/login");
		sponsorFilter.setAuthenticationSuccessHandler(
				new SimpleUrlAuthenticationSuccessHandler("/sponsors"));
		sponsorFilter.setAuthenticationFailureHandler(
				new SimpleUrlAuthenticationFailureHandler("/sponsors/login?error"));
		sponsorFilter.setAuthenticationManager(
				new ProviderManager(Arrays.asList(new SponsorAuthenticationProvider(
						sponsorCredentialRepository, passwordEncoder()))));
		return sponsorFilter;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
