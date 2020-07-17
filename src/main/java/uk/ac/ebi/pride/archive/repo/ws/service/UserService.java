package uk.ac.ebi.pride.archive.repo.ws.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.pride.archive.dataprovider.utils.RoleConstants;
import uk.ac.ebi.pride.archive.dataprovider.utils.TitleConstants;
import uk.ac.ebi.pride.archive.repo.models.project.Project;
import uk.ac.ebi.pride.archive.repo.models.project.ProjectSummary;
import uk.ac.ebi.pride.archive.repo.models.user.*;
import uk.ac.ebi.pride.archive.repo.util.AAPConstants;
import uk.ac.ebi.pride.archive.repo.util.ObjectMapper;
import uk.ac.ebi.pride.archive.repo.util.PasswordUtilities;
import uk.ac.ebi.pride.archive.repo.ws.exception.UserAccessException;
import uk.ac.ebi.pride.archive.repo.ws.exception.UserExistsException;
import uk.ac.ebi.pride.archive.repo.ws.exception.UserModificationException;
import uk.ac.ebi.pride.archive.repo.ws.repository.ProjectRepository;
import uk.ac.ebi.pride.archive.repo.ws.repository.UserRepository;

import java.util.*;

@Service
@Transactional(readOnly = true)
@Slf4j
public class UserService {

    private static final String REVIEWER = "reviewer";
    private static final String REVIEWER_FIRST_NAME = "PRIDE";
    private static final String REVIEWER_LAST_NAME = "REVIEWER";
    private static final String AFFILIATION = "EBI";


    private AAPService aapService;
    private UserRepository userRepository;
    private ProjectRepository projectRepository;
    private RestTemplate restTemplate;

    @Value("${aap.auth.url}")
    private String aapRegisterURL;

    @Value("${reviewer.password.message}")
    private String reviewerPasswordMessage;

    public UserService(UserRepository userRepository, ProjectRepository projectRepository, AAPService aapService) {
        this.userRepository = userRepository;
        this.restTemplate = new RestTemplate();
        this.projectRepository = projectRepository;
        this.aapService = aapService;
    }

    @Transactional(readOnly = false)
    public User signUp(UserSummary userSummary) throws UserExistsException, UserModificationException {
        Assert.notNull(userSummary, "New user cannot be empty");
        log.info("Entered registerNewUser : " + userSummary.getEmail());

        String password = PasswordUtilities.generatePassword();
        userSummary.setPassword(password);

        //Sign up user in both AAP and PRIDE
        log.info("Begin user signup : " + userSummary.getEmail());

        try {
            User user = registerWithAAP(userSummary);
            log.info("AAP registration successful : " + user.getEmail() + " : " + user.getUserRef());
            log.info("Begin user domain registration: " + userSummary.getEmail());
            if (user.getUserRef() != null) {
                boolean isDomainRegSuccessful = addUserToAAPDomain(user);
                if (!isDomainRegSuccessful) {
                    log.error("Error adding user to submitter domain in AAP:" + user.getEmail());
                }
            } else {
                log.error("Error creating user and getting user ref for email:" + user.getEmail());
            }
            setCreationAndUpdateDate(user);
            userRepository.save(user);
            user.setPasswordOriginal(password);
            return user;
        } catch (UserExistsException ue) {
            String email = userSummary.getEmail();
            String msg = "Failed to create a new user in AAP: " + email;
            log.error(msg, ue);
            throw ue;
        } catch (Exception e) {
            String email = userSummary.getEmail();
            String msg = "Failed to create a new user in PRIDE DB: " + email;
            log.error(msg, e);
            throw new UserModificationException(msg, e, email);
        }
    }

    @Transactional(readOnly = false)
    public User registerWithAAP(UserSummary userSummary) throws UserExistsException, UserModificationException {
        try {
            UserAAP userAAP = new UserAAP();
            userAAP.setEmail(userSummary.getEmail());
            userAAP.setUsername(userSummary.getEmail());//email is the username for pride hence using same for AAP
            userAAP.setPassword(userSummary.getPassword());
            userAAP.setName(userSummary.getFirstName() + " " + userSummary.getLastName());
            userAAP.setOrganization(userSummary.getAffiliation().substring(0, userSummary.getAffiliation().length() <= 255 ? userSummary.getAffiliation().length() : 255));//AAP limits org size to 255bytes

            String userRef = restTemplate.postForObject(
                    aapRegisterURL, userAAP, String.class);


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

    public boolean addUserToAAPDomain(User user) {
        return aapService.addUserToAAPDomain(user.getUserRef(), AAPConstants.PRIDE_SUBMITTER_DOMAIN);
    }

    public static void setCreationAndUpdateDate(User user) {
        Date currentDate = Calendar.getInstance().getTime();
        user.setCreateAt(currentDate);
        user.setUpdateAt(currentDate);
        if (user.getAcceptedTermsOfUse() != null && user.getAcceptedTermsOfUse() == 1) {
            user.setAcceptedTermsOfUseAt(currentDate);
        }
    }

    public boolean isEmailInUse(String email) throws UserAccessException {
        Assert.notNull(email, "Email cannot be empty");
        try {
            return userRepository.findByEmail(email) != null;
        } catch (Exception ex) {
            String msg = "Failed to check user existence: " + email;
            log.error(msg, ex);
            throw new UserAccessException(msg, ex, email);
        }
    }

    public User findByEmail(String email) throws UserAccessException {
        Assert.notNull(email, "Email cannot be null");
        try {
            return userRepository.findByEmail(email);
        } catch (Exception ex) {
            String msg = "Failed to find user by email: " + email;
            log.error(msg, ex);
            throw new UserAccessException(msg, ex, email);
        }
    }

    public List<ProjectSummary> findAllProjectsById(Long userId) throws UserAccessException {
        List<ProjectSummary> projectSummaries = new ArrayList<>();
        List<Project> ownedProjects =
                projectRepository.findAllBySubmitterId(userId); // find the projects owned by the user
        for (Project ownedProject : ownedProjects) {
            projectSummaries.add(ObjectMapper.mapProjectToProjectSummary(ownedProject));
        }
        List<Project> accessibleProjects = userRepository.findAllProjectsById(userId);
        for (Project accessibleProject : accessibleProjects) {
            projectSummaries.add(ObjectMapper.mapProjectToProjectSummary(accessibleProject));
        }
        return projectSummaries;
    }

    public List<User> findAllByProjectId(Long id) {
        return userRepository.findAllByProjectId(id);
    }

    @Transactional
    public User save(User user) {
        Assert.notNull(user, "User object cannot be null");
        try {
            return userRepository.save(user);
        } catch (Exception ex) {
            String msg = "Failed to save user";
            log.error(msg, ex);
            throw new UserModificationException(msg, ex, user.getEmail());
        }
    }

    @Transactional
    public List<User> findUsersNotInAAP() {
        return userRepository.findUsersNotInAAP();
    }

    @Transactional
    public User findByUserRef(String userRef) {
        return userRepository.findByUserRef(userRef);
    }

    @Transactional
    public User createReviewerAccount(String projectAccession) {

        String reviewerEmail = REVIEWER + "_" + projectAccession.toLowerCase();
        User user = userRepository.findByEmail(reviewerEmail);
        if (user != null) {
            user.setPasswordOriginal(reviewerPasswordMessage);
        } else {
            UserSummary userSummary = new UserSummary();
            userSummary.setEmail(reviewerEmail);
            userSummary.setFirstName(REVIEWER_FIRST_NAME);
            userSummary.setLastName(REVIEWER_LAST_NAME);
            userSummary.setTitle(TitleConstants.UNKNOWN);
            userSummary.setAffiliation(AFFILIATION);
            Date time = Calendar.getInstance().getTime();
            userSummary.setCreateAt(time);
            userSummary.setUpdateAt(time);
            userSummary.setUserAuthorities(Collections.singleton(RoleConstants.REVIEWER));
            user = signUp(userSummary);
        }

        // establish link between project and reviewer user
        Project project = projectRepository.findByAccession(projectAccession);
        Assert.notNull(project, "Project with accession " + projectAccession + " cannot be found in repository");

        // get project users and add the reviewer
        Collection<User> users = project.getUsers();
        if (users == null) {
            users = new ArrayList<>(1);
        }
        users.add(user);
        project.setUsers(users);

        projectRepository.save(project);

        return user;
    }

    /*private String generateReviewerUsername() {
        return REVIEWER + RandomStringUtils.random(REVIEWER_LENGTH, false, true) + EBI_DOMAIN;
    }*/

    @Transactional
    public boolean updateProfile(String token, String currentUserEmail, UserProfile updateUser) {
        User currentUser = userRepository.findByEmail(currentUserEmail);
        UserSummary oldUserSumary = ObjectMapper.mapUserToUserSummary(currentUser);
        //check if fields have been modified
        boolean isModified = false;
        boolean isAAPUpdateRequired = false;

        if (isModified(oldUserSumary.getAcceptedTermsOfUse(), updateUser.getAcceptedTermsOfUse())) {
            oldUserSumary.setAcceptedTermsOfUse(updateUser.getAcceptedTermsOfUse());
            oldUserSumary.setAcceptedTermsOfUseAt(Calendar.getInstance().getTime());
            isModified = true;
        }

        if (!oldUserSumary.getTitle().getTitle().equalsIgnoreCase(updateUser.getTitle().getTitle())) {
            oldUserSumary.setTitle(updateUser.getTitle());
            isModified = true;
        }

        if (isModified(oldUserSumary.getFirstName(), updateUser.getFirstName())) {
            oldUserSumary.setFirstName(updateUser.getFirstName());
            isAAPUpdateRequired = true;
            isModified = true;
        }

        if (isModified(oldUserSumary.getLastName(), updateUser.getLastName())) {
            oldUserSumary.setLastName(updateUser.getLastName());
            isAAPUpdateRequired = true;
            isModified = true;
        }

        if (isModified(oldUserSumary.getAffiliation(), updateUser.getAffiliation())) {
            oldUserSumary.setAffiliation(updateUser.getAffiliation());
            isAAPUpdateRequired = true;
            isModified = true;
        }

        if (isModified(oldUserSumary.getCountry(), updateUser.getCountry())) {
            oldUserSumary.setCountry(updateUser.getCountry());
            isModified = true;
        }

        if (isModified(oldUserSumary.getOrcid(), updateUser.getOrcid())) {
            oldUserSumary.setOrcid(updateUser.getOrcid());
            isModified = true;
        }

        if (isModified) {
            oldUserSumary.setUpdateAt(Calendar.getInstance().getTime());
            if (isAAPUpdateRequired) {
                //update in AAP
                boolean isUpdateSuccessful = aapService.updateUserData(token, oldUserSumary.getUserRef(), oldUserSumary);
                if (!isUpdateSuccessful) {
                    String msg = "Failed to update user detail in AAP: " + oldUserSumary.getEmail();
                    log.error(msg);
                    throw new UserAccessException(msg, oldUserSumary.getEmail());
                }
            }
            //update pride
            try {
                User user = ObjectMapper.mapUserSummaryToUser(oldUserSumary);
                userRepository.save(user);
            } catch (Exception ex) {
                String msg = "Failed to update user detail, user email: " + oldUserSumary.getEmail();
                log.error(ex.getMessage() + ";" + msg);
                throw new UserAccessException(msg, ex, oldUserSumary.getEmail());
            }
        }
        return isModified;
    }

    private boolean isModified(Object oldVal, Object newVal) {
        if (oldVal == null && newVal == null) {
            return false;
        } else if (oldVal == null && newVal != null) {
            return true;
        } else if (oldVal != null && newVal == null) {
            return true;
        } else if (!oldVal.equals(newVal)) {
            return true;
        }
        return false;
    }

    public UserSummary getProfile(String email) {
        User currentUser = userRepository.findByEmail(email);
        if (currentUser != null) {
            UserSummary userSumary = ObjectMapper.mapUserToUserSummary(currentUser);
            return userSumary;
        } else {
            throw new NullPointerException("Email id doesn't exist");
        }
    }

    public ResponseEntity<String> resetPassword(ResetPassword resetPassword) {
        ResponseEntity<String> responseEntity = aapService.resetPassword(resetPassword);
        updateLocalPassword(resetPassword.getUsername(), resetPassword.getPassword());
        return responseEntity;
    }

    public void updateLocalPassword(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            user.setPassword(password);
            userRepository.save(user);
        }
    }

}

