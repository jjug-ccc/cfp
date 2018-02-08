package jjug.submission;

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
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
public class SubmissionControllerTest extends MockGithubServerTest {
	@LocalServerPort
	int port;

	@Test
	public void showSubmissions() throws Exception {
		this.server.setDispatcher(new MockGithubServerDispatcher(port,
				CfpUser.builder().name("Foo Bar").github("foo").email("foo@example.com")
						.avatarUrl("http://image.example.com/foo.jpg").build()));

		HtmlPage submissions = this.webClient.getPage("http://localhost:" + port
				+ "/conferences/00000000-0000-0000-0000-000021000101/submissions/form");

		assertThat(submissions.asText()).startsWith("Test Conf 1\n" + //
				"\n" + //
				"Test Conf 1 (2100/01/01)\n" + //
				"Japanese English\n" + //
				"\n" + //
				"Call for Papers\n" + //
				"講演情報\n" + //
				"タイトル\t\n" + //
				"概要\t\n" + //
				"想定している聴講者層\t\n" + //
				"カテゴリ\t(Required)\n" + //
				"難易度\t(Required)\n" + //
				"種類\t(Required)\n" + //
				"言語\t(Required)\n" + //
				"講演者情報1\n" + //
				"名前\tFoo Bar\n" + //
				"GitHubアカウント\tfoo\n" + //
				"所属\t\n" + //
				"講演者紹介\t\n" + //
				"コミュニティ活動、BlogのURL、Twitterアカウントなど\thttps://github.com/foo\n" + //
				"プロフィール画像URL\thttps://avatars.githubusercontent.com/foo?size=120\n" + //
				"非公開情報1\n" + //
				"Email\tfoo@example.com\n" + //
				"交通費支給を希望\tunchecked\n" + //
				"国・市町村（交通費支給を要する場合)\t\n" + //
				"事務局へのコメント、セッションの補足情報など");
	}

	@Test
	public void submitSubmission() throws Exception {
		this.server.setDispatcher(new MockGithubServerDispatcher(port,
				CfpUser.builder().name("Foo Bar").github("foo").email("foo@example.com")
						.avatarUrl("http://image.example.com/foo.jpg").build()));

		// Submit a submission
		HtmlPage submissions = this.webClient.getPage("http://localhost:" + port
				+ "/conferences/00000000-0000-0000-0000-000021000101/submissions/form");
		HtmlForm form = (HtmlForm) submissions.getElementsByTagName("form").get(0);
		form.getInputByName("title").setValueAttribute("テストセッション");
		form.getTextAreaByName("description").setText("テスト用のセッションです。");
		form.getInputByName("target").setValueAttribute("テストユーザーを対象としています。");
		form.getSelectByName("category").setSelectedAttribute("SERVER_SIDE_JAVA", true);
		form.getSelectByName("level").setSelectedAttribute("ADVANCED", true);
		form.getSelectByName("talkType").setSelectedAttribute("SHORT", true);
		form.getSelectByName("language").setSelectedAttribute("JAPANESE", true);
		form.getInputByName("speakerForms[0].companyOrCommunity")
				.setValueAttribute("JJUG");
		form.getTextAreaByName("speakerForms[0].bio").setText("色々やっています。");
		form.getTextAreaByName("speakerForms[0].activities").setText("@JJUG");
		HtmlPage submit = form.getInputsByValue("Submit CFP").get(0).click();
		assertThat(submit.asText()).contains("[Test Conf 1] テストセッション [応募済]");

		// Edit Form
		HtmlPage submission = submit.getAnchorByText("テストセッション").click();
		assertThat(submission.asText()).startsWith("Test Conf 1\n" + //
				"\n" + //
				"Test Conf 1 (2100/01/01)\n" + //
				"Japanese English\n" + //
				"\n" + //
				"Call for Papers\n" + //
				"Status: 応募済\n" + //
				"講演情報\n" + //
				"タイトル\tテストセッション\n" + //
				"概要\tテスト用のセッションです。\n" + //
				"想定している聴講者層\tテストユーザーを対象としています。\n" + //
				"カテゴリ\tServer Side Java\n" + //
				"難易度\t上級者向け\n" + //
				"種類\t一般枠 (20分)\n" + //
				"言語\t日本語\n" + //
				"講演者情報1\n" + //
				"名前\tFoo Bar\n" + //
				"GitHubアカウント\tfoo\n" + //
				"所属\tJJUG\n" + //
				"講演者紹介\t色々やっています。\n" + //
				"コミュニティ活動、BlogのURL、Twitterアカウントなど\t@JJUG\n" + //
				"プロフィール画像URL\thttps://avatars.githubusercontent.com/foo?size=120\n" + //
				"非公開情報1\n" + //
				"Email\tfoo@example.com\n" + //
				"交通費支給を希望\tunchecked\n" + //
				"国・市町村（交通費支給を要する場合)\t\n" + //
				"事務局へのコメント、セッションの補足情報など\t\n" + //
				" スピーカーを増やす");

		// Preview
		HtmlPage preview = ((HtmlAnchor) submission.querySelector("a[target=_blank]"))
				.click();
		assertThat(preview.asText()).startsWith("テストセッション\n" + //
				"\n" + //
				"テストセッション\n" + //
				"講演情報\n" + //
				"タイトル\n" + //
				"テストセッション\n" + //
				"概要\n" + //
				"テスト用のセッションです。\n" + //
				"\n" + //
				"想定している聴講者層\n" + //
				"テストユーザーを対象としています。\n" + //
				"カテゴリ\n" + //
				"Server Side Java\n" + //
				"難易度\n" + //
				"上級者向け\n" + //
				"種類\n" + //
				"一般枠 (20分)\n" + //
				"言語\n" + //
				"講演者情報\n" + //
				"\n" + //
				"\n" + //
				"Foo Bar \n" + //
				" JJUG\n" + //
				"色々やっています。\n" + //
				"\n" + //
				"@JJUG");
	}
}