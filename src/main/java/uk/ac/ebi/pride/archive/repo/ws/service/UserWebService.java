package uk.ac.ebi.pride.archive.repo.ws.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.pride.archive.repo.models.user.User;
import uk.ac.ebi.pride.archive.repo.models.user.UserAAP;
import uk.ac.ebi.pride.archive.repo.models.user.UserSummary;
import uk.ac.ebi.pride.archive.repo.util.ObjectMapper;
import uk.ac.ebi.pride.archive.repo.ws.config.UserWebServiceUrl;
import uk.ac.ebi.pride.archive.repo.ws.exception.UserExistsException;
import uk.ac.ebi.pride.archive.repo.ws.exception.UserModificationException;
import uk.ac.ebi.pride.archive.repo.ws.repository.ProjectRepository;
import uk.ac.ebi.pride.archive.repo.ws.repository.UserRepository;
import uk.ac.ebi.pride.web.util.template.SecureRestTemplateFactory;

/**
 * @author Rui Wang
 * @version $Id$
 */
@Service
@Transactional
@Slf4j
public class UserWebService extends UserService {

    private RestTemplate restTemplate;
    private UserWebServiceUrl userWebServiceUrl;

    @Autowired
    public UserWebService(
            UserRepository userRepository,
            ProjectRepository projectRepository,
            UserWebServiceUrl userWebServiceUrl) {
        super(userRepository, projectRepository);
        this.restTemplate = new RestTemplate();
        this.userWebServiceUrl = userWebServiceUrl;
    }

    @Override
    public User signUp(UserSummary userSummary) throws UserModificationException, UserExistsException {
        try {
            User user = registerWithAAP(userSummary);
            log.info("AAP registration successful : " + userSummary.getEmail() + " : " + userSummary.getUserRef());
            super.signUp(userSummary);
            return user;
        } catch (UserModificationException | UserExistsException ue) {
            throw ue;
        } catch (Exception e) {
            String email = userSummary.getEmail();
            String msg = "Failed to create a new user in PRIDE DB: " + email;
            log.error(msg, e);
            throw new UserModificationException(msg, e, email);
        }
    }

    @Override
    public User registerWithAAP(UserSummary userSummary) throws UserModificationException, UserExistsException {
        try {
            UserAAP userAAP = new UserAAP();
            userAAP.setEmail(userSummary.getEmail());
            userAAP.setUsername(userSummary.getEmail());//email is the username for pride hence using same for AAP
            userAAP.setPassword(userSummary.getPassword());
            userAAP.setName(userSummary.getFirstName() + " " + userSummary.getLastName());
            userAAP.setOrganization(userSummary.getAffiliation().substring(0, userSummary.getAffiliation().length() <= 255 ? userSummary.getAffiliation().length() : 255));//AAP limits org size to 255bytes

            String userRef = restTemplate.postForObject(
                    userWebServiceUrl.getAapRegisterUrl(), userAAP, String.class);


            userSummary.setUserRef(userRef);
            return ObjectMapper.mapUserSummaryToUser(userSummary);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().equals(HttpStatus.CONFLICT)) {
                throw new UserExistsException(e.getResponseBodyAsString(), userSummary.getEmail());
            } else {
                throw new UserModificationException(e.getResponseBodyAsString(), userSummary.getEmail());
            }
        } catch (Exception e) {
            String email = userSummary.getEmail();
            String msg = "Failed to query web service to create a new user in AAP: " + email;
            log.error(msg, e);
            throw new UserModificationException(msg, e, email);
        }
    }

    @Override
    public UserSummary resetPassword(String email) throws UserModificationException {
        UserSummary userSummary = new UserSummary();
        userSummary.setEmail(email);

        try {
            return restTemplate.postForObject(
                    userWebServiceUrl.getPasswordResetUrl(), userSummary, UserSummary.class);
        } catch (Exception e) {
            String msg =
                    "Failed to query web service to reset password for user: " + userSummary.getEmail();
            log.error(msg, e);
            throw new UserModificationException(msg, e, email);
        }
    }

    /**
     * Please note: original user's password should be plain text instead of hashed version
     */
    @Override
    public void update(UserSummary originalUser, UserSummary updatedUser) throws UserModificationException {
        String updateUserRestfulUrl = userWebServiceUrl.getUpdateUrl()
                        + (userWebServiceUrl.getUpdateUrl().endsWith("/") ? "" : "/")
                        + "{userid}";
        String originalUserEmail = originalUser.getEmail();
        try {
            RestTemplate newRestTemplate = SecureRestTemplateFactory.getTemplate(originalUserEmail, originalUser.getPassword());
            newRestTemplate.put(updateUserRestfulUrl, new UserSummary(updatedUser), originalUser.getId());
        } catch (Exception e) {
            String msg = "Failed to query web service to update details for user: " + originalUserEmail;
            log.error(msg, e);
            throw new UserModificationException(msg, e, originalUserEmail);
        }
    }
}
