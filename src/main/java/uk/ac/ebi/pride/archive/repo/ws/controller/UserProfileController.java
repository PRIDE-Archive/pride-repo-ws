package uk.ac.ebi.pride.archive.repo.ws.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.pride.archive.dataprovider.utils.TitleConstants;
import uk.ac.ebi.pride.archive.repo.models.user.Credentials;
import uk.ac.ebi.pride.archive.repo.models.user.User;
import uk.ac.ebi.pride.archive.repo.models.user.UserProfile;
import uk.ac.ebi.pride.archive.repo.models.user.UserSummary;
import uk.ac.ebi.pride.archive.repo.ws.service.UserService;
import uk.ac.ebi.pride.archive.repo.ws.utils.AapJwtToken;
import uk.ac.ebi.pride.archive.repo.ws.validators.UpdateProfileValidator;
import uk.ac.ebi.pride.archive.repo.ws.validators.UserRegistrationValidator;
import uk.ac.ebi.tsc.aap.client.exception.InvalidJWTTokenException;

import javax.validation.Valid;
import java.nio.charset.Charset;
import java.util.Date;

@RestController
@Validated
@RequestMapping("/user/profile/")
@Slf4j
@Tag(name = "UserProfile")
public class UserProfileController {

    @Value("${aap.auth.url}")
    private String auth_url;

    private UserRegistrationValidator userRegistrationValidator;

    private UpdateProfileValidator updateProfileValidator;

    private UserService userService;

    public UserProfileController(UserRegistrationValidator userRegistrationValidator, UpdateProfileValidator updateProfileValidator, UserService userService) {
        this.userRegistrationValidator = userRegistrationValidator;
        this.updateProfileValidator = updateProfileValidator;
        this.userService = userService;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> registerNewUser(@RequestBody @Valid UserSummary userSummary, BindingResult errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors.getAllErrors());
        }
        try {
            User user = userService.signUp(userSummary);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

    }

    @PostMapping(path = "/view-profile", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getProfile(@RequestHeader String authorization) {
        try {
            String jwtToken = authorization.split(" ")[1];
            AapJwtToken aapJwtToken = getDecodedAAPtokenValue(jwtToken);
            UserSummary userSummary = userService.getProfile(aapJwtToken.getEmail());
            return ResponseEntity.ok(userSummary);
        } catch (InvalidJWTTokenException e) {
            log.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error fetching user profile:" + e.getMessage());
        }
    }

    @PostMapping(path = "/update-profile", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateProfile(@RequestBody @Valid UserProfile userProfile,
                                                @RequestHeader String authorization,
                                                BindingResult errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors.getAllErrors());
        }
        try {
            String jwtToken = authorization.split(" ")[1];
            AapJwtToken aapJwtToken = getDecodedAAPtokenValue(jwtToken);
            if (!aapJwtToken.getEmail().equalsIgnoreCase(userProfile.getEmail())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email mismatch occurred");
            }
            userService.updateProfile(jwtToken, aapJwtToken.getEmail(), userProfile);
            return ResponseEntity.ok().build();
        } catch (InvalidJWTTokenException e) {
            log.error(e.getMessage(), e);
            throw e;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @RequestMapping(path = "getAAPToken", method = RequestMethod.POST)
    public String getAAPToken(@RequestBody @Valid Credentials credentials) throws Exception {
        ResponseEntity<String> response = null;
        String jwtToken = null;
        try {
            String email = credentials.getUsername();
            HttpEntity<String> entity = new HttpEntity<>(createHttpHeaders(email, credentials.getPassword()));
            RestTemplate restTemplate = new RestTemplate();
            response = restTemplate.exchange
                    (auth_url, HttpMethod.GET, entity, String.class);
            jwtToken = response.getBody();
            boolean emailInUse = userService.isEmailInUse(email);
            if (emailInUse) {//update password for existing local user..just to avoid mismatch between AAP & our local DB
                userService.updateLocalPassword(email, credentials.getPassword());
            } else { // User is in AAP DB but not in our DB
                try {
                    AapJwtToken aapJwtToken = getDecodedAAPtokenValue(jwtToken);
                    UserSummary userSummary = new UserSummary();
                    userSummary.setUserRef(aapJwtToken.getAapRef());
                    userSummary.setEmail(aapJwtToken.getEmail());
                    userSummary.setPassword(credentials.getPassword());
                    userSummary.setTitle(TitleConstants.UNKNOWN);
                    userSummary.setFirstName(aapJwtToken.getName());
                    userSummary.setLastName(" ");
                    userSummary.setAffiliation(" ");
                    userSummary.setAcceptedTermsOfUse(true);
                    userSummary.setAcceptedTermsOfUseAt(new Date());
                    uk.ac.ebi.pride.archive.repo.models.user.User user = userService.signUp(userSummary);

                    //Add user to submitter domain in AAP
                    log.info("Begin user domain registeration: " + userSummary.getEmail());
                    if (user.getUserRef() != null) {
                        boolean isDomainRegSuccessful = userService.addUserToAAPDomain(user);
                        if (!isDomainRegSuccessful) {
                            log.error("Error adding user to submitter domain in AAP:" + user.getEmail());
                        }
                    }
                } catch (Exception ex) {
                    log.error("Email doesn't exists in our DB but exists in AAP. Failed to register during login : " + email);
                    log.error(ex.getMessage(), ex);
                }
            }
        } catch (HttpClientErrorException e) {
            String error = "username/password wrong. Please check username or password to get token";
            log.error(error);
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, error);
        } catch (Exception e) {
            throw new RuntimeException("Error while getting AAP token", e);
        }
        return jwtToken;
    }

    private AapJwtToken getDecodedAAPtokenValue(String jwtToken) {
        try {
            java.util.Base64.Decoder decoder = java.util.Base64.getUrlDecoder();
            String[] parts = jwtToken.split("\\."); // split out the "parts" (header, payload and signature)
            String payloadJson = new String(decoder.decode(parts[1]));
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(payloadJson, AapJwtToken.class);
        } catch (Exception ex) {
            log.error("Error in getting current user, Token parsing exception");
            log.error(ex.getMessage(), ex);
            throw new InvalidJWTTokenException("Error parsing token");
        }
    }

    private static HttpHeaders createHttpHeaders(String username, String password) {
        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.encodeBase64(
                auth.getBytes(Charset.forName("US-ASCII")));
        String authHeader = "Basic " + new String(encodedAuth);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", authHeader);
        return headers;
    }

    @InitBinder("userProfile")
    protected void initBinderUpdateProfile(WebDataBinder binder) {
        binder.setValidator(updateProfileValidator);
    }

    @InitBinder("userSummary")
    protected void initBinderRegister(WebDataBinder binder) {
        binder.setValidator(userRegistrationValidator);
    }

}
