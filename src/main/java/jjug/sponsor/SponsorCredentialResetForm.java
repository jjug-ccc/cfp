package jjug.sponsor;

import java.io.Serializable;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.terasoluna.gfw.common.validator.constraints.Compare;

@Compare(left = "passwordConfirm", right = "password", operator = Compare.Operator.EQUAL, message = "パスワード(確認)とパスワードの値が一致しません")
public class SponsorCredentialResetForm implements Serializable {
	@NotEmpty
	private String sponsorId;
	@NotEmpty
	@Size(min = 6, max = 255)
	private String password;
	@NotEmpty
	@Size(max = 255)
	private String passwordConfirm;

	public String getSponsorId() {
		return sponsorId;
	}

	public void setSponsorId(String sponsorId) {
		this.sponsorId = sponsorId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPasswordConfirm() {
		return passwordConfirm;
	}

	public void setPasswordConfirm(String passwordConfirm) {
		this.passwordConfirm = passwordConfirm;
	}
}
