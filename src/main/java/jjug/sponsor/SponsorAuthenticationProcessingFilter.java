package jjug.sponsor;

import java.io.IOException;
import java.util.Objects;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

public class SponsorAuthenticationProcessingFilter
		extends AbstractAuthenticationProcessingFilter {
	public SponsorAuthenticationProcessingFilter(String processesUrl) {
		super(new AntPathRequestMatcher(processesUrl, "POST"));
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request,
			HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {
		String sponsorId = Objects.toString(request.getParameter("sponsorId"), "").trim();
		String password = Objects.toString(request.getParameter("password"), "");
		UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
				sponsorId, password);
		return this.getAuthenticationManager().authenticate(authRequest);
	}
}
