package jjug.submission;

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
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

import javax.mail.internet.MimeMessage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = { "classpath:/delete-test-data.sql",
		"classpath:/insert-test-data.sql" }, executionPhase = BEFORE_TEST_METHOD)
public class SubmissionControllerTest extends MockGithubServerTest {
	@LocalServerPort
	int port;
	@Rule
	public final GreenMailRule greenMail = new GreenMailRule(ServerSetupTest.SMTP);

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
				"セッションの補足情報\t\n" + //
				"講演者情報1\n" + //
				"名前\tFoo Bar\n" + //
				"GitHubアカウント\tfoo\n" + //
				"所属\t\n" + //
				"講演者紹介\t\n" + //
				"プロフィール画像URL\thttps://avatars.githubusercontent.com/foo?size=120\n" + //
				"コミュニティ活動、BlogのURL、Twitterアカウントなど1\tGithub https://github.com/foo\n" + //
				"コミュニティ活動、BlogのURL、Twitterアカウントなど2\t\n" + //
				"コミュニティ活動、BlogのURL、Twitterアカウントなど3\t\n" + //
				"コミュニティ活動、BlogのURL、Twitterアカウントなど4\t\n" + //
				"コミュニティ活動、BlogのURL、Twitterアカウントなど5\t\n" + //
				"非公開情報1\n" + //
				"Email\tfoo@example.com\n" + //
				"交通費支給を希望\tunchecked\n" + //
				"国・市町村（交通費支給を要する場合)\t\n" + //
				"事務局へのコメントなど");
	}

	@Test
	public void submitSubmission() throws Exception {
		this.server.setDispatcher(new MockGithubServerDispatcher(port,
				CfpUser.builder().name("Foo Bar").github("foo").email("foo@example.com")
						.avatarUrl("http://image.example.com/foo.jpg").build()));
		HtmlPage createForm = this.webClient.getPage("http://localhost:" + port
				+ "/conferences/00000000-0000-0000-0000-000021000101/submissions/form");
		HtmlPage submit = this.submitASubmission(createForm);
		assertThat(submit.asText()).contains("[Test Conf 1] テストセッション [応募済]");
		this.checkCreatedEmail();
		HtmlPage editForm = this.showEditForm(submit);
		this.showCreatedPreview(editForm);
		HtmlPage nothingChanged = this.submitASubmission(editForm);
		this.showCreatedPreview(nothingChanged);
		this.checkUpdatedEmailNothingChanged();
	}

	@Test
	public void submitAddSpeakerSubmission() throws Exception {
		this.server.setDispatcher(new MockGithubServerDispatcher(port,
				CfpUser.builder().name("Foo Bar").github("foo").email("foo@example.com")
						.avatarUrl("http://image.example.com/foo.jpg").build()));
		HtmlPage createForm = this.webClient.getPage("http://localhost:" + port
				+ "/conferences/00000000-0000-0000-0000-000021000101/submissions/form");
		HtmlPage addSubmit = this.addSpeakerForCreate(createForm);
		HtmlPage removeSpeaker = this.removeSpeaker(addSubmit);
		HtmlPage submit = this.addSpeakerForCreate(removeSpeaker);
		submit = submitAMultiSpeakerSubmission(submit);
		assertThat(submit.asText()).contains("[Test Conf 1] テストセッション [応募済]");
		this.checkMultiSpeakerCreatedEmail();
		HtmlPage editForm = this.showMultiSpeakerEditForm(submit);
		this.showMultiSpeakerCreatedPreview(editForm);
		HtmlPage nothingChanged = this.submitAMultiSpeakerSubmission(editForm);
		this.showMultiSpeakerCreatedPreview(nothingChanged);
	}

	@Test
	public void submitRemoveSpeakerSubmission() throws Exception {
		this.server.setDispatcher(new MockGithubServerDispatcher(port,
				CfpUser.builder().name("Foo Bar").github("foo").email("foo@example.com")
						.avatarUrl("http://image.example.com/foo.jpg").build()));
		HtmlPage createForm = this.webClient.getPage("http://localhost:" + port
				+ "/conferences/00000000-0000-0000-0000-000021000101/submissions/form");
		HtmlPage create = this.addSpeakerForCreate(createForm);
		create = submitAMultiSpeakerSubmission(create);
		assertThat(create.asText()).contains("[Test Conf 1] テストセッション [応募済]");
		this.checkMultiSpeakerCreatedEmail();
		HtmlPage editForm = this.showMultiSpeakerEditForm(create);
		this.showMultiSpeakerCreatedPreview(editForm);
		HtmlPage removedSpeakerSubmit = this.removeSpeaker(editForm);
		HtmlPage addSpeakerSubmit = this.addSpeakerForEdit(removedSpeakerSubmit);
		HtmlPage submit = this.removeSpeaker(addSpeakerSubmit);
		assertThat(submit.asText()).startsWith("Test Conf 1\n" + //
				"\n" + //
				"Test Conf 1 (2100/01/01)\n" + //
				"Japanese English\n" + //
				"\n" + //
				"Call for Papers\n" + //
				"Status: [応募済]\n" + //
				"講演情報\n" + //
				"タイトル\tテストセッション\n" + //
				"概要\tテスト用のセッションです。\n" + //
				"想定している聴講者層\tテストユーザーを対象としています。\n" + //
				"カテゴリ\tServer Side Java\n" + //
				"難易度\t上級者向け\n" + //
				"種類\t一般枠 (20分)\n" + //
				"言語\t日本語\n" + //
				"セッションの補足情報\t\n" + //
				"講演者情報1\n" + //
				"名前\tFoo Bar\n" + //
				"GitHubアカウント\tfoo\n" + //
				"所属\tJJUG\n" + //
				"講演者紹介\t色々やっています。\n" + //
				"プロフィール画像URL\thttps://avatars.githubusercontent.com/foo?size=120\n" + //
				"コミュニティ活動、BlogのURL、Twitterアカウントなど1\tTwitter https://twitter.com/jjug\n" + //
				"コミュニティ活動、BlogのURL、Twitterアカウントなど2\t\n" + //
				"コミュニティ活動、BlogのURL、Twitterアカウントなど3\t\n" + //
				"コミュニティ活動、BlogのURL、Twitterアカウントなど4\t\n" + //
				"コミュニティ活動、BlogのURL、Twitterアカウントなど5\t\n" + //
				"非公開情報1\n" + //
				"Email\tfoo@example.com\n" + //
				"交通費支給を希望\tunchecked\n" + //
				"国・市町村（交通費支給を要する場合)\t\n" + //
				"事務局へのコメントなど");
		HtmlPage nothingChanged = this.submitASubmission(removedSpeakerSubmit);
		this.showCreatedPreview(nothingChanged);
		this.checkUpdatedEmailNothingChanged();
	}

	@Test
	public void editSubmission() throws Exception {
		this.server.setDispatcher(new MockGithubServerDispatcher(port,
				CfpUser.builder().name("Foo Bar").github("foo").email("foo@example.com")
						.avatarUrl("http://image.example.com/foo.jpg").build()));
		HtmlPage createForm = this.webClient.getPage("http://localhost:" + port
				+ "/conferences/00000000-0000-0000-0000-000021000101/submissions/form");
		HtmlPage submit = this.submitASubmission(createForm);
		assertThat(submit.asText()).contains("[Test Conf 1] テストセッション [応募済]");
		this.checkCreatedEmail();
		HtmlPage editForm = this.showEditForm(submit);
		HtmlPage editedForm = this.editTheSubmission(editForm);
		assertThat(editedForm.asText())
				.startsWith("Test Conf 1\n" + "\n" + "Test Conf 1 (2100/01/01)\n" + //
						"Japanese English\n" + //
						"\n" + //
						"Call for Papers\n" + //
						"Status: [応募済]\n" + //
						"講演情報\n" + //
						"タイトル\tテストセッション[変更]\n" + //
						"概要\tテスト用のセッションです。[変更]\n" + //
						"想定している聴講者層\tテストユーザーを対象としています。[変更]\n" + //
						"カテゴリ\tJVM\n" + //
						"難易度\t中級者向け\n" + //
						"種類\t初心者枠 (45分)\n" + //
						"言語\t英語\n" + //
						"セッションの補足情報\t\n" + //
						"講演者情報1\n" + //
						"名前\tFoo Bar\n" + //
						"GitHubアカウント\tfoo\n" + //
						"所属\tJJUG[変更]\n" + //
						"講演者紹介\t色々やっています。[変更]\n" + //
						"プロフィール画像URL\thttps://avatars.githubusercontent.com/foo?size=120\n" + //
						"コミュニティ活動、BlogのURL、Twitterアカウントなど1\tTwitter https://twitter.com/jjug[変更]\n" + //
						"コミュニティ活動、BlogのURL、Twitterアカウントなど2\t\n" + //
						"コミュニティ活動、BlogのURL、Twitterアカウントなど3\t\n" + //
						"コミュニティ活動、BlogのURL、Twitterアカウントなど4\t\n" + //
						"コミュニティ活動、BlogのURL、Twitterアカウントなど5\t\n" + //
						"非公開情報1\n" + //
						"Email\tfoo@example.com\n" + //
						"交通費支給を希望\tunchecked\n" + //
						"国・市町村（交通費支給を要する場合)\t\n" + //
						"事務局へのコメントなど");
		this.checkUpdatedEmailChanged();
		this.showUpdatedPreview(editedForm);
	}

	private HtmlPage editTheSubmission(HtmlPage submission) throws Exception {
		HtmlForm form = (HtmlForm) submission.getElementsByTagName("form").get(0);
		form.getInputByName("title").setValueAttribute("テストセッション[変更]");
		form.getTextAreaByName("description").setText("テスト用のセッションです。[変更]");
		form.getInputByName("target").setValueAttribute("テストユーザーを対象としています。[変更]");
		form.getSelectByName("category").setSelectedAttribute("JVM", true);
		form.getSelectByName("level").setSelectedAttribute("INTERMEDIATE", true);
		form.getSelectByName("talkType").setSelectedAttribute("BEGINNER_STANDARD", true);
		form.getSelectByName("language").setSelectedAttribute("ENGLISH", true);
		form.getInputByName("speakerForms[0].companyOrCommunity")
				.setValueAttribute("JJUG[変更]");
		form.getTextAreaByName("speakerForms[0].bio").setText("色々やっています。[変更]");
		form.getSelectByName("speakerForms[0].activityList[0].activityType").setSelectedAttribute("TWITTER", true);
		form.getInputByName("speakerForms[0].activityList[0].url").setValueAttribute("https://twitter.com/jjug[変更]");
		HtmlPage submit = form.getInputsByValue("Submit CFP").get(0).click();
		return submit;
	}

	private HtmlPage submitASubmission(HtmlPage submission) throws Exception {
		HtmlForm inputSessionForm = inputSession(submission);
		HtmlForm form = inputSpeaker1(inputSessionForm);
		HtmlPage submit = form.getInputsByValue("Submit CFP").get(0).click();
		return submit;
	}

	private HtmlPage addSpeakerForCreate(HtmlPage submission) throws Exception {
		HtmlForm form = (HtmlForm) submission.getElementsByTagName("form").get(0);
		HtmlPage addSpeakerForm = form.getInputsByName("add-speaker").get(0).click();
		assertThat(addSpeakerForm.asText()).startsWith("Test Conf 1\n" + //
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
				"セッションの補足情報\t\n" + //
				"講演者情報1\n" + //
				"名前\tFoo Bar\n" + //
				"GitHubアカウント\tfoo\n" + //
				"所属\t\n" + //
				"講演者紹介\t\n" + //
				"プロフィール画像URL\thttps://avatars.githubusercontent.com/foo?size=120\n" + //
				"コミュニティ活動、BlogのURL、Twitterアカウントなど1\tGithub https://github.com/foo\n" + //
				"コミュニティ活動、BlogのURL、Twitterアカウントなど2\t\n" + //
				"コミュニティ活動、BlogのURL、Twitterアカウントなど3\t\n" + //
				"コミュニティ活動、BlogのURL、Twitterアカウントなど4\t\n" + //
				"コミュニティ活動、BlogのURL、Twitterアカウントなど5\t\n" + //
				"非公開情報1\n" + //
				"Email\tfoo@example.com\n" + //
				"交通費支給を希望\tunchecked\n" + //
				"国・市町村（交通費支給を要する場合)\t\n" + //
				"事務局へのコメントなど\t\n" + //
				"講演者情報2\n" + //
				"名前\t\n" + //
				"GitHubアカウント\t\n" + //
				"所属\t\n" + //
				"講演者紹介\t\n" + //
				"プロフィール画像URL\t\n" + //
				"コミュニティ活動、BlogのURL、Twitterアカウントなど1\t(Required)\n" + //
				"コミュニティ活動、BlogのURL、Twitterアカウントなど2\t\n" + //
				"コミュニティ活動、BlogのURL、Twitterアカウントなど3\t\n" + //
				"コミュニティ活動、BlogのURL、Twitterアカウントなど4\t\n" + //
				"コミュニティ活動、BlogのURL、Twitterアカウントなど5\t\n" + //
				"非公開情報2\n" + //
				"Email\t\n" + //
				"交通費支給を希望\tunchecked\n" + //
				"国・市町村（交通費支給を要する場合)\t\n" + //
				"事務局へのコメントなど");
		return addSpeakerForm;
	}

	private HtmlPage addSpeakerForEdit(HtmlPage submission) throws Exception {
		HtmlForm form = (HtmlForm) submission.getElementsByTagName("form").get(0);
		HtmlPage addSpeakerForm = form.getInputsByName("add-speaker").get(0).click();
		assertThat(addSpeakerForm.asText()).startsWith("Test Conf 1\n" + //
				"\n" + //
				"Test Conf 1 (2100/01/01)\n" + //
				"Japanese English\n" + //
				"\n" + //
				"Call for Papers\n" + //
				"Status: [応募済]\n" + //
				"講演情報\n" + //
				"タイトル\tテストセッション\n" + //
				"概要\tテスト用のセッションです。\n" + //
				"想定している聴講者層\tテストユーザーを対象としています。\n" + //
				"カテゴリ\tServer Side Java\n" + //
				"難易度\t上級者向け\n" + //
				"種類\t一般枠 (20分)\n" + //
				"言語\t日本語\n" + //
				"セッションの補足情報\t\n" + //
				"講演者情報1\n" + //
				"名前\tFoo Bar\n" + //
				"GitHubアカウント\tfoo\n" + //
				"所属\tJJUG\n" + //
				"講演者紹介\t色々やっています。\n" + //
				"プロフィール画像URL\thttps://avatars.githubusercontent.com/foo?size=120\n" + //
				"コミュニティ活動、BlogのURL、Twitterアカウントなど1\tTwitter https://twitter.com/jjug\n" + //
				"コミュニティ活動、BlogのURL、Twitterアカウントなど2\t\n" + //
				"コミュニティ活動、BlogのURL、Twitterアカウントなど3\t\n" + //
				"コミュニティ活動、BlogのURL、Twitterアカウントなど4\t\n" + //
				"コミュニティ活動、BlogのURL、Twitterアカウントなど5\t\n" + //
				"非公開情報1\n" + //
				"Email\tfoo@example.com\n" + //
				"交通費支給を希望\tunchecked\n" + //
				"国・市町村（交通費支給を要する場合)\t\n" + //
				"事務局へのコメントなど\t\n" + //
				"講演者情報2\n" + //
				"名前\t\n" + //
				"GitHubアカウント\t\n" + //
				"所属\t\n" + //
				"講演者紹介\t\n" + //
				"プロフィール画像URL\t\n" + //
				"コミュニティ活動、BlogのURL、Twitterアカウントなど1\t(Required)\n" + //
				"コミュニティ活動、BlogのURL、Twitterアカウントなど2\t\n" + //
				"コミュニティ活動、BlogのURL、Twitterアカウントなど3\t\n" + //
				"コミュニティ活動、BlogのURL、Twitterアカウントなど4\t\n" + //
				"コミュニティ活動、BlogのURL、Twitterアカウントなど5\t\n" + //
				"非公開情報2\n" + //
				"Email\t\n" + //
				"交通費支給を希望\tunchecked\n" + //
				"国・市町村（交通費支給を要する場合)\t\n" + //
				"事務局へのコメントなど");
		return addSpeakerForm;
	}

	private HtmlPage removeSpeaker(HtmlPage submission) throws Exception {
		HtmlForm form = (HtmlForm) submission.getElementsByTagName("form").get(0);
		return form.getInputsByName("remove-speaker").get(0).click();
	}

	private HtmlForm inputSession(HtmlPage submission) {
		HtmlForm form = (HtmlForm) submission.getElementsByTagName("form").get(0);
		form.getInputByName("title").setValueAttribute("テストセッション");
		form.getTextAreaByName("description").setText("テスト用のセッションです。");
		form.getInputByName("target").setValueAttribute("テストユーザーを対象としています。");
		form.getSelectByName("category").setSelectedAttribute("SERVER_SIDE_JAVA", true);
		form.getSelectByName("level").setSelectedAttribute("ADVANCED", true);
		form.getSelectByName("talkType").setSelectedAttribute("SHORT", true);
		form.getSelectByName("language").setSelectedAttribute("JAPANESE", true);
		return form;
	}

	private HtmlForm inputSpeaker1(HtmlForm form) {
		form.getInputByName("speakerForms[0].companyOrCommunity")
				.setValueAttribute("JJUG");
		form.getTextAreaByName("speakerForms[0].bio").setText("色々やっています。");
		form.getSelectByName("speakerForms[0].activityList[0].activityType").setSelectedAttribute("TWITTER", true);
		form.getInputByName("speakerForms[0].activityList[0].url").setValueAttribute("https://twitter.com/jjug");
		return form;
	}

	private HtmlPage submitAMultiSpeakerSubmission(HtmlPage submission) throws Exception {
		HtmlForm inputSessionForm = inputSession(submission);
		HtmlForm form = inputSpeaker1(inputSessionForm);
		form.getInputByName("speakerForms[1].name").setValueAttribute("Hoge Bar");
		form.getInputByName("speakerForms[1].companyOrCommunity")
				.setValueAttribute("Java女子部");
		form.getInputByName("speakerForms[1].github").setValueAttribute("hoge");
		form.getTextAreaByName("speakerForms[1].bio").setText("色々やっています。");
		form.getSelectByName("speakerForms[1].activityList[0].activityType").setSelectedAttribute("TWITTER", true);
		form.getInputByName("speakerForms[1].activityList[0].url").setValueAttribute("https://twitter.com/java_women");
		form.getSelectByName("speakerForms[1].activityList[1].activityType").setSelectedAttribute("OTHER", true);
		form.getInputByName("speakerForms[1].activityList[1].url").setValueAttribute("https://javajo.doorkeeper.jp/");
		form.getInputByName("speakerForms[1].profileUrl")
				.setValueAttribute("https://avatars.githubusercontent.com/hoge?size=120");
		form.getInputByName("speakerForms[1].email").setValueAttribute("hoge@example.com");
		HtmlPage submit = form.getInputsByValue("Submit CFP").get(0).click();
		return submit;
	}

	private HtmlPage showEditForm(HtmlPage submit) throws Exception {
		HtmlPage submission = submit.getAnchorByText("テストセッション").click();
		assertThat(submission.asText()).startsWith("Test Conf 1\n" + //
				"\n" + //
				"Test Conf 1 (2100/01/01)\n" + //
				"Japanese English\n" + //
				"\n" + //
				"Call for Papers\n" + //
				"Status: [応募済]\n" + //
				"講演情報\n" + //
				"タイトル\tテストセッション\n" + //
				"概要\tテスト用のセッションです。\n" + //
				"想定している聴講者層\tテストユーザーを対象としています。\n" + //
				"カテゴリ\tServer Side Java\n" + //
				"難易度\t上級者向け\n" + //
				"種類\t一般枠 (20分)\n" + //
				"言語\t日本語\n" + //
				"セッションの補足情報\t\n" + //
				"講演者情報1\n" + //
				"名前\tFoo Bar\n" + //
				"GitHubアカウント\tfoo\n" + //
				"所属\tJJUG\n" + //
				"講演者紹介\t色々やっています。\n" + //
				"プロフィール画像URL\thttps://avatars.githubusercontent.com/foo?size=120\n" + //
				"コミュニティ活動、BlogのURL、Twitterアカウントなど1\tTwitter https://twitter.com/jjug\n" + //
				"コミュニティ活動、BlogのURL、Twitterアカウントなど2\t\n" + //
				"コミュニティ活動、BlogのURL、Twitterアカウントなど3\t\n" + //
				"コミュニティ活動、BlogのURL、Twitterアカウントなど4\t\n" + //
				"コミュニティ活動、BlogのURL、Twitterアカウントなど5\t\n" + //
				"非公開情報1\n" + //
				"Email\tfoo@example.com\n" + //
				"交通費支給を希望\tunchecked\n" + //
				"国・市町村（交通費支給を要する場合)\t\n" + //
				"事務局へのコメントなど\t\n" + //
				" スピーカーを増やす");
		return submission;
	}

	private HtmlPage showMultiSpeakerEditForm(HtmlPage submit) throws Exception {
		HtmlPage submission = submit.getAnchorByText("テストセッション").click();
		assertThat(submission.asText()).startsWith("Test Conf 1\n" + //
				"\n" + //
				"Test Conf 1 (2100/01/01)\n" + //
				"Japanese English\n" + //
				"\n" + //
				"Call for Papers\n" + //
				"Status: [応募済]\n" + //
				"講演情報\n" + //
				"タイトル\tテストセッション\n" + //
				"概要\tテスト用のセッションです。\n" + //
				"想定している聴講者層\tテストユーザーを対象としています。\n" + //
				"カテゴリ\tServer Side Java\n" + //
				"難易度\t上級者向け\n" + //
				"種類\t一般枠 (20分)\n" + //
				"言語\t日本語\n" + //
				"セッションの補足情報\t\n" + //
				"講演者情報1\n" + //
				"名前\tFoo Bar\n" + //
				"GitHubアカウント\tfoo\n" + //
				"所属\tJJUG\n" + //
				"講演者紹介\t色々やっています。\n" + //
				"プロフィール画像URL\thttps://avatars.githubusercontent.com/foo?size=120\n" + //
				"コミュニティ活動、BlogのURL、Twitterアカウントなど1\tTwitter https://twitter.com/jjug\n" + //
				"コミュニティ活動、BlogのURL、Twitterアカウントなど2\t\n" + //
				"コミュニティ活動、BlogのURL、Twitterアカウントなど3\t\n" + //
				"コミュニティ活動、BlogのURL、Twitterアカウントなど4\t\n" + //
				"コミュニティ活動、BlogのURL、Twitterアカウントなど5\t\n" + //
				"非公開情報1\n" + //
				"Email\tfoo@example.com\n" + //
				"交通費支給を希望\tunchecked\n" + //
				"国・市町村（交通費支給を要する場合)\t\n" + //
				"事務局へのコメントなど\t\n" + //
				"講演者情報2\n" + //
				"名前\tHoge Bar\n" + //
				"GitHubアカウント\thoge\n" + //
				"所属\tJava女子部\n" + //
				"講演者紹介\t色々やっています。\n" + //
				"プロフィール画像URL\thttps://avatars.githubusercontent.com/hoge?size=120\n" + //
				"コミュニティ活動、BlogのURL、Twitterアカウントなど1\tTwitter https://twitter.com/java_women\n" + //
				"コミュニティ活動、BlogのURL、Twitterアカウントなど2\tその他 https://javajo.doorkeeper.jp/\n" + //
				"コミュニティ活動、BlogのURL、Twitterアカウントなど3\t\n" + //
				"コミュニティ活動、BlogのURL、Twitterアカウントなど4\t\n" + //
				"コミュニティ活動、BlogのURL、Twitterアカウントなど5\t\n" + //
				"非公開情報2\n" + //
				"Email\thoge@example.com\n" + //
				"交通費支給を希望\tunchecked\n" + //
				"国・市町村（交通費支給を要する場合)\t\n" + //
				"事務局へのコメントなど");
		return submission;
	}

	private HtmlPage showCreatedPreview(HtmlPage submission) throws Exception {
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
				"日本語\n" + //
				"講演者情報\n" + //
				"\n" + //
				"\n" + //
				"Foo Bar \n" + //
				" JJUG\n" + //
				"色々やっています。\n" + //
				"\n" + //
				"https://twitter.com/jjug");
		return preview;
	}

	private HtmlPage showMultiSpeakerCreatedPreview(HtmlPage submission) throws Exception {
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
				"日本語\n" + //
				"講演者情報\n" + //
				"\n" + //
				"\n" + //
				"Foo Bar \n" + //
				" JJUG\n" + //
				"色々やっています。\n" + //
				"\n" + //
				"https://twitter.com/jjug\n" + //
				"\n" + //
				"\n" + //
				"\n" + //
				"Hoge Bar \n" + //
				" Java女子部\n" + //
				"色々やっています。\n" + //
				"\n" + //
				"https://twitter.com/java_women\n" + //
				" https://javajo.doorkeeper.jp/");
		return preview;
	}

	private HtmlPage showUpdatedPreview(HtmlPage submission) throws Exception {
		HtmlPage preview = ((HtmlAnchor) submission.querySelector("a[target=_blank]"))
				.click();
		assertThat(preview.asText()).startsWith("テストセッション[変更]\n" + //
				"\n" + //
				"テストセッション[変更]\n" + //
				"講演情報\n" + //
				"タイトル\n" + //
				"テストセッション[変更]\n" + //
				"概要\n" + //
				"テスト用のセッションです。[変更]\n" + //
				"\n" + //
				"想定している聴講者層\n" + //
				"テストユーザーを対象としています。[変更]\n" + //
				"カテゴリ\n" + //
				"JVM\n" + //
				"難易度\n" + //
				"中級者向け\n" + //
				"種類\n" + //
				"初心者枠 (45分)\n" + //
				"言語\n" + //
				"英語\n" + //
				"講演者情報\n" + //
				"\n" + //
				"\n" + //
				"Foo Bar \n" + //
				" JJUG[変更]\n" + //
				"色々やっています。[変更]\n" + //
				"\n" + //
				"https://twitter.com/jjug[変更]");
		return preview;
	}

	private void checkCreatedEmail() throws Exception {
		assertThat(greenMail.waitForIncomingEmail(3000, 1)).isTrue();
		MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
		assertThat(receivedMessages.length).isEqualTo(1);
		assertThat(receivedMessages[0].getContent().toString()).startsWith("Foo Bar様\n" + //
				"Call for Paperへのご応募ありがとうございます。 \n" + //
				"タイトル: テストセッション \n" + //
				"\n" + //
				"応募内容は下記URLより変更可能です。\n" + //
				"http://localhost:" + port + "/submissions/");
		assertThat(receivedMessages[0].getSubject())
				.isEqualTo("[Test Conf 1] Call for Paperへのご応募ありがとうございます。");
		assertThat(receivedMessages[0].getFrom().length).isEqualTo(1);
		assertThat(receivedMessages[0].getFrom()[0].toString())
				.isEqualTo("office@java-users.jp");
		assertThat(receivedMessages[0].getAllRecipients().length).isEqualTo(1);
		assertThat(receivedMessages[0].getAllRecipients()[0].toString())
				.isEqualTo("foo@example.com");
		greenMail.reset();
	}

	private void checkMultiSpeakerCreatedEmail() throws Exception {
		assertThat(greenMail.waitForIncomingEmail(3000, 1)).isTrue();
		MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
		assertThat(receivedMessages.length).isEqualTo(2);
		assertThat(receivedMessages[0].getContent().toString()).startsWith("Foo Bar様, Hoge Bar様\n" + //
				"Call for Paperへのご応募ありがとうございます。 \n" + //
				"タイトル: テストセッション \n" + //
				"\n" + //
				"応募内容は下記URLより変更可能です。\n" + //
				"http://localhost:" + port + "/submissions/");
		assertThat(receivedMessages[0].getSubject())
				.isEqualTo("[Test Conf 1] Call for Paperへのご応募ありがとうございます。");
		assertThat(receivedMessages[0].getFrom().length).isEqualTo(1);
		assertThat(receivedMessages[0].getFrom()[0].toString())
				.isEqualTo("office@java-users.jp");
		assertThat(receivedMessages[0].getAllRecipients().length).isEqualTo(2);
		assertThat(receivedMessages[0].getAllRecipients()[0].toString())
				.isEqualTo("foo@example.com");
		assertThat(receivedMessages[1].getContent().toString()).startsWith("Foo Bar様, Hoge Bar様\n" + //
				"Call for Paperへのご応募ありがとうございます。 \n" + //
				"タイトル: テストセッション \n" + //
				"\n" + //
				"応募内容は下記URLより変更可能です。\n" + //
				"http://localhost:" + port + "/submissions/");
		assertThat(receivedMessages[1].getSubject())
				.isEqualTo("[Test Conf 1] Call for Paperへのご応募ありがとうございます。");
		assertThat(receivedMessages[1].getFrom().length).isEqualTo(1);
		assertThat(receivedMessages[1].getFrom()[0].toString())
				.isEqualTo("office@java-users.jp");
		assertThat(receivedMessages[1].getAllRecipients().length).isEqualTo(2);
		assertThat(receivedMessages[1].getAllRecipients()[1].toString())
				.isEqualTo("hoge@example.com");
		greenMail.reset();
	}

	private void checkUpdatedEmailChanged() throws Exception {
		assertThat(greenMail.waitForIncomingEmail(3000, 1)).isTrue();
		MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
		assertThat(receivedMessages.length).isEqualTo(1);
		assertThat(receivedMessages[0].getContent().toString()).startsWith("Foo Bar様\n" + //
				"Call for Paperが変更されました。 \n" + //
				"タイトル: テストセッション[変更] \n" + //
				"\n" + //
				"応募内容は下記URLより変更可能です。\n" + //
				"http://localhost:" + port + "/submissions/");
		assertThat(receivedMessages[0].getSubject())
				.isEqualTo("[Test Conf 1] Call for Paperが変更されました。");
		assertThat(receivedMessages[0].getFrom().length).isEqualTo(1);
		assertThat(receivedMessages[0].getFrom()[0].toString())
				.isEqualTo("office@java-users.jp");
		assertThat(receivedMessages[0].getAllRecipients().length).isEqualTo(1);
		assertThat(receivedMessages[0].getAllRecipients()[0].toString())
				.isEqualTo("foo@example.com");
		greenMail.reset();
	}

	private void checkUpdatedEmailNothingChanged() throws Exception {
		assertThat(greenMail.waitForIncomingEmail(3000, 1)).isTrue();
		MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
		assertThat(receivedMessages.length).isEqualTo(1);
		assertThat(receivedMessages[0].getContent().toString()).startsWith("Foo Bar様\n" + //
				"Call for Paperが変更されました。 \n" + //
				"タイトル: テストセッション \n" + //
				"\n" + //
				"応募内容は下記URLより変更可能です。\n" + //
				"http://localhost:" + port + "/submissions/");
		assertThat(receivedMessages[0].getSubject())
				.isEqualTo("[Test Conf 1] Call for Paperが変更されました。");
		assertThat(receivedMessages[0].getFrom().length).isEqualTo(1);
		assertThat(receivedMessages[0].getFrom()[0].toString())
				.isEqualTo("office@java-users.jp");
		assertThat(receivedMessages[0].getAllRecipients().length).isEqualTo(1);
		assertThat(receivedMessages[0].getAllRecipients()[0].toString())
				.isEqualTo("foo@example.com");
		greenMail.reset();
	}
}
