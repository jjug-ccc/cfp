package jjug.admin;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import jjug.CfpUser;
import jjug.MockGithubServerDispatcher;
import jjug.MockGithubServerTest;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = { "classpath:/delete-test-data.sql",
		"classpath:/insert-test-data.sql" }, executionPhase = BEFORE_TEST_METHOD)
public class ConferenceCreateControllerTest extends MockGithubServerTest {
	@LocalServerPort
	int port;

	@Test
	public void create() throws Exception {
		this.server.setDispatcher(new MockGithubServerDispatcher(port,
				CfpUser.builder().name("Taro JJUG").github("jjug-cfp")
						.email("jjug-cfp@example.com")
						.avatarUrl("http://image.example.com/foo.jpg").build()));
		HtmlPage formPage = this.webClient
				.getPage("http://localhost:" + port + "/admin/conferences");

		HtmlForm form = (HtmlForm) formPage.getElementsByTagName("form").get(0);
		form.getInputByName("confName").setValueAttribute("Test CCC 2030 Spring");
		form.getInputByName("confDate").setValueAttribute("2030-01-01");
		HtmlPage created = form.getInputByValue("作成").click();
		assertThat(created.asText()).contains("Test CCC 2030 Spring (2030/01/01)");
	}
}
