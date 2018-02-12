package jjug;

import java.net.URI;
import java.util.Set;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "cfp")
@Data
@Component
public class CfpProps {
	private String applicationName = "JJUG Call for Papers";
	private Set<String> adminUsers;
	private Slack slack = new Slack();

	@Data
	public static class Slack {
		private URI webhookUrl;
	}
}