package jjug;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HomeControllerTest {
	WebClient webClient;
	MockWebServer server = new MockWebServer();
	@LocalServerPort
	int port;

	@Before
	public void setup() throws Exception {
		this.server.start(55321);
		this.webClient = new WebClient();
		this.webClient.getOptions().setCssEnabled(false);
		this.webClient.getOptions().setJavaScriptEnabled(false);
	}

	@After
	public void shutdown() throws Exception {
		this.server.shutdown();
		this.webClient.close();
	}

	@Test
	public void homeUser() throws Exception {
		this.server.setDispatcher(new MockGithubServerDispatcher(port,
				CfpUser.builder().name("Foo Bar").github("foo").email("foo@example.com")
						.avatarUrl("http://image.example.com/foo.jpg").build()));
		HtmlPage home = this.webClient.getPage("http://localhost:" + port);
		assertThat(home.asText()).startsWith("JJUG Call for Papers\n" + //
				"\n" + //
				"JJUG Call for Papers\n" + //
				" Foo Barさんログイン中。");
	}

	@Test
	public void homeAdmin() throws Exception {
		this.server.setDispatcher(new MockGithubServerDispatcher(port,
				CfpUser.builder().name("Taro JJUG").github("jjug-cfp")
						.email("jjug-cfp@example.com")
						.avatarUrl("http://image.example.com/foo.jpg").build()));
		HtmlPage home = this.webClient.getPage("http://localhost:" + port);
		assertThat(home.asText()).startsWith("JJUG Call for Papers\n" + //
				"\n" + //
				"JJUG Call for Papers\n" + //
				" Taro JJUGさんログイン中。\n" + //
				"\n" + //
				"CFP一覧 (管理者用)\n" + //
				"\n" + //
				"JJUG CCC 2018 Spring (2018/05/26) [CFP前準備]\n" + //
				" JJUG CCC 2017 Fall (2017/11/18) [CFP前準備]\n" + //
				" JJUG CCC 2017 Spring (2017/05/20) [CFP前準備]");
	}
}