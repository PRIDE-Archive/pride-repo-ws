package uk.ac.ebi.pride.archive.repo.ws.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.ac.ebi.pride.archive.repo.models.param.CvParam;

/**
 * @author Jose A. Dianes
 * @version $Id$
 */
@Repository
public interface CvParamRepository extends JpaRepository<CvParam, Long> {

  CvParam findByAccession(String accession);
}
