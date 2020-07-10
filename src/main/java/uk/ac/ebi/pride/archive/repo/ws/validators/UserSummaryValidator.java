package uk.ac.ebi.pride.archive.repo.ws.validators;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import uk.ac.ebi.pride.archive.repo.models.user.UserSummary;
import uk.ac.ebi.pride.archive.repo.ws.service.UserService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Component
public class UserSummaryValidator implements Validator {

    public static final String EMAIL_ERROR_MESSAGE = "Valid email address is required";
    public static final String EMAIL_ALREADY_EXIST_ERROR_MESSAGE = "Email address already in use";
    public static final String EMAIL_DOESNT_EXIST_ERROR_MESSAGE = "Email address not found";
    public static final String PASSWORD_ERROR_MESSAGE = "Password cannot be empty";
    public static final String USER_TITLE_ERROR_MESSAGE = "Title cannot be empty";
    public static final String FIRST_NAME_ERROR_MESSAGE = "First name cannot be empty";
    public static final String LAST_NAME_ERROR_MESSAGE = "Last name cannot be empty";
    public static final String AFFILIATION_ERROR_MESSAGE = "Affiliation cannot be empty";
    public static final String COUNTRY_ERROR_MESSAGE = "Country cannot be empty";
    public static final String ORCID_ERROR_MESSAGE = "Invalid ORCID";
    public static final String ACCEPTED_TERMS_ERROR_MESSAGE = "Terms of use must be accepted to proceed";

    public static final Pattern EMAIL_REGEX_PATTERN =
            Pattern.compile("^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$");
    public static final Pattern ORCID_REGEX_PATTERN =
            Pattern.compile("\\d{4}-\\d{4}-\\d{4}-\\d{3}[0-9X]");

    protected UserService userService;

    @Autowired
    public UserSummaryValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return UserSummary.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        validateContactDetails(target, errors);
        validateEmail(target, errors);
        validatePassword(target, errors);
    }

    public void validateEmail(Object target, Errors errors) {
        rejectAlreadyExistEmail(errors, EMAIL_ALREADY_EXIST_ERROR_MESSAGE);
    }

    public void validateEmailExists(Object target, Errors errors) {
        rejectNoneExistingEmail(errors, EMAIL_DOESNT_EXIST_ERROR_MESSAGE);
    }

    public void validateContactDetails(Object target, Errors errors) {
        rejectIllegalEmail(errors, EMAIL_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(
                errors, "title", "required", USER_TITLE_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(
                errors, "firstName", "required", FIRST_NAME_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(
                errors, "lastName", "required", LAST_NAME_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(
                errors, "affiliation", "required", AFFILIATION_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "country", "required", COUNTRY_ERROR_MESSAGE);
        Object orcidValue = errors.getFieldValue("orcid");
        if (orcidValue != null) { // validate ORCID
            if (!StringUtils.isEmpty(orcidValue.toString())) {
                Matcher m = ORCID_REGEX_PATTERN.matcher(orcidValue.toString());
                if (!m.matches()) {
                    errors.rejectValue("orcid", "required", null, ORCID_ERROR_MESSAGE);
                } // else OK
            } // else no ORCID, OK
        } // else no ORCID, OK
    }

    public void validatePassword(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(
                errors, "password", "required", PASSWORD_ERROR_MESSAGE);
    }

    public void validateAcceptedTerms(Object target, Errors errors) {
        Object acceptedTermsOfUse = errors.getFieldValue("acceptedTermsOfUse");
        if (acceptedTermsOfUse == null || !acceptedTermsOfUse.toString().equalsIgnoreCase("true")) {
            errors.rejectValue("acceptedTermsOfUse", "required", ACCEPTED_TERMS_ERROR_MESSAGE);
        }
    }

    /**
     * Validate email if it is not null
     */
    protected void rejectIllegalEmail(Errors errors, String errorMessage) {
        Object value = errors.getFieldValue("email");
        if (value != null) {
            Matcher m = EMAIL_REGEX_PATTERN.matcher(value.toString());
            if (!m.matches()) {
                errors.rejectValue("email", "required", null, errorMessage);
            }
        }
    }

    protected void rejectAlreadyExistEmail(Errors errors, String errorMessage) {
        Object value = errors.getFieldValue("email");
        if (value != null) {
            boolean emailedInUse = userService.isEmailInUse(value.toString());
            if (emailedInUse) {
                errors.rejectValue("email", "required", null, errorMessage);
            }
        }
    }

    protected void rejectNoneExistingEmail(Errors errors, String errorMessage) {
        Object value = errors.getFieldValue("email");
        if (value != null) {
            boolean emailedInUse = userService.isEmailInUse(value.toString());
            if (!emailedInUse) {
                errors.rejectValue("email", "required", null, errorMessage);
            }
        }
    }
}
