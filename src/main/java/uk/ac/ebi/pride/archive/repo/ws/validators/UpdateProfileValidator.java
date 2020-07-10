package uk.ac.ebi.pride.archive.repo.ws.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import uk.ac.ebi.pride.archive.repo.models.user.UserProfile;
import uk.ac.ebi.pride.archive.repo.ws.service.UserService;

@Component
public class UpdateProfileValidator extends UserSummaryValidator {

    @Autowired
    public UpdateProfileValidator(UserService userService) {
        super(userService);
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return UserProfile.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        validateEmailExists(target, errors);
        validateContactDetails(target, errors);
        validateAcceptedTerms(target, errors);
    }
}
