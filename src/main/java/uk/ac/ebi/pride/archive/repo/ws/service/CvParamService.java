package uk.ac.ebi.pride.archive.repo.ws.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import uk.ac.ebi.pride.archive.repo.models.param.CvParam;
import uk.ac.ebi.pride.archive.repo.ws.exception.CvParamAccessException;
import uk.ac.ebi.pride.archive.repo.ws.repository.CvParamRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@Slf4j
public class CvParamService {

    private final CvParamRepository cvParamRepository;

   @Autowired
    public CvParamService(CvParamRepository cvParamRepository) {
        this.cvParamRepository = cvParamRepository;
    }

    public CvParam findByAccession(String accession) throws CvParamAccessException{
        Assert.notNull(accession, "accession cannot be empty");
        try {
            return cvParamRepository.findByAccession(accession);
        } catch (Exception ex) {
            String msg = "Failed to find CvParam by accession: " + accession;
            log.error(msg, ex);
            throw new CvParamAccessException(msg, ex, accession);
        }
    }

    public List<CvParam> findAll() throws CvParamAccessException {
        return cvParamRepository.findAll();
    }

    @Transactional(readOnly = false)
    public CvParam save(CvParam cvParam) {
        return cvParamRepository.save(cvParam);
    }
}
