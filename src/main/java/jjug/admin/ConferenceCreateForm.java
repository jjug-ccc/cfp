package jjug.admin;

import java.time.LocalDate;

import am.ik.yavi.constraint.Constraint;
import am.ik.yavi.core.Validator;
import jjug.conference.Conference;
import jjug.conference.enums.ConfStatus;

import org.springframework.format.annotation.DateTimeFormat;

import static am.ik.yavi.constraint.charsequence.CodePoints.Range;

public interface ConferenceCreateForm {
	String getConfName();

	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	LocalDate getConfDate();

	Validator<ConferenceCreateForm> validator = Validator.<ConferenceCreateForm>builder()
			.constraint(ConferenceCreateForm::getConfName, "confName", c -> c.notBlank() //
					.lessThanOrEqual(64) //
					.codePoints(Range.of("a".codePointAt(0), "z".codePointAt(0)),
							Range.of("A".codePointAt(0), "Z".codePointAt(0)),
							Range.of("0".codePointAt(0), "9".codePointAt(0)),
							Range.single("-".codePointAt(0)),
							Range.single(" ".codePointAt(0)))
					.asWhiteList().asByteArray().lessThanOrEqual(255))
			.constraintOnObject(ConferenceCreateForm::getConfDate, "confDate",
					Constraint::notNull)
			.build();

	default Conference toConference() {
		return Conference.builder() //
				.confName(this.getConfName()) //
				.confDate(this.getConfDate()) //
				.confCfpNote(this.getConfDate()
						+ "に行われるJJUG CCC 2018 SpringのCfPを下記フォームをご入力の上、ご提出ください。  \n" + //
						"提出にはGitHubによるログインが必須です。  \n" + //
						" \n" + //
						"#### 締め切り  \n" + //
						" \n" + //
						"**TBD** \n" + //
						"#### CfP作成方法  \n" + //
						" \n" + //
						"フォーム入力後に\"Submit CFP\"ボタンクリックで提出完了です。\"Save as Draft\"ボタンをクリックした場合は提出とみなされません。  \n"
						+ //
						"GitHubで[ログイン](/)後、応募済みのCfPを編集可能です。  \n" + //
						"CfPを取り下げたい場合は\"Withdraw CFP\"ボタンをクリックしてください。  \n") //
				.confVoteNote("投票による希望セッションのアンケートは**実施しません**。") //
				.confStatus(ConfStatus.DRAFT_CFP) //
				.build();
	}
}
