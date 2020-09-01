package uk.ac.ebi.pride.archive.repo.ws.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.ac.ebi.pride.archive.repo.models.accession.PrideAccession;


@Repository
public interface AccessionRepository extends JpaRepository<PrideAccession, Long> {

    PrideAccession findByEntity(String entity);
}
