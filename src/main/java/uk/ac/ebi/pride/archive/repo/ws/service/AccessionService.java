package uk.ac.ebi.pride.archive.repo.ws.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.pride.archive.repo.models.accession.PrideAccession;
import uk.ac.ebi.pride.archive.repo.models.accession.PrideAccessions;
import uk.ac.ebi.pride.archive.repo.ws.exception.AccessionUpdateException;
import uk.ac.ebi.pride.archive.repo.ws.repository.AccessionRepository;
import uk.ac.ebi.pride.archive.repo.ws.utils.accession.AccessionFormatter;
import uk.ac.ebi.pride.archive.repo.ws.utils.accession.AccessionType;
import uk.ac.ebi.pride.archive.repo.ws.utils.accession.AccessionUpdateMode;

@Service
@Transactional
@Slf4j
public class AccessionService {

    private static final String SEPARATOR = "-";

    private final AccessionRepository accessionRepository;

    @Autowired
    public AccessionService(AccessionRepository accessionRepository) {
        this.accessionRepository = accessionRepository;
    }

    public PrideAccessions getAccessions(int count, AccessionType type, AccessionUpdateMode mode) throws AccessionUpdateException {
        PrideAccessions accessions = new PrideAccessions();

        // increment accession
        long lastAccession = reserveId(type + SEPARATOR + mode, count);

        // accession formatter
        AccessionFormatter accessionFormatter = mode.equals(AccessionUpdateMode.PRODUCTION) ?
                type.getProductionAccessionFormatter() :
                type.getTestAccessionFormatter();

        //that represents last valid accession in my list, create the list
        if (lastAccession > 0) {
            for (long i = lastAccession - count + 1; i <= lastAccession; i++) {
                accessions.addAccession(accessionFormatter.formatAccession(i));
            }
        } else {
            String msg = "Failed to reserve accession: " + type;
            log.error(msg);
            throw new AccessionUpdateException(msg);
        }

        return accessions;
    }

    @Transactional
    public Long reserveId(String entity, int numIds) {

        PrideAccession prideAccession = accessionRepository.findByEntity(entity);
        Long lastUsedId = prideAccession.getLastId();

        if (lastUsedId == null) {
            lastUsedId = 0L;
        }

        // update adding numIds
        Long idToUpdate = lastUsedId + numIds;
        accessionRepository.save(new PrideAccession(entity, idToUpdate));

        // return last available ID
        return idToUpdate;
    }

    public PrideAccessions getLastAccession(AccessionType type, AccessionUpdateMode mode) {

        PrideAccessions accessions = new PrideAccessions();

        PrideAccession prideAccession = accessionRepository.findByEntity(type + SEPARATOR + mode);
        Long lastAccession = prideAccession.getLastId();

        // accession formatter
        AccessionFormatter accessionFormatter = mode.equals(AccessionUpdateMode.PRODUCTION) ?
                type.getProductionAccessionFormatter() :
                type.getTestAccessionFormatter();

        //that represents last valid accession in my list, create the list
        if (lastAccession > 0) {
            accessions.addAccession(accessionFormatter.formatAccession(lastAccession));
        } else {
            String msg = "Failed to retrieve accession: " + type;
            log.error(msg);
            throw new AccessionUpdateException(msg);
        }

        return accessions;
    }
}
