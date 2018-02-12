package jjug.slack;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

import jjug.CfpProps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class SlackNotifier {
	private static final Logger log = LoggerFactory.getLogger(SlackNotifier.class);
	private final RestTemplate restTemplate;
	private final URI slackWebhookUrl;

	public SlackNotifier(RestTemplate restTemplate, CfpProps props) {
		this.restTemplate = restTemplate;
		this.slackWebhookUrl = props.getSlack().getWebhookUrl();
	}

	@Async
	public CompletableFuture<Void> notify(SlackWebhookPayload payload) {
		if (this.slackWebhookUrl == null) {
			log.info("Skip slack webhook");
			return CompletableFuture.completedFuture(null);
		}

		RequestEntity<?> req = RequestEntity.post(this.slackWebhookUrl)
				.contentType(MediaType.APPLICATION_JSON).body(payload);
		this.restTemplate.exchange(req, Void.class);
		return CompletableFuture.completedFuture(null);
	}
}