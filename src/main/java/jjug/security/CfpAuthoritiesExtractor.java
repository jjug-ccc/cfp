package jjug.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.boot.autoconfigure.security.oauth2.resource.AuthoritiesExtractor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import jjug.CfpProps;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CfpAuthoritiesExtractor implements AuthoritiesExtractor {
	private final CfpProps props;

	@Override
	public List<GrantedAuthority> extractAuthorities(Map<String, Object> map) {
		String login = String.valueOf(map.get("login"));
		List<String> authorities = new ArrayList<>();
		authorities.add("USER");
		if (props.getAdminUsers().contains(login)) {
			authorities.add("ADMIN");
			authorities.add("ACTUATOR");
		}
		return authorities.stream().map(a -> new SimpleGrantedAuthority("ROLE_" + a))
				.collect(Collectors.toList());
	}
}
