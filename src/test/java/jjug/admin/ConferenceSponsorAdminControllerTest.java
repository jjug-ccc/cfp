package jjug.admin;

import java.util.Objects;

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
public class ConferenceSponsorAdminControllerTest extends MockGithubServerTest {
	@LocalServerPort
	int port;

	@Test
	public void testAddSponsor() throws Exception {
		this.server.setDispatcher(new MockGithubServerDispatcher(port,
				CfpUser.builder().name("Taro JJUG").github("jjug-cfp")
						.email("jjug-cfp@example.com")
						.avatarUrl("http://image.example.com/foo.jpg").build()));
		HtmlPage homePage = this.webClient.getPage("http://localhost:" + port);
		assertThat(homePage.asText()).contains("Test Conf 1 (2100/01/01)");
		HtmlPage confPage = homePage.getAnchorByText("Test Conf 1 (2100/01/01)").click();
		assertThat(confPage.asText()).contains("スポンサー\n" + //
				"\uD83D\uDCB0スポンサーを追加\n" + //
				"タイプ\tスポンサーID\t名前\tロゴ\tURL\tパスワード\t編集\n" + //
				"PLATINUM\ttest-sponsor\tテストスポンサー\t✅\t✅\t ✅ \t 編集フォーム \n" + //
				" パスワードリセット");

		HtmlPage sponsorCreatePage1 = confPage.getAnchorByText("\uD83D\uDCB0スポンサーを追加")
				.click();
		HtmlForm sponsorCreateForm1 = (HtmlForm) sponsorCreatePage1
				.getElementsByTagName("form").get(0);

		sponsorCreateForm1.getSelectByName("sponsorType").setSelectedAttribute("GOLD",
				true);
		sponsorCreateForm1.getInputByName("sponsorId").setValueAttribute("2018s-jjug");
		sponsorCreateForm1.getInputByName("sponsorName").setValueAttribute("JJUG");

		HtmlPage confPageAdded1 = sponsorCreateForm1.getInputByValue("追加").click();
		assertThat(confPageAdded1.asText())
				.contains("PLATINUM\ttest-sponsor\tテストスポンサー\t✅\t✅\t ✅ \t 編集フォーム \n" + //
						" パスワードリセット\n" + //
						"GOLD\t2018s-jjug\tJJUG\t\t\t 未設定 \t 編集フォーム \n" + //
						" パスワードリセット");

		HtmlPage sponsorCreatePage2 = confPageAdded1
				.getAnchorByText("\uD83D\uDCB0スポンサーを追加").click();
		HtmlForm sponsorCreateForm2 = (HtmlForm) sponsorCreatePage2
				.getElementsByTagName("form").get(0);

		sponsorCreateForm2.getSelectByName("sponsorType").setSelectedAttribute("DIAMOND",
				true);
		sponsorCreateForm2.getInputByName("sponsorId").setValueAttribute("2018s-diamond");
		sponsorCreateForm2.getInputByName("sponsorName").setValueAttribute("ダイヤ");

		HtmlPage confPageAdded2 = sponsorCreateForm2.getInputByValue("追加").click();
		assertThat(confPageAdded2.asText())
				.contains("DIAMOND\t2018s-diamond\tダイヤ\t\t\t 未設定 \t 編集フォーム \n" + //
						" パスワードリセット\n" + //
						"PLATINUM\ttest-sponsor\tテストスポンサー\t✅\t✅\t ✅ \t 編集フォーム \n" + //
						" パスワードリセット\n" + //
						"GOLD\t2018s-jjug\tJJUG\t\t\t 未設定 \t 編集フォーム \n" + //
						" パスワードリセット");
	}

	@Test
	public void testEditSponsor() throws Exception {
		this.server.setDispatcher(new MockGithubServerDispatcher(port,
				CfpUser.builder().name("Taro JJUG").github("jjug-cfp")
						.email("jjug-cfp@example.com")
						.avatarUrl("http://image.example.com/foo.jpg").build()));
		HtmlPage homePage = this.webClient.getPage("http://localhost:" + port);
		assertThat(homePage.asText()).contains("Test Conf 1 (2100/01/01)");
		HtmlPage confPage = homePage.getAnchorByText("Test Conf 1 (2100/01/01)").click();

		HtmlPage sponsorCreatePage1 = confPage.getAnchorByText("\uD83D\uDCB0スポンサーを追加")
				.click();
		HtmlForm sponsorCreateForm1 = (HtmlForm) sponsorCreatePage1
				.getElementsByTagName("form").get(0);

		sponsorCreateForm1.getSelectByName("sponsorType").setSelectedAttribute("GOLD",
				true);
		sponsorCreateForm1.getInputByName("sponsorId").setValueAttribute("2018s-jjug");
		sponsorCreateForm1.getInputByName("sponsorName").setValueAttribute("JJUG");

		HtmlPage confPageAdded1 = sponsorCreateForm1.getInputByValue("追加").click();
		HtmlPage editPage = confPageAdded1.getAnchors().stream()
				.filter(a -> Objects.equals(a.getTextContent(), "編集フォーム"))
				.toArray(HtmlAnchor[]::new)[1].click();

		HtmlForm editForm = (HtmlForm) editPage.getElementsByTagName("form").get(0);
		editForm.getSelectByName("sponsorType").setSelectedAttribute("PLATINUM", true);
		editForm.getInputByName("sponsorName").setValueAttribute("JJUG [変更]");
		editForm.getInputByName("sponsorLogoUrl").setValueAttribute("http://exampel.com");
		editForm.getInputByName("sponsorUrl").setValueAttribute("http://exampel.com");

		HtmlPage confPageEdited = editForm.getInputByValue("更新").click();
		assertThat(confPageEdited.asText())
				.contains("タイプ\tスポンサーID\t名前\tロゴ\tURL\tパスワード\t編集\n" + //
						"PLATINUM\t2018s-jjug\tJJUG [変更]\t✅\t✅\t 未設定 \t 編集フォーム \n" + //
						" パスワードリセット\n" + //
						"PLATINUM\ttest-sponsor\tテストスポンサー\t✅\t✅\t ✅ \t 編集フォーム \n" + //
						" パスワードリセット");
	}

	@Test
	public void testResetPassword() throws Exception {
		this.server.setDispatcher(new MockGithubServerDispatcher(port,
				CfpUser.builder().name("Taro JJUG").github("jjug-cfp")
						.email("jjug-cfp@example.com")
						.avatarUrl("http://image.example.com/foo.jpg").build()));
		HtmlPage homePage = this.webClient.getPage("http://localhost:" + port);
		assertThat(homePage.asText()).contains("Test Conf 1 (2100/01/01)");
		HtmlPage confPage = homePage.getAnchorByText("Test Conf 1 (2100/01/01)").click();

		HtmlPage sponsorCreatePage1 = confPage.getAnchorByText("\uD83D\uDCB0スポンサーを追加")
				.click();
		HtmlForm sponsorCreateForm1 = (HtmlForm) sponsorCreatePage1
				.getElementsByTagName("form").get(0);

		sponsorCreateForm1.getSelectByName("sponsorType").setSelectedAttribute("GOLD",
				true);
		sponsorCreateForm1.getInputByName("sponsorId").setValueAttribute("2018s-jjug");
		sponsorCreateForm1.getInputByName("sponsorName").setValueAttribute("JJUG");

		HtmlPage confPageAdded1 = sponsorCreateForm1.getInputByValue("追加").click();
		HtmlPage passwordResetPage = confPageAdded1.getAnchors().stream()
				.filter(a -> Objects.equals(a.getTextContent(), "パスワードリセット"))
				.toArray(HtmlAnchor[]::new)[1].click();
		assertThat(passwordResetPage.asText()).startsWith("Test Conf 1\n" + //
				"\n" + //
				"Test Conf 1 (2100/01/01)\n" + //
				"JJUG\n" + //
				" リセットURLを生成して、URLとスポンサーID(2018s-jjug)をスポンサーに伝えてください。\n" + //
				"リセットURL\t有効期限\t \n" + //
				"\n" + //
				" リセットURL生成 Test Conf 1 (2100/01/01)");

		HtmlPage passwordResetPageAdded = ((HtmlForm) passwordResetPage
				.getElementsByTagName("form").get(0)).getInputByValue("リセットURL生成")
						.click();
		assertThat(passwordResetPageAdded.asText()).contains("✅ \t 破棄");

		String resetUrl = passwordResetPageAdded.getAnchorByText("URL")
				.getHrefAttribute();

		HtmlPage resetPage = this.webClient
				.getPage("http://localhost:" + port + "/" + resetUrl);
		HtmlForm resetForm = (HtmlForm) resetPage.getElementsByTagName("form").get(0);
		assertThat(resetForm.asText()).contains("Test Conf 1");
		assertThat(resetForm.asText()).contains("JJUG 様");
		resetForm.getInputByName("sponsorId").setValueAttribute("2018s-jjug");
		resetForm.getInputByName("password").setValueAttribute("new-password");
		resetForm.getInputByName("passwordConfirm").setValueAttribute("new-password");
		resetForm.getInputByValue("リセット").click();

		HtmlPage updated = homePage.getAnchorByText("Test Conf 1 (2100/01/01)").click();
		assertThat(updated.asText())
				.contains("PLATINUM\ttest-sponsor\tテストスポンサー\t✅\t✅\t ✅ \t 編集フォーム \n" + //
						" パスワードリセット\n" + //
						"GOLD\t2018s-jjug\tJJUG\t\t\t ✅ \t 編集フォーム \n" + //
						" パスワードリセット");
	}

	@Test
	public void testAddExistingSponsor() throws Exception {
		this.server.setDispatcher(new MockGithubServerDispatcher(port,
				CfpUser.builder().name("Taro JJUG").github("jjug-cfp")
						.email("jjug-cfp@example.com")
						.avatarUrl("http://image.example.com/foo.jpg").build()));
		HtmlPage homePage = this.webClient.getPage("http://localhost:" + port);
		HtmlPage confPage = homePage.getAnchorByText("Test Conf 1 (2100/01/01)").click();
		HtmlPage sponsorCreatePage1 = confPage.getAnchorByText("\uD83D\uDCB0スポンサーを追加")
				.click();
		HtmlForm sponsorCreateForm1 = (HtmlForm) sponsorCreatePage1
				.getElementsByTagName("form").get(0);

		sponsorCreateForm1.getSelectByName("sponsorType").setSelectedAttribute("GOLD",
				true);
		sponsorCreateForm1.getInputByName("sponsorId").setValueAttribute("2018s-jjug");
		sponsorCreateForm1.getInputByName("sponsorName").setValueAttribute("JJUG");

		HtmlPage confPageAdded1 = sponsorCreateForm1.getInputByValue("追加").click();
		HtmlPage sponsorCreatePage2 = confPageAdded1
				.getAnchorByText("\uD83D\uDCB0スポンサーを追加").click();
		HtmlForm sponsorCreateForm2 = (HtmlForm) sponsorCreatePage2
				.getElementsByTagName("form").get(0);

		sponsorCreateForm2.getSelectByName("sponsorType").setSelectedAttribute("DIAMOND",
				true);
		sponsorCreateForm2.getInputByName("sponsorId").setValueAttribute("2018s-jjug");
		sponsorCreateForm2.getInputByName("sponsorName").setValueAttribute("ダイヤ");

		HtmlPage confPageAdded2 = sponsorCreateForm2.getInputByValue("追加").click();
		assertThat(confPageAdded2.asText()).contains("sponsorIdは既に使用されています");
	}
}