package jjug.admin;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import jjug.sponsor.enums.SponsorType;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;

public class ConferenceSponsorForm implements Serializable {
	@NotEmpty
	@Size(max = 255)
	@Pattern(regexp = "[a-zA-Z0-9\\-_]+", message = "sponsorIdは半角英数字および\"-\",\"_\"のみ利用可能です")
	private String sponsorId;
	@NotNull
	private SponsorType sponsorType;
	@NotEmpty
	@Size(max = 255)
	private String sponsorName;
	@Size(max = 255)
	@URL
	private String sponsorLogoUrl = "";
	@Size(max = 255)
	@URL
	private String sponsorUrl = "";

	public String getSponsorId() {
		return sponsorId;
	}

	public void setSponsorId(String sponsorId) {
		this.sponsorId = sponsorId;
	}

	public SponsorType getSponsorType() {
		return sponsorType;
	}

	public void setSponsorType(SponsorType sponsorType) {
		this.sponsorType = sponsorType;
	}

	public String getSponsorName() {
		return sponsorName;
	}

	public void setSponsorName(String sponsorName) {
		this.sponsorName = sponsorName;
	}

	public String getSponsorLogoUrl() {
		return sponsorLogoUrl;
	}

	public void setSponsorLogoUrl(String sponsorLogoUrl) {
		this.sponsorLogoUrl = sponsorLogoUrl;
	}

	public String getSponsorUrl() {
		return sponsorUrl;
	}

	public void setSponsorUrl(String sponsorUrl) {
		this.sponsorUrl = sponsorUrl;
	}
}
