package uk.ac.ebi.pride.archive.repo.ws.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uk.ac.ebi.pride.archive.repo.models.project.Project;
import uk.ac.ebi.pride.archive.repo.models.user.User;

import java.util.List;

/**
 * @author Jose A. Dianes
 * @version $Id$
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  User findByEmail(String email);

  @Query("select u from User u, IN(u.projects) p where p.id = ?1")
  List<User> findAllByProjectId(Long projectId);

  @Query("select u.projects from User u where u.id = ?1")
  List<Project> findAllProjectsById(Long userId);

  //AAP changes
  @Query("select u from User u where u.userRef = ?1")
  User findByUserRef(String userRef);

  //Fetch users not in AAP
  @Query("select u from User u where u.userRef is null")
  List<User> findFilteredLocalUsers();
}
