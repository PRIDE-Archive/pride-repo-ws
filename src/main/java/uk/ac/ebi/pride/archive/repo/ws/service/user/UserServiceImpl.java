package uk.ac.ebi.pride.archive.repo.ws.service.user;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import uk.ac.ebi.pride.archive.dataprovider.utils.RoleConstants;
import uk.ac.ebi.pride.archive.dataprovider.utils.TitleConstants;
import uk.ac.ebi.pride.archive.repo.models.project.Project;
import uk.ac.ebi.pride.archive.repo.models.project.ProjectSummary;
import uk.ac.ebi.pride.archive.repo.models.user.User;
import uk.ac.ebi.pride.archive.repo.models.user.UserSummary;
import uk.ac.ebi.pride.archive.repo.util.ObjectMapper;
import uk.ac.ebi.pride.archive.repo.util.PasswordUtilities;
import uk.ac.ebi.pride.archive.repo.ws.exception.UserAccessException;
import uk.ac.ebi.pride.archive.repo.ws.exception.UserModificationException;
import uk.ac.ebi.pride.archive.repo.ws.repository.ProjectRepository;
import uk.ac.ebi.pride.archive.repo.ws.repository.UserRepository;

import java.util.*;

/**
 * @author Rui Wang
 * @author Jose A. Dianes
 * @version $Id$
 */
@Service
@Transactional(readOnly = true)
@Slf4j
public class UserServiceImpl implements UserService {

    private static final int REVIEWER_LENGTH = 5;
    private static final String EBI_DOMAIN = "@ebi.ac.uk";
    private static final String REVIEWER = "reviewer";
    private static final String REVIEWER_FIRST_NAME = "PRIDE";
    private static final String REVIEWER_LAST_NAME = "REVIEWER";
    private static final String AFFILIATION = "EBI";

    private UserRepository userRepository;
    private ProjectRepository projectRepository;

    public UserServiceImpl(UserRepository userRepository, ProjectRepository projectRepository) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
    }

    @Transactional(readOnly = false)
    public User signUp(UserSummary userSummary) throws UserModificationException {
        Assert.notNull(userSummary, "New user cannot be empty");
        try {
            User user = mapToPersistableUser(userSummary);
            setCreationAndUpdateDate(user);
            userRepository.save(user);
            return user;
        } catch (Exception ex) {
            String email = userSummary.getEmail();
            String msg = "Failed to create a new user: " + email;
            log.error(msg, ex);
            throw new UserModificationException(msg, ex, email);
        }
    }

    @Transactional(readOnly = false)
    public User registerWithAAP(UserSummary userSummary) throws UserModificationException {
        return signUp(userSummary);
    }


    private void setCreationAndUpdateDate(User user) {
        Date currentDate = Calendar.getInstance().getTime();
        user.setCreateAt(currentDate);
        user.setUpdateAt(currentDate);
        if (user.getAcceptedTermsOfUse() != null && user.getAcceptedTermsOfUse() == 1) {
            user.setAcceptedTermsOfUseAt(currentDate);
        }
    }

    public boolean isEmailedInUse(String email) throws UserAccessException {
        Assert.notNull(email, "Email cannot be empty");
        try {
            return userRepository.findByEmail(email) != null;
        } catch (Exception ex) {
            String msg = "Failed to check user existence: " + email;
            log.error(msg, ex);
            throw new UserAccessException(msg, ex, email);
        }
    }

    @Transactional(readOnly = false)
    public UserSummary resetPassword(String email) throws UserModificationException {
        Assert.notNull(email, "Email cannot be empty");
        try {
            User user = userRepository.findByEmail(email);
            String newPassword = PasswordUtilities.generatePassword(); // reset passwor
            user.setPassword(newPassword);
            userRepository.save(user);
            return hideUserDetailsForPasswordReset(user, newPassword);
        } catch (Exception ex) {
            String msg = "Failed to reset password for user: " + email;
            log.error(msg, ex);
            throw new UserModificationException(msg, ex, email);
        }
    }

    private UserSummary hideUserDetailsForPasswordReset(User user, String newPassword) {
        UserSummary userSummary = ObjectMapper.mapUserToUserSummary(user);
        userSummary.setPassword(newPassword);
        return userSummary;
    }

    public UserSummary login(String email, String passwordPlainText) throws UserAccessException {
        Assert.notNull(email, "Email cannot be empty");
        Assert.notNull(passwordPlainText, "Password cannot be empty");
        try {
            User user = userRepository.findByEmail(email);
            if (PasswordUtilities.matches(passwordPlainText, user.getPassword())) {
                return ObjectMapper.mapUserToUserSummary(user);
            } else {
                String msg = "Failed to login as user: " + email;
                log.error(msg);
                throw new UserAccessException(msg);
            }
        } catch (Exception ex) {
            String msg = "Failed to login as user: " + email;
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

    /*@Transactional(readOnly = false)
    public void update(UserSummary originalUser, UserSummary updatedUser)
            throws UserModificationException {
        Assert.notNull(originalUser, "User to update cannot be null");
        Assert.notNull(updatedUser, "User to update cannot be null");
        try {
            updateUser(originalUser, updatedUser);
            changeUpdateDate(originalUser);
            User user = ObjectMapper.mapUserSummaryToUser(originalUser);
            userRepository.save(user);
        } catch (Exception ex) {
            String msg = "Failed to update user detail, user email: " + originalUser.getEmail();
            log.error(msg, ex);
            throw new UserAccessException(msg, ex, originalUser.getEmail());
        }
    }*/

    private void updateUser(UserSummary prideUser, UserSummary user) {
        if (user.getEmail() != null) {
            prideUser.setEmail(user.getEmail());
        }
        if (user.getPassword() != null) {
            prideUser.setPassword(user.getPassword());
        }
        if (user.getTitle() != null) {
            prideUser.setTitle(user.getTitle());
        }
        if (user.getFirstName() != null) {
            prideUser.setFirstName(user.getFirstName());
        }
        if (user.getLastName() != null) {
            prideUser.setLastName(user.getLastName());
        }
        if (user.getAffiliation() != null) {
            prideUser.setAffiliation(user.getAffiliation());
        }
        if (user.getCountry() != null) {
            prideUser.setCountry(user.getCountry());
        }
        if (user.getOrcid() != null) {
            prideUser.setOrcid(user.getOrcid());
        }
        if (user.getAcceptedTermsOfUse() != null) {
            prideUser.setAcceptedTermsOfUse(user.getAcceptedTermsOfUse());
        }
    }

    private void changeUpdateDate(UserSummary userSummary) {
        Date currentDate = Calendar.getInstance().getTime();
        userSummary.setUpdateAt(currentDate);
        if (userSummary.getAcceptedTermsOfUse() != null && userSummary.getAcceptedTermsOfUse()) {
            userSummary.setAcceptedTermsOfUseAt(currentDate);
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

    private User mapToPersistableUser(UserSummary userSummary) {
        User prideUser = new User();
        prideUser.setEmail(userSummary.getEmail());
        prideUser.setPassword(userSummary.getPassword());
        prideUser.setUserRef(userSummary.getUserRef());
        prideUser.setTitle(userSummary.getTitle());
        prideUser.setFirstName(userSummary.getFirstName());
        prideUser.setLastName(userSummary.getLastName());
        prideUser.setAffiliation(userSummary.getAffiliation());
        prideUser.setCountry(userSummary.getCountry());
        prideUser.setOrcid(userSummary.getOrcid());
        prideUser.setAcceptedTermsOfUse(userSummary.getAcceptedTermsOfUse() ? 1 : 0);
        prideUser.setAcceptedTermsOfUseAt(userSummary.getAcceptedTermsOfUseAt());
        Set<RoleConstants> authorities = new HashSet<>();
        authorities.add(RoleConstants.SUBMITTER); // can only create submitter
        prideUser.setUserAuthorities(authorities);
        return prideUser;
    }

    @Transactional
    public User createReviewerAccount(String projectAccession, String password) {
        User user = createReviewerAccount();
        user.setPassword(password);

        // establish link between project and reviewer user
        Project project = projectRepository.findByAccession(projectAccession);
        Assert.notNull(project, "Project with accession " + projectAccession + " cannot be found in repository");

        user = userRepository.save(user);

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

    //only method to create a reviewer account with appropriate random generated username and password
    private User createReviewerAccount() {
        User user = new User();

        user.setAffiliation(AFFILIATION);

        String reviewerEmail = generateReviewerUsername();
        while (userRepository.findByEmail(reviewerEmail) != null) {
            reviewerEmail = generateReviewerUsername();
        }
        user.setEmail(reviewerEmail);

        user.setFirstName(REVIEWER_FIRST_NAME);
        user.setLastName(REVIEWER_LAST_NAME);
        user.setTitle(TitleConstants.UNKNOWN);

        Date time = Calendar.getInstance().getTime();
        user.setCreateAt(time);
        user.setUpdateAt(time);

        user.setUserAuthorities(Collections.singleton(RoleConstants.REVIEWER));

        return user;
    }

    private String generateReviewerUsername() {
        return REVIEWER + RandomStringUtils.random(REVIEWER_LENGTH, false, true) + EBI_DOMAIN;
    }

}

