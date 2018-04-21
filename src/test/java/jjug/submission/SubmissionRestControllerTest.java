package jjug.submission;

import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {
		"classpath:/delete-test-data.sql", "classpath:/insert-test-data.sql",
		"classpath:/delete-test-submission-data.sql", "classpath:/insert-test-submission-data.sql"},
		executionPhase = BEFORE_TEST_METHOD)
public class SubmissionRestControllerTest {
	@LocalServerPort
	int port;

	@Autowired
	private WebApplicationContext context;

	private JacksonTester<Submission> json;

	protected MockMvc mockMvc;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
	}

	@Test
	public void submissions() throws Exception {
		this.mockMvc.perform(get("/v1/conferences/{confId}/submissions", "00000000-0000-0000-0000-000021000101"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$._embedded.submissions[0].title", is("テストタイトル1")))
				.andExpect(jsonPath("$._embedded.submissions[0].description", is("test-description1")))
				.andExpect(jsonPath("$._embedded.submissions[0].target", is("テスト1が好きな人")))
				.andExpect(jsonPath("$._embedded.submissions[0].speakers[0].name", is("duke")))
				.andExpect(jsonPath("$._embedded.submissions[0].speakers[0].companyOrCommunity", is("JJUG")))
				.andExpect(jsonPath("$._embedded.submissions[0].speakers[0].bio", is("日本Javaユーザーグループ（Japan Java User Group/JJUG）は6000人以上のJavaエンジニアが参加する日本最大のJavaコミュニティです。")))
				.andExpect(jsonPath("$._embedded.submissions[0].speakers[0].activityList[0].activityType", is("TWITTER")))
				.andExpect(jsonPath("$._embedded.submissions[0].speakers[0].activityList[0].url", is("https://twitter.com/JJUG")))
				.andExpect(jsonPath("$._embedded.submissions[0].speakers[0].github", is("jjug1")))
				.andExpect(jsonPath("$._embedded.submissions[0].speakers[0].profileUrl", is("http://www.java-users.jp/wp-content/uploads/2012/09/duke.png")))
				.andExpect(jsonPath("$._embedded.submissions[0].category", is("JAVA_SE")))
				.andExpect(jsonPath("$._embedded.submissions[0].level", is("BEGINNER")))
				.andExpect(jsonPath("$._embedded.submissions[0].talkType", is("STANDARD")))
				.andExpect(jsonPath("$._embedded.submissions[0].language", is("JAPANESE")))
				.andExpect(jsonPath("$._embedded.submissions[1].title", is("テストタイトル4")))
				.andExpect(jsonPath("$._embedded.submissions[1].description", is("test-description4")))
				.andExpect(jsonPath("$._embedded.submissions[1].target", is("テスト4が好きな人")))
				.andExpect(jsonPath("$._embedded.submissions[1].speakers[0].name", is("duke")))
				.andExpect(jsonPath("$._embedded.submissions[1].speakers[0].companyOrCommunity", is("JJUG")))
				.andExpect(jsonPath("$._embedded.submissions[1].speakers[0].bio", is("日本Javaユーザーグループ（Japan Java User Group/JJUG）は6000人以上のJavaエンジニアが参加する日本最大のJavaコミュニティです。")))
				.andExpect(jsonPath("$._embedded.submissions[1].speakers[0].activityList[0].activityType", is("TWITTER")))
				.andExpect(jsonPath("$._embedded.submissions[1].speakers[0].activityList[0].url", is("https://twitter.com/JJUG")))
				.andExpect(jsonPath("$._embedded.submissions[1].speakers[0].github", is("jjug3")))
				.andExpect(jsonPath("$._embedded.submissions[1].speakers[1].profileUrl", is("http://www.java-users.jp/wp-content/uploads/2012/09/duke.png")))
				.andExpect(jsonPath("$._embedded.submissions[1].speakers[1].name", is("duke")))
				.andExpect(jsonPath("$._embedded.submissions[1].speakers[1].companyOrCommunity", is("JJUG")))
				.andExpect(jsonPath("$._embedded.submissions[1].speakers[1].bio", is("日本Javaユーザーグループ（Japan Java User Group/JJUG）は6000人以上のJavaエンジニアが参加する日本最大のJavaコミュニティです。")))
				.andExpect(jsonPath("$._embedded.submissions[1].speakers[1].activityList[0].activityType", is("TWITTER")))
				.andExpect(jsonPath("$._embedded.submissions[1].speakers[1].activityList[0].url", is("https://twitter.com/JJUG")))
				.andExpect(jsonPath("$._embedded.submissions[1].speakers[1].github", is("jjug4")))
				.andExpect(jsonPath("$._embedded.submissions[1].speakers[1].profileUrl", is("http://www.java-users.jp/wp-content/uploads/2012/09/duke.png")))
				.andExpect(jsonPath("$._embedded.submissions[1].category", is("JAVA_SE")))
				.andExpect(jsonPath("$._embedded.submissions[1].level", is("BEGINNER")))
				.andExpect(jsonPath("$._embedded.submissions[1].talkType", is("STANDARD")))
				.andExpect(jsonPath("$._embedded.submissions[1].language", is("JAPANESE")));
	}

}
