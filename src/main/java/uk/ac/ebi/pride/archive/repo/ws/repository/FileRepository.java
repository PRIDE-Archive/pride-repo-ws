package uk.ac.ebi.pride.archive.repo.ws.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.ac.ebi.pride.archive.repo.models.file.ProjectFile;

import java.util.List;

/**
 * @author Jose A. Dianes
 * @version $Id$
 */
@Repository
public interface FileRepository extends JpaRepository<ProjectFile, Long> {

  List<ProjectFile> findAllByProjectId(Long projectId);

  List<ProjectFile> findAllByAssayId(Long assayId);
}
