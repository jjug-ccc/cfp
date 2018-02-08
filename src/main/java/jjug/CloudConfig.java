package jjug;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.config.java.AbstractCloudConfig;
import org.springframework.cloud.service.PooledServiceConnectorConfig.PoolConfig;
import org.springframework.cloud.service.relational.DataSourceConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Profile("cloud")
@Configuration
@ConfigurationProperties("cfp.cloud")
@Getter
@Setter
@EnableConfigurationProperties(MailProperties.class)
public class CloudConfig extends AbstractCloudConfig {
	private int minPoolSize = 0;
	private int maxPoolSize = 4;
	private int maxWaitTime = 3000;

	@Bean
	DataSource dataSource() {
		Map<String, Object> properties = new HashMap<>();
		// TODO
		// properties.put("initSQL",
		// "SET SESSION sql_mode='TRADITIONAL,NO_AUTO_VALUE_ON_ZERO,ONLY_FULL_GROUP_BY'");
		return connectionFactory().dataSource(new DataSourceConfig(
				new PoolConfig(minPoolSize, maxPoolSize, maxWaitTime), null, properties));
	}

	@Bean
	MailSender mailSender(MailProperties properties) {
		MailSender mailSender = connectionFactory().service(MailSender.class);
		if (mailSender instanceof JavaMailSenderImpl) {
			JavaMailSenderImpl javaMailSender = (JavaMailSenderImpl) mailSender;
			javaMailSender.setProtocol(properties.getProtocol());
			if (properties.getDefaultEncoding() != null) {
				javaMailSender.setDefaultEncoding(properties.getDefaultEncoding().name());
			}
			if (!properties.getProperties().isEmpty()) {
				Properties javaMailProperties = new Properties();
				javaMailProperties.putAll(properties.getProperties());
				javaMailSender.setJavaMailProperties(javaMailProperties);
			}
		}
		return mailSender;
	}
}