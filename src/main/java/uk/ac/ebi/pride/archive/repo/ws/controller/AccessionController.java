package uk.ac.ebi.pride.archive.repo.ws.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ebi.pride.archive.repo.models.accession.AccessionInputWrapper;
import uk.ac.ebi.pride.archive.repo.models.accession.PrideAccessions;
import uk.ac.ebi.pride.archive.repo.ws.exception.AccessionUpdateException;
import uk.ac.ebi.pride.archive.repo.ws.service.AccessionService;
import uk.ac.ebi.pride.archive.repo.ws.utils.accession.AccessionType;
import uk.ac.ebi.pride.archive.repo.ws.utils.accession.AccessionUpdateMode;

@RestController
@Validated
@RequestMapping("/accession-provider")
@Slf4j
@Tag(name = "Accession-Provider")
public class AccessionController {

    private final AccessionService accessionService;

    @Autowired
    public AccessionController(AccessionService accessionService) {
        this.accessionService = accessionService;
    }

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PrideAccessions> getAccessions(@RequestBody AccessionInputWrapper accessionInputWrapper) {

        Integer count = Integer.valueOf(accessionInputWrapper.getCount());
        String type = accessionInputWrapper.getType();
        String mode = accessionInputWrapper.getMode();
        Object[] args = {accessionInputWrapper.getCount(), type, mode};
        log.info("Retrieving {} accessions for {} under {} mode", args);

        // map mode from string the accepted value by the accession service
        AccessionUpdateMode updateMode = AccessionUpdateMode.getMode(mode);
        if (updateMode == null) {
            throw new AccessionUpdateException("Failed to recognize update mode: " + mode);
        }

        AccessionType accessionType = AccessionType.getType(type);
        if (accessionType == null) {
            throw new AccessionUpdateException("Failed to recognize accession type: " + type);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(accessionService.getAccessions(count, accessionType, updateMode));
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PrideAccessions> getLastAccession(@RequestParam String type,
                                                            @RequestParam String mode) {

        log.info("Checking last accessions for " + type);

        // map mode from string the accepted value by the accession service
        AccessionUpdateMode updateMode = AccessionUpdateMode.getMode(mode);
        if (updateMode == null) {
            throw new AccessionUpdateException("Failed to recognize update mode: " + mode);
        }

        AccessionType accessionType = AccessionType.getType(type);
        if (accessionType == null) {
            throw new AccessionUpdateException("Failed to recognize accession type: " + type);
        }

        return ResponseEntity.status(HttpStatus.OK).body(accessionService.getLastAccession(accessionType, updateMode));
    }
}
