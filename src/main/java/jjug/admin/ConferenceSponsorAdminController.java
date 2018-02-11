package jjug.admin;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import jjug.conference.Conference;
import jjug.conference.ConferenceRepository;
import jjug.sponsor.Sponsor;
import jjug.sponsor.SponsorCredentialReset;
import jjug.sponsor.SponsorCredentialResetRepository;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
public class ConferenceSponsorAdminController {
	private final ConferenceRepository conferenceRepository;
	private final SponsorCredentialResetRepository sponsorCredentialResetRepository;

	public ConferenceSponsorAdminController(ConferenceRepository conferenceRepository,
			SponsorCredentialResetRepository sponsorCredentialResetRepository) {
		this.conferenceRepository = conferenceRepository;
		this.sponsorCredentialResetRepository = sponsorCredentialResetRepository;
	}

	@ModelAttribute
	ConferenceSponsorForm conferenceSponsorForm() {
		return new ConferenceSponsorForm();
	}

	@GetMapping(path = "admin/conferences/{confId}/sponsors")
	public String sponsorCreateForm(@PathVariable UUID confId, Model model,
			ConferenceSponsorForm sponsorForm) {
		Conference conference = conferenceRepository.findOne(confId).get();
		model.addAttribute("conference", conference);
		return "admin/sponsorCreateForm";
	}

	@PostMapping(path = "admin/conferences/{confId}/sponsors")
	public String sponsorCreate(@PathVariable UUID confId, Model model,
			@Validated ConferenceSponsorForm sponsorForm, BindingResult result) {
		Conference conference = conferenceRepository.findOne(confId).get();
		if (result.hasErrors()) {
			model.addAttribute("conference", conference);
			return "admin/sponsorCreateForm";
		}
		Sponsor sponsor = new Sponsor();
		BeanUtils.copyProperties(sponsorForm, sponsor);
		sponsor.setConference(conference);
		conference.getSponsors().add(sponsor);
		conferenceRepository.save(conference);
		return "redirect:/admin/conferences/{confId}";
	}

	@GetMapping(path = "admin/conferences/{confId}/sponsors/{sponsorId}")
	public String sponsorEditForm(@PathVariable UUID confId,
			@PathVariable String sponsorId, Model model,
			ConferenceSponsorForm sponsorForm) {
		Conference conference = conferenceRepository.findOne(confId).get();
		Sponsor sponsor = conference.getSponsors().stream() //
				.filter(s -> Objects.equals(s.getSponsorId(), sponsorId)) //
				.findAny() //
				.get();
		BeanUtils.copyProperties(sponsor, sponsorForm);
		model.addAttribute("conference", conference);
		model.addAttribute("sponsor", sponsor);
		return "admin/sponsorEditForm";
	}

	@PostMapping(path = "admin/conferences/{confId}/sponsors/{sponsorId}")
	public String sponsorEdit(@PathVariable UUID confId, @PathVariable String sponsorId,
			Model model, @Validated ConferenceSponsorForm sponsorForm,
			BindingResult result) {
		Conference conference = conferenceRepository.findOne(confId).get();
		Sponsor sponsor = conference.getSponsors().stream() //
				.filter(s -> Objects.equals(s.getSponsorId(), sponsorId)) //
				.findAny() //
				.get();
		if (result.hasErrors()) {
			model.addAttribute("conference", conference);
			model.addAttribute("sponsor", sponsor);
			return "admin/sponsorEditForm";
		}
		BeanUtils.copyProperties(sponsorForm, sponsor, "sponsorId");
		conferenceRepository.save(conference);
		return "redirect:/admin/conferences/{confId}";
	}

	@GetMapping(path = "admin/conferences/{confId}/sponsors/{sponsorId}/credentialresets")
	public String credentialReset(@PathVariable UUID confId,
			@PathVariable String sponsorId, Model model) {
		Conference conference = conferenceRepository.findOne(confId).get();
		Sponsor sponsor = conference.getSponsors().stream() //
				.filter(s -> Objects.equals(s.getSponsorId(), sponsorId)) //
				.findAny() //
				.get();
		List<SponsorCredentialReset> resets = sponsorCredentialResetRepository
				.findBySponsor_SponsorIdOrderByCreatedAtDesc(sponsorId);
		model.addAttribute("conference", conference);
		model.addAttribute("sponsor", sponsor);
		model.addAttribute("resets", resets);
		return "admin/sponsorCredentialReset";
	}

	@PostMapping(path = "admin/conferences/{confId}/sponsors/{sponsorId}/credentialresets")
	public String gerenateCredentialReset(@PathVariable UUID confId,
			@PathVariable String sponsorId) {
		Conference conference = conferenceRepository.findOne(confId).get();
		Sponsor sponsor = conference.getSponsors().stream() //
				.filter(s -> Objects.equals(s.getSponsorId(), sponsorId)) //
				.findAny() //
				.get();

		SponsorCredentialReset reset = SponsorCredentialReset.builder().sponsor(sponsor)
				.createdAt(Instant.now()).build();
		sponsorCredentialResetRepository.save(reset);
		return "redirect:/admin/conferences/{confId}/sponsors/{sponsorId}/credentialresets";
	}

	@DeleteMapping(path = "admin/conferences/{confId}/sponsors/{sponsorId}/credentialresets/{resetId}")
	public String revokeCredentialReset(@PathVariable UUID confId,
			@PathVariable String sponsorId, @PathVariable UUID resetId) {
		Conference conference = conferenceRepository.findOne(confId).get();
		List<SponsorCredentialReset> resets = sponsorCredentialResetRepository
				.findBySponsor_SponsorIdOrderByCreatedAtDesc(sponsorId);
		SponsorCredentialReset reset = resets.stream()
				.filter(r -> Objects.equals(r.getResetId(), resetId)).findAny().get();
		sponsorCredentialResetRepository.delete(reset);
		return "redirect:/admin/conferences/{confId}/sponsors/{sponsorId}/credentialresets";
	}
}
