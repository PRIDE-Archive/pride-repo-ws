package uk.ac.ebi.pride.archive.repo.ws.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.ac.ebi.pride.archive.repo.models.assay.software.Software;

import java.util.List;

/**
 * @author Jose A. Dianes
 * @version $Id$
 */
@Repository
public interface SoftwareRepository extends JpaRepository<Software, Long> {

  List<Software> findAllByAssayId(Long assayId);
}
