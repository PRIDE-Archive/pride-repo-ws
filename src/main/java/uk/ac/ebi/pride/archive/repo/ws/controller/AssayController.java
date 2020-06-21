package uk.ac.ebi.pride.archive.repo.ws.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.pride.archive.repo.models.assay.Assay;
import uk.ac.ebi.pride.archive.repo.ws.exception.AssayAccessException;
import uk.ac.ebi.pride.archive.repo.ws.service.AssayService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@Validated
@RequestMapping("/assay")
@Slf4j
@Tag(name = "Assay")
public class AssayController {

    private final AssayService assayService;

    @Autowired
    public AssayController(AssayService assayService) {
        this.assayService = assayService;
    }

    @GetMapping("/findById/{assayId}")
    public Assay findById(@Valid @PathVariable Long assayId) throws AssayAccessException {
        Optional<Assay> optional = assayService.findById(assayId);
        return optional.orElse(null);
    }

    @GetMapping("/findByAccession/{assayAccession}")
    public Assay findByAccession(@Valid @PathVariable String assayAccession) throws AssayAccessException {
        return assayService.findByAccession(assayAccession);
    }

    @GetMapping("/findAllByProjectId/{projectId}")
    public List<Assay> findAllByProjectId(@Valid @PathVariable Long projectId) throws AssayAccessException {
        return assayService.findAllByProjectId(projectId);
    }

    @GetMapping("/findAllByProjectAccession/{projectAccession}")
    public List<Assay> findAllByProjectAccession(@Valid @PathVariable String projectAccession) throws AssayAccessException {
        return assayService.findAllByProjectAccession(projectAccession);
    }

    @GetMapping("/countByProjectAccession/{projectAccession}")
    public Long countByProjectAccession(@Valid @PathVariable String projectAccession) throws AssayAccessException {
        return assayService.countByProjectAccession(projectAccession);
    }

    @PostMapping(path = "/save", consumes = "application/json")
    public Assay saveAssay(@RequestBody Assay assay) {
        return assayService.saveAssay(assay);
    }

//    @GetMapping("/findByIdSummary/{assayId}")
//    public AssaySummary findByIdSummary(@Valid @PathVariable Long assayId) throws AssayAccessException {
//        return assayService.findByIdSummary(assayId);
//    }
//
//    @GetMapping("/findByAccessionSummary/{assayAccession}")
//    public AssaySummary findByAccessionSummary(@Valid @PathVariable String assayAccession) throws AssayAccessException {
//        return assayService.findByAccessionSummary(assayAccession);
//    }
//
//    @GetMapping("/findAllByProjectIdSummary/{projectId}")
//    public Collection<AssaySummary> findAllByProjectIdSummary(@Valid @PathVariable Long projectId) throws AssayAccessException {
//        return assayService.findAllByProjectIdSummary(projectId);
//    }
//
//    @GetMapping("/findAllByProjectAccessionSummary/{projectAccession}")
//    public Collection<AssaySummary> findAllByProjectAccessionSummary(@Valid @PathVariable String projectAccession) throws AssayAccessException {
//        return assayService.findAllByProjectAccessionSummary(projectAccession);
//    }

}
