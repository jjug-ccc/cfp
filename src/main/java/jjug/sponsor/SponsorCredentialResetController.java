package jjug.sponsor;

import java.util.Objects;
import java.util.UUID;

import jjug.conference.Conference;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class SponsorCredentialResetController {
	private final SponsorCredentialResetRepository sponsorCredentialResetRepository;
	private final SponsorCredentialRepository sponsorCredentialRepository;
	private final PasswordEncoder passwordEncoder;

	public SponsorCredentialResetController(
			SponsorCredentialResetRepository sponsorCredentialResetRepository,
			SponsorCredentialRepository sponsorCredentialRepository,
			PasswordEncoder passwordEncoder) {
		this.sponsorCredentialResetRepository = sponsorCredentialResetRepository;
		this.sponsorCredentialRepository = sponsorCredentialRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@ModelAttribute
	public SponsorCredentialResetForm sponsorCredentialResetForm() {
		return new SponsorCredentialResetForm();
	}

	@GetMapping(path = "credentialresets/{resetId}")
	public String credentialsResetForm(@PathVariable UUID resetId, Model model) {
		SponsorCredentialReset reset = this.sponsorCredentialResetRepository
				.findByResetId(resetId) //
				.filter(SponsorCredentialReset::isValid) //
				.orElseThrow(SponsorCredentialResetExpiredException::new);
		Sponsor sponsor = reset.getSponsor();
		Conference conference = sponsor.getConference();
		model.addAttribute("conference", conference);
		model.addAttribute("reset", reset);
		model.addAttribute("sponsor", sponsor);
		return "sponsor/credentialResetForm";
	}

	@PostMapping(path = "credentialresets/{resetId}")
	public String credentialsReset(@PathVariable UUID resetId, Model model,
			@Validated SponsorCredentialResetForm form, BindingResult result) {
		SponsorCredentialReset reset = this.sponsorCredentialResetRepository
				.findByResetId(resetId) //
				.filter(SponsorCredentialReset::isValid) //
				.orElseThrow(SponsorCredentialResetExpiredException::new);
		Sponsor sponsor = reset.getSponsor();
		if (!Objects.equals(sponsor.getSponsorId(), form.getSponsorId())) {
			result.rejectValue("sponsorId", "sponsorId.invalid", "スポンサーIDが正しくありません");
		}
		if (result.hasErrors()) {
			return credentialsResetForm(resetId, model);
		}
		SponsorCredential credential = SponsorCredential.builder() //
				.credential(this.passwordEncoder.encode(form.getPassword())) //
				.sponsorId(sponsor.getSponsorId()) //
				.sponsor(sponsor) //
				.build();
		this.sponsorCredentialRepository.save(credential.reset(reset));
		return "redirect:/sponsors/login?reset";
	}
}
