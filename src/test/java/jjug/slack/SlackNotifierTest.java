package jjug.slack;

import java.net.URI;

import jjug.CfpProps;
import org.junit.Test;

import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class SlackNotifierTest {

	@Test
	public void testNotify() throws Exception {
		RestTemplate restTemplate = new RestTemplate();
		MockRestServiceServer mockServer = MockRestServiceServer.bindTo(restTemplate)
				.build();

		CfpProps props = new CfpProps();
		CfpProps.Slack slack = new CfpProps.Slack();
		slack.setWebhookUrl(URI.create("https://example.com"));
		props.setSlack(slack);

		mockServer.expect(requestTo("https://example.com"))
				.andExpect(MockRestRequestMatchers.jsonPath("$.text",
						equalTo("Test Payload")))
				.andRespond(withSuccess("{}", APPLICATION_JSON_UTF8));

		SlackWebhookPayload payload = SlackWebhookPayload.builder().text("Test Payload")
				.build();
		SlackNotifier slackNotifier = new SlackNotifier(restTemplate, props);
		slackNotifier.notify(payload).get();

		mockServer.verify();
	}
}