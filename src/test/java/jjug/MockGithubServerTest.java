package jjug;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebClientOptions;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.After;
import org.junit.Before;

public abstract class MockGithubServerTest {
	protected WebClient webClient;
	protected MockWebServer server = new MockWebServer();

	@Before
	public void setup() throws Exception {
		this.server.start(55321);
		this.webClient = new WebClient();
		this.configureWebClient();
	}

	@After
	public void shutdown() throws Exception {
		this.server.shutdown();
		this.webClient.close();
	}

	protected void configureWebClient() {
		WebClientOptions options = this.webClient.getOptions();
		options.setCssEnabled(false);
		options.setJavaScriptEnabled(false);
		options.setThrowExceptionOnFailingStatusCode(false);
	}
}
