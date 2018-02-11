package jjug.sponsor;

import jjug.CfpUser;

// This class extends CfpUser in order to leverage `@AuthenticationPrincipal CfpUser`.
// This is not good design :(
public class SponsorUser extends CfpUser {
	private final Sponsor sponsor;

	public SponsorUser(Sponsor sponsor) {
		super(sponsor.getSponsorName(), "", "", "");
		this.sponsor = sponsor;
	}

	public Sponsor getSponsor() {
		return sponsor;
	}
}
