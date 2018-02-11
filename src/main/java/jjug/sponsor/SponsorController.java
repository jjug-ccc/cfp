package jjug.sponsor;

import java.time.Instant;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class SponsorController {
	private final SponsorCredentialResetRepository sponsorCredentialResetRepository;

	public SponsorController(
			SponsorCredentialResetRepository sponsorCredentialResetRepository) {
		this.sponsorCredentialResetRepository = sponsorCredentialResetRepository;
	}

	@GetMapping("sponsors")
	public String sponsor() {
		return "redirect:/";
	}

	@GetMapping("sponsors/login")
	public String loginForm() {
		return "sponsor/loginForm";
	}

	@GetMapping("sponsors/credentialsreset")
	public String credentialsReset(@AuthenticationPrincipal SponsorUser sponsorUser,
			RedirectAttributes attributes) {
		SponsorCredentialReset reset = SponsorCredentialReset.builder() //
				.sponsor(sponsorUser.getSponsor()) //
				.createdAt(Instant.now()) //
				.build();
		SponsorCredentialReset created = this.sponsorCredentialResetRepository
				.save(reset);
		attributes.addAttribute("resetId", created.getResetId());
		return "redirect:/credentialresets/{resetId}";
	}
}
