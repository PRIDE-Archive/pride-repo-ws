package uk.ac.ebi.pride.archive.repo.ws.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.pride.archive.repo.models.project.Project;

import java.util.List;

/**
 * @author Jose A. Dianes
 * @version $Id$
 */
@Transactional
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

  Project findByAccession(String projectAccession);

  @Query("select p from Project p where p.submitter.id = ?1")
  List<Project> findAllBySubmitterId(Long submitterId);

  @Query("select p from Project p where p.submitter.id = ?1 and p.publicProject = ?2")
  List<Project> findFilteredBySubmitterIdAndIsPublic(Long submitterId, Boolean isPublic);

  @Query("select p from Project p join p.users u where u.userRef = ?1 ")
  List<Project> findFilteredByReviewer(String user_aap_ref);

  @Query("select p.accession from Project p order by p.submissionDate")
  List<String> findAllAccessions();

  @Query("select p.accession from Project p where p.publicProject=true order by p.submissionDate")
  List<String> findAllPublicAccessions();

  @Query("select p.accession from Project p where p.changed = 1")
  List<String> findAllAccessionsChanged();

  @Query(value="select TO_CHAR(date_trunc('month', submission_date),'MON-YY') AS MONTH, count(*) from Project group by date_trunc('month', submission_date) order by date_trunc('month', submission_date) DESC",nativeQuery = true)
  List<List<String>> findMonthlySubmissions();
}
