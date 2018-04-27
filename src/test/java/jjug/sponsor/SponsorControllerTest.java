package jjug.sponsor;

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.icegreen.greenmail.junit.GreenMailRule;
import com.icegreen.greenmail.util.ServerSetupTest;
import jjug.CfpUser;
import jjug.MockGithubServerDispatcher;
import jjug.MockGithubServerTest;
import org.junit.Rule;
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
public class SponsorControllerTest extends MockGithubServerTest {
	@LocalServerPort
	int port;
	@Rule
	public final GreenMailRule greenMail = new GreenMailRule(ServerSetupTest.SMTP);

	@Test
	public void testSponsorLogin() throws Exception {
		HtmlPage homePage = this.login("test-sponsor", "password");
		assertThat(homePage.asText()).startsWith("JJUG Call for Papers\n" + //
				"\n" + //
				"JJUG Call for Papers\n" + //
				" [スポンサー] テストスポンサーさんログイン中。 パスワードリセット\n" + //
				"スポンサー中のCFP\n" + //
				" Test Conf 1 (2100/01/01) \uD83D\uDCDD応募");
	}

	@Test
	public void testSponsorLoginWrongSponsorId() throws Exception {
		HtmlPage fail = this.login("wrong-sponsor", "password");
		assertThat(fail.asText()).contains("スポンサーIDまたはパスワードが正しくありません");
	}

	@Test
	public void testSponsorLoginWrongPassword() throws Exception {
		HtmlPage fail = this.login("test-sponsor", "test-password");
		assertThat(fail.asText()).contains("スポンサーIDまたはパスワードが正しくありません");
	}

	@Test
	public void testPasswordReset() throws Exception {
		HtmlPage home = this.login("test-sponsor", "password");
		HtmlPage resetPage = home.getAnchorByText("パスワードリセット").click();
		HtmlForm resetForm = (HtmlForm) resetPage.getElementsByTagName("form").get(0);
		assertThat(resetForm.asText()).contains("Test Conf 1");
		assertThat(resetForm.asText()).contains("テストスポンサー 様");
		resetForm.getInputByName("sponsorId").setValueAttribute("test-sponsor");
		resetForm.getInputByName("password").setValueAttribute("new-password");
		resetForm.getInputByName("passwordConfirm").setValueAttribute("new-password");
		HtmlPage loginPage = resetForm.getInputByValue("リセット").click();
		HtmlForm loginForm = (HtmlForm) loginPage.getElementsByTagName("form").get(0);
		loginForm.getInputByName("sponsorId").setValueAttribute("test-sponsor");
		loginForm.getInputByName("password").setValueAttribute("new-password");
		HtmlPage homePage = loginForm.getInputByValue("ログイン").click();
		assertThat(homePage.asText()).contains("[スポンサー] テストスポンサーさんログイン中。");
	}

	@Test
	public void testSponsorSubmission() throws Exception {
		HtmlPage home = this.login("test-sponsor", "password");
		HtmlPage resetPage = home.getAnchorByText("パスワードリセット").click();
		HtmlForm resetForm = (HtmlForm) resetPage.getElementsByTagName("form").get(0);
		resetForm.getInputByName("sponsorId").setValueAttribute("test-sponsor");
		resetForm.getInputByName("password").setValueAttribute("new-password");
		resetForm.getInputByName("passwordConfirm").setValueAttribute("new-password");
		HtmlPage loginPage = resetForm.getInputByValue("リセット").click();
		HtmlForm loginForm = (HtmlForm) loginPage.getElementsByTagName("form").get(0);
		loginForm.getInputByName("sponsorId").setValueAttribute("test-sponsor");
		loginForm.getInputByName("password").setValueAttribute("new-password");
		HtmlPage homePage = loginForm.getInputByValue("ログイン").click();
		HtmlPage submissionPage = homePage.getAnchorByText("\uD83D\uDCDD応募").click();
		HtmlPage submissionFormPage = submissionPage.getAnchorByText("\uD83D\uDCDD応募")
				.click();
		assertThat(submissionFormPage.asText()).isEqualTo("Test Conf 1\n" + //
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
				" ❗ Githubアカウントがない場合は、\"GitHubアカウント\"にjjug-ccc-test-sponsorを入力してください。 ❗\n" + //
				"名前\t\n" + //
				"GitHubアカウント\t\n" + //
				"所属\tテストスポンサー\n" + //
				"講演者紹介\t\n" + //
				"プロフィール画像URL\thttp://www.java-users.jp/ccc2017fall/assets/img/speakers/duke.jpg\n"
				+ //
				"コミュニティ活動、BlogのURL、Twitterアカウントなど1\t(Required)\n" + //
				"コミュニティ活動、BlogのURL、Twitterアカウントなど2\t\n" + //
				"コミュニティ活動、BlogのURL、Twitterアカウントなど3\t\n" + //
				"コミュニティ活動、BlogのURL、Twitterアカウントなど4\t\n" + //
				"コミュニティ活動、BlogのURL、Twitterアカウントなど5\t\n" + //
				"非公開情報1\n" + //
				"Email\t\n" + //
				"交通費支給を希望\tunchecked\n" + //
				"国・市町村（交通費支給を要する場合)\t\n" + //
				"事務局へのコメント、セッションの補足情報など\t\n" + //
				" スピーカーを増やす\n" + //
				"\n" + //
				"Submit CFP\n" + //
				"JJUG Call for Papers");

		HtmlForm submissionForm = this
				.inputSpeaker1(this.inputSession(submissionFormPage));
		HtmlPage submit = submissionForm.getInputsByValue("Submit CFP").get(0).click();

		assertThat(submit.asText()).isEqualTo("JJUG Call for Papers\n" + //
				"\n" + //
				"JJUG Call for Papers\n" + //
				" [スポンサー] テストスポンサーさんログイン中。 パスワードリセット\n" + //
				"スポンサー中のCFP\n" + //
				" Test Conf 1 (2100/01/01) \uD83D\uDCDD応募\n" + //
				"応募済みCFP\n" + //
				"\n" + //
				"[Test Conf 1] スポンサーセッション [スポンサー]");

		HtmlPage editFormPage = submit.getAnchorByText("スポンサーセッション").click();
		HtmlPage preview = ((HtmlAnchor) editFormPage.querySelector("a[target=_blank]"))
				.click();
		assertThat(preview.asText()).isEqualTo("スポンサーセッション\n" + //
				"\n" + //
				"スポンサーセッション\n" + //
				"講演情報\n" + //
				"タイトル\n" + //
				"スポンサーセッション\n" + //
				"概要\n" + //
				"スポンサーのセッションです。\n" + //
				"\n" + //
				"想定している聴講者層\n" + //
				"テストユーザーを対象としています。\n" + //
				"カテゴリ\n" + //
				"Server Side Java\n" + //
				"難易度\n" + //
				"中級者向け\n" + //
				"種類\n" + //
				"一般枠 (45分)\n" + //
				"言語\n" + //
				"日本語\n" + //
				"講演者情報\n" + //
				"\n" + //
				"\n" + //
				"スポンサー太郎 \n" + //
				" テストスポンサー\n" + //
				"色々やっています。\n" + //
				"\n" + //
				"https://twitter.com/jjug");
	}

	@Test
	public void testPasswordResetTwice() throws Exception {
		HtmlPage home = this.login("test-sponsor", "password");
		HtmlPage resetPage = home.getAnchorByText("パスワードリセット").click();
		HtmlForm resetForm = (HtmlForm) resetPage.getElementsByTagName("form").get(0);
		resetForm.getInputByName("sponsorId").setValueAttribute("test-sponsor");
		resetForm.getInputByName("password").setValueAttribute("new-password");
		resetForm.getInputByName("passwordConfirm").setValueAttribute("new-password");
		resetForm.getInputByValue("リセット").click();
		HtmlPage expired = this.webClient.getPage(resetPage.getBaseURL());
		assertThat(expired.asText()).contains("パスワードリセットの有効期限が切れているか、パスワードリセット済みです。");
	}

	@Test
	public void testPasswordResetWrongSponsorId() throws Exception {
		HtmlPage home = this.login("test-sponsor", "password");
		HtmlPage resetPage = home.getAnchorByText("パスワードリセット").click();
		HtmlForm resetForm = (HtmlForm) resetPage.getElementsByTagName("form").get(0);
		resetForm.getInputByName("sponsorId").setValueAttribute("wrong-sponsor");
		resetForm.getInputByName("password").setValueAttribute("new-password");
		resetForm.getInputByName("passwordConfirm").setValueAttribute("new-password");
		HtmlPage loginPage = resetForm.getInputByValue("リセット").click();
		assertThat(loginPage.asText()).contains("スポンサーIDが正しくありません");
	}

	@Test
	public void testPasswordResetWrongPassword() throws Exception {
		HtmlPage home = this.login("test-sponsor", "password");
		HtmlPage resetPage = home.getAnchorByText("パスワードリセット").click();
		HtmlForm resetForm = (HtmlForm) resetPage.getElementsByTagName("form").get(0);
		resetForm.getInputByName("sponsorId").setValueAttribute("test-sponsor");
		resetForm.getInputByName("password").setValueAttribute("new-password");
		resetForm.getInputByName("passwordConfirm").setValueAttribute("new-password2");
		HtmlPage loginPage = resetForm.getInputByValue("リセット").click();
		assertThat(loginPage.asText()).contains("パスワード(確認)とパスワードの値が一致しません");
	}

	@Test
	public void testSubmitAfterCfpIsFixed() throws Exception {
		this.server.setDispatcher(new MockGithubServerDispatcher(port,
				CfpUser.builder().name("Taro JJUG").github("jjug-cfp")
						.email("jjug-cfp@example.com")
						.avatarUrl("http://image.example.com/foo.jpg").build()));
		HtmlPage adminPage = this.webClient.getPage("http://localhost:" + port);
		assertThat(adminPage.asText()).contains("Test Conf 1 (2100/01/01)");
		HtmlPage confPage = adminPage.getAnchorByText("Test Conf 1 (2100/01/01)").click();

		((HtmlSelect) confPage.getElementByName("confStatus"))
				.setSelectedAttribute("SELECTION", true);
		HtmlPage updatedConfPage = confPage.getElementByName("changeConfStatus").click();
		assertThat(updatedConfPage.asText()).startsWith("JJUG Call for Papers\n" + //
				"\n" + //
				"JJUG Call for Papers\n" + //
				" Taro JJUGさんログイン中。\n" + //
				"\n" + //
				"CFP一覧 (管理者用)\n" + //
				"\n" + //
				"Test Conf 1 (2100/01/01) [選考中]");

		HtmlPage home = this.login("test-sponsor", "password");
		HtmlPage resetPage = home.getAnchorByText("パスワードリセット").click();
		HtmlForm resetForm = (HtmlForm) resetPage.getElementsByTagName("form").get(0);
		resetForm.getInputByName("sponsorId").setValueAttribute("test-sponsor");
		resetForm.getInputByName("password").setValueAttribute("new-password");
		resetForm.getInputByName("passwordConfirm").setValueAttribute("new-password");
		HtmlPage loginPage = resetForm.getInputByValue("リセット").click();
		HtmlForm loginForm = (HtmlForm) loginPage.getElementsByTagName("form").get(0);
		loginForm.getInputByName("sponsorId").setValueAttribute("test-sponsor");
		loginForm.getInputByName("password").setValueAttribute("new-password");
		HtmlPage homePage = loginForm.getInputByValue("ログイン").click();
		HtmlPage submissionPage = homePage.getAnchorByText("\uD83D\uDCDD応募").click();
		HtmlPage submissionFormPage = submissionPage.getAnchorByText("\uD83D\uDCDD応募")
				.click();
		HtmlForm submissionForm = this
				.inputSpeaker1(this.inputSession(submissionFormPage));
		HtmlPage submit = submissionForm.getInputsByValue("Submit CFP").get(0).click();

		assertThat(submit.asText()).isEqualTo("JJUG Call for Papers\n" + //
				"\n" + //
				"JJUG Call for Papers\n" + //
				" [スポンサー] テストスポンサーさんログイン中。 パスワードリセット\n" + //
				"スポンサー中のCFP\n" + //
				" Test Conf 1 (2100/01/01) \uD83D\uDCDD応募\n" + //
				"応募済みCFP\n" + //
				"\n" + //
				"[Test Conf 1] スポンサーセッション [スポンサー]");
	}

	@Test
	public void testSubmitAfterCfpIsOpen() throws Exception {
		this.server.setDispatcher(new MockGithubServerDispatcher(port,
				CfpUser.builder().name("Taro JJUG").github("jjug-cfp")
						.email("jjug-cfp@example.com")
						.avatarUrl("http://image.example.com/foo.jpg").build()));
		HtmlPage adminPage = this.webClient.getPage("http://localhost:" + port);
		assertThat(adminPage.asText()).contains("Test Conf 1 (2100/01/01)");
		HtmlPage confPage = adminPage.getAnchorByText("Test Conf 1 (2100/01/01)").click();

		((HtmlSelect) confPage.getElementByName("confStatus"))
				.setSelectedAttribute("OPEN", true);
		HtmlPage updatedConfPage = confPage.getElementByName("changeConfStatus").click();
		assertThat(updatedConfPage.asText()).startsWith("JJUG Call for Papers\n" + //
				"\n" + //
				"JJUG Call for Papers\n" + //
				" Taro JJUGさんログイン中。\n" + //
				"投票完了のCFP\n" + //
				"\n" + //
				"Test Conf 1 (2100/01/01)\n" + //
				"\n" + //
				"CFP一覧 (管理者用)\n" + //
				"\n" + //
				"Test Conf 1 (2100/01/01) [公開中]");

		HtmlPage home = this.login("test-sponsor", "password");
		HtmlPage resetPage = home.getAnchorByText("パスワードリセット").click();
		HtmlForm resetForm = (HtmlForm) resetPage.getElementsByTagName("form").get(0);
		resetForm.getInputByName("sponsorId").setValueAttribute("test-sponsor");
		resetForm.getInputByName("password").setValueAttribute("new-password");
		resetForm.getInputByName("passwordConfirm").setValueAttribute("new-password");
		HtmlPage loginPage = resetForm.getInputByValue("リセット").click();
		HtmlForm loginForm = (HtmlForm) loginPage.getElementsByTagName("form").get(0);
		loginForm.getInputByName("sponsorId").setValueAttribute("test-sponsor");
		loginForm.getInputByName("password").setValueAttribute("new-password");
		HtmlPage homePage = loginForm.getInputByValue("ログイン").click();
		HtmlPage submissionPage = homePage.getAnchorByText("\uD83D\uDCDD応募").click();
		HtmlPage submissionFormPage = submissionPage.getAnchorByText("\uD83D\uDCDD応募")
				.click();
		HtmlForm submissionForm = this
				.inputSpeaker1(this.inputSession(submissionFormPage));
		HtmlPage submit = submissionForm.getInputsByValue("Submit CFP").get(0).click();

		assertThat(submit.asText()).isEqualTo("JJUG Call for Papers\n" + //
				"\n" + //
				"JJUG Call for Papers\n" + //
				" [スポンサー] テストスポンサーさんログイン中。 パスワードリセット\n" + //
				"スポンサー中のCFP\n" + //
				" Test Conf 1 (2100/01/01) \uD83D\uDCDD応募\n" + //
				"投票完了のCFP\n" + //
				"\n" + //
				"Test Conf 1 (2100/01/01)\n" + //
				"\n" + //
				"応募済みCFP\n" + //
				"\n" + //
				"[Test Conf 1] スポンサーセッション [スポンサー]");
	}

	private HtmlPage login(String sponsorId, String password) throws Exception {
		HtmlPage loginPage = this.webClient
				.getPage("http://localhost:" + port + "/sponsors/login");
		HtmlForm loginForm = (HtmlForm) loginPage.getElementsByTagName("form").get(0);
		loginForm.getInputByName("sponsorId").setValueAttribute(sponsorId);
		loginForm.getInputByName("password").setValueAttribute(password);
		return loginForm.getInputByValue("ログイン").click();
	}

	private HtmlForm inputSession(HtmlPage submission) {
		HtmlForm form = (HtmlForm) submission.getElementsByTagName("form").get(0);
		form.getInputByName("title").setValueAttribute("スポンサーセッション");
		form.getTextAreaByName("description").setText("スポンサーのセッションです。");
		form.getInputByName("target").setValueAttribute("テストユーザーを対象としています。");
		form.getSelectByName("category").setSelectedAttribute("SERVER_SIDE_JAVA", true);
		form.getSelectByName("level").setSelectedAttribute("INTERMEDIATE", true);
		form.getSelectByName("talkType").setSelectedAttribute("STANDARD", true);
		form.getSelectByName("language").setSelectedAttribute("JAPANESE", true);
		return form;
	}

	private HtmlForm inputSpeaker1(HtmlForm form) {
		form.getInputByName("speakerForms[0].name").setValueAttribute("スポンサー太郎");
		form.getInputByName("speakerForms[0].email")
				.setValueAttribute("sponsor@example.com");
		form.getInputByName("speakerForms[0].github").setValueAttribute("jjug-ccc");
		form.getTextAreaByName("speakerForms[0].bio").setText("色々やっています。");
		form.getSelectByName("speakerForms[0].activityList[0].activityType")
				.setSelectedAttribute("TWITTER", true);
		form.getInputByName("speakerForms[0].activityList[0].url")
				.setValueAttribute("https://twitter.com/jjug");
		return form;
	}
}
