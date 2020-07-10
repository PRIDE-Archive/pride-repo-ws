package uk.ac.ebi.pride.archive.repo.ws.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import uk.ac.ebi.pride.archive.repo.ws.service.UserService;

@Component
public class UserRegistrationValidator extends UserSummaryValidator {
    @Autowired
    public UserRegistrationValidator(UserService userService) {
        super(userService);
    }

    @Override
    public void validate(Object target, Errors errors) {
        validateContactDetails(target, errors);
        validateEmail(target, errors);
        validateAcceptedTerms(target, errors);
    }

}