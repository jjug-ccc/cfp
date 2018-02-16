package jjug.sponsor;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class SponsorAuthenticationProvider implements AuthenticationProvider {
	private final SponsorCredentialRepository sponsorCredentialRepository;
	private final PasswordEncoder passwordEncoder;

	public SponsorAuthenticationProvider(
			SponsorCredentialRepository sponsorCredentialRepository,
			PasswordEncoder passwordEncoder) {
		this.sponsorCredentialRepository = sponsorCredentialRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	@Transactional
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {
		UsernamePasswordAuthenticationToken token = UsernamePasswordAuthenticationToken.class
				.cast(authentication);
		String sponsorId = token.getPrincipal().toString();
		String password = token.getCredentials().toString();

		SponsorCredential credential = this.sponsorCredentialRepository
				.findBySponsor_SponsorId(sponsorId) //
				.orElseThrow(() -> new UsernameNotFoundException("password is not set."));
		if (this.passwordEncoder.matches(password, credential.getCredential())) {
			SponsorUser sponsorUser = new SponsorUser(credential.getSponsor());
			UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(
					sponsorUser, password,
					AuthorityUtils.createAuthorityList("ROLE_SPONSOR"));
			return result;
		}
		else {
			throw new BadCredentialsException("username or password is wrong.");
		}
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}
}
