package jjug.sponsor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.stereotype.Component;

@Component
public class UnusedSponsorIdValidator
		implements ConstraintValidator<UnusedSponsorId, String> {
	private final SponsorRepository sponsorRepository;

	public UnusedSponsorIdValidator(SponsorRepository sponsorRepository) {
		this.sponsorRepository = sponsorRepository;
	}

	@Override
	public void initialize(UnusedSponsorId unusedSponsorId) {

	}

	@Override
	public boolean isValid(String s,
			ConstraintValidatorContext constraintValidatorContext) {
		if (s == null || s.isEmpty()) {
			return true;
		}
		return this.sponsorRepository.countBySponsorId(s) == 0;
	}
}
