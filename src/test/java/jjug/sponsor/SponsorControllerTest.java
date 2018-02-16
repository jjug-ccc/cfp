package jjug.sponsor;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebClientOptions;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.junit.After;
import org.junit.Before;
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
public class SponsorControllerTest {
	@LocalServerPort
	int port;
	WebClient webClient;

	@Before
	public void setup() throws Exception {
		this.webClient = new WebClient();
		WebClientOptions options = this.webClient.getOptions();
		options.setCssEnabled(false);
		options.setJavaScriptEnabled(false);
		options.setThrowExceptionOnFailingStatusCode(false);
	}

	@After
	public void shutdown() throws Exception {
		this.webClient.close();
	}

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
		assertThat(submissionFormPage.asText()).startsWith("Test Conf 1\n" + //
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
				" ❗ Githubアカウントがない場合は、\"GitHubアカウント\"にjjug-cfpを入力してください。 ❗\n" + //
				"名前\tテストスポンサー\n" + //
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

	private HtmlPage login(String sponsorId, String password) throws Exception {
		HtmlPage loginPage = this.webClient
				.getPage("http://localhost:" + port + "/sponsors/login");
		HtmlForm loginForm = (HtmlForm) loginPage.getElementsByTagName("form").get(0);
		loginForm.getInputByName("sponsorId").setValueAttribute(sponsorId);
		loginForm.getInputByName("password").setValueAttribute(password);
		return loginForm.getInputByValue("ログイン").click();
	}
}
