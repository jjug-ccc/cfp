package jjug.security;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;

import jjug.sponsor.SponsorAuthenticationProcessingFilter;

import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableOAuth2Sso
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	private final SponsorAuthenticationProcessingFilter sponsorFilter;

	public SecurityConfig(SponsorAuthenticationProcessingFilter sponsorFilter) {
		this.sponsorFilter = sponsorFilter;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.requestMatchers() //
				.antMatchers("/**") //
				.and() //
				.addFilterAfter(this.sponsorFilter, LogoutFilter.class) //
				.authorizeRequests() //
				.antMatchers(HttpMethod.OPTIONS).permitAll() //
				.mvcMatchers("/submissions/*", "/conferences/*",
						"/conferences/*/submissions", "/conferences/*/votes",
						"/v1/conferences/{confId}/submissions/**", "/sponsors/login",
						"/credentialresets/**")
				.permitAll() //
				.mvcMatchers("/v1/votes").authenticated() //
				.mvcMatchers("/admin", "/admin/**", "/v1/**").hasRole("ADMIN") //
				.mvcMatchers("/sponsors", "/sponsors/*").hasAnyRole("SPONSOR") //
				.antMatchers("/login**").permitAll() //
				.anyRequest().authenticated() //
				.and() //
				.csrf()
				.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
	}

	@Bean
	public Filter cors() {
		return new Filter() {
			@Override
			public void init(FilterConfig filterConfig) throws ServletException {

			}

			@Override
			public void doFilter(ServletRequest request, ServletResponse response,
					FilterChain chain) throws IOException, ServletException {
				HttpServletResponse servletResponse = (HttpServletResponse) response;
				servletResponse.setHeader("Access-Control-Allow-Origin", "*");
				servletResponse.setHeader("Access-Control-Allow-Methods", "OPTIONS, GET");
				servletResponse.setHeader("Access-Control-Max-Age", "3600");
				servletResponse.setHeader("Access-Control-Allow-Headers",
						"Origin, X-Requested-With, Content-Type, Accept, Authorization, X-XSRF-TOKEN");
				chain.doFilter(request, response);
			}

			@Override
			public void destroy() {

			}
		};
	}
}