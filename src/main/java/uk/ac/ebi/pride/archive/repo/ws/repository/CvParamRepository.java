package uk.ac.ebi.pride.archive.repo.ws.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.ac.ebi.pride.archive.repo.models.param.CvParam;

/**
 * @author Jose A. Dianes
 * @version $Id$
 */
@Repository
public interface CvParamRepository extends CrudRepository<CvParam, Long> {

  CvParam findByAccession(String accession);
}
