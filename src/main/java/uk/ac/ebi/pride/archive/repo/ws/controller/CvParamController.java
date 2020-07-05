package uk.ac.ebi.pride.archive.repo.ws.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.pride.archive.repo.models.param.CvParam;
import uk.ac.ebi.pride.archive.repo.ws.exception.CvParamAccessException;
import uk.ac.ebi.pride.archive.repo.ws.service.CvParamService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Validated
@RequestMapping("/cvparam")
@Slf4j
@Tag(name = "CvParam")
public class CvParamController {

    private final CvParamService cvParamService;

    @Autowired
    public CvParamController(CvParamService cvParamService) {
        this.cvParamService = cvParamService;
    }

    @GetMapping("/findByAccession/{accession}")
    public CvParam findByAccession(@Valid @PathVariable String accession) throws CvParamAccessException {
        return cvParamService.findByAccession(accession);
    }

    @GetMapping("/findAll")
    public List<CvParam> findAll() throws CvParamAccessException {
        return cvParamService.findAll();
    }

    @PostMapping(path = "/save", consumes = "application/json")
    public CvParam save(@RequestBody CvParam assay) {
        return cvParamService.save(assay);
    }
}
