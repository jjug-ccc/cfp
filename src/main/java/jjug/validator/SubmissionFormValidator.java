package jjug.validator;

import jjug.submission.*;
import org.springframework.stereotype.Component;
import org.springframework.validation.*;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.String.*;
import static java.util.Objects.*;
import static org.springframework.util.StringUtils.*;

@Component
public class SubmissionFormValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
 		return SubmissionForm.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		SubmissionForm form = SubmissionForm.class.cast(target);
		validateSpeaker(form.getSpeakerForms(), errors);
	}

	void validateSpeaker(Deque<SpeakerForm> speakerForms, Errors errors) {
		for (int i = 0; i < speakerForms.size(); i++) {
			validateActivity(new ArrayList<>(speakerForms).get(i).getActivityList(), errors, i);
		}
	}

	void validateActivity(List<ActivityForm> activityList, Errors errors, int index) {
		String activityTypeMsg = "speakerForms[%s].activityList[%s].activityType";
		String urlMsg = "speakerForms[%s].activityList[%s].url";

		List<ActivityForm> activityForms = activityList.stream()
				.filter(activityForm -> nonNull(activityForm.getActivityType()) || hasLength(activityForm.getUrl()))
				.collect(Collectors.toList());

		if (activityForms.isEmpty()) {
			errors.rejectValue(format(activityTypeMsg, index, 0), "NotEmpty", new Object[]{format(activityTypeMsg, index, 0)}, null);
			errors.rejectValue(format(urlMsg, index, 0), "NotEmpty", new Object[]{format(urlMsg, index, 0)}, null);
		}

		for (int i = 0; i < activityForms.size(); i++) {
			if (isNull(activityForms.get(i).getActivityType())) {
				errors.rejectValue(format(activityTypeMsg, index, i), "NotEmpty", new Object[]{format(activityTypeMsg, index, i)}, null);
			}
			if (!hasLength(activityForms.get(i).getUrl())) {
				errors.rejectValue(format(urlMsg, index, i), "NotEmpty", new Object[]{format(urlMsg, index, i)}, null);
			}
		}
	}
}
