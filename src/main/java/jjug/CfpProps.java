package jjug;

import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@ConfigurationProperties(prefix = "cfp")
@Data
@Component
public class CfpProps {
	private String applicationName = "JJUG Call for Papers";
	private Set<String> adminUsers;
}