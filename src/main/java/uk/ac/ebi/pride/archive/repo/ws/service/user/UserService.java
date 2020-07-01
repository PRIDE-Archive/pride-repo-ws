package uk.ac.ebi.pride.archive.repo.ws.service.user;

import uk.ac.ebi.pride.archive.repo.models.project.ProjectSummary;
import uk.ac.ebi.pride.archive.repo.models.user.User;
import uk.ac.ebi.pride.archive.repo.models.user.UserSummary;
import uk.ac.ebi.pride.archive.repo.ws.exception.UserAccessException;
import uk.ac.ebi.pride.archive.repo.ws.exception.UserModificationException;

import java.util.List;

public interface UserService {
    User signUp(UserSummary user) throws UserModificationException;

    User registerWithAAP(UserSummary user) throws UserModificationException;

    /*void update(UserSummary updatedUser) throws UserModificationException;*/

    boolean isEmailedInUse(String email) throws UserAccessException;

    User findByEmail(String email) throws UserAccessException;

    List<ProjectSummary> findAllProjectsById(Long userId) throws UserAccessException;
}
