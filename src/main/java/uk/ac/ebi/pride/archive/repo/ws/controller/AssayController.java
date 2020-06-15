package uk.ac.ebi.pride.archive.repo.ws.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ebi.pride.archive.repo.models.assay.AssaySummary;
import uk.ac.ebi.pride.archive.repo.models.file.FileSummary;
import uk.ac.ebi.pride.archive.repo.ws.exception.AssayAccessException;
import uk.ac.ebi.pride.archive.repo.ws.exception.FileAccessException;
import uk.ac.ebi.pride.archive.repo.ws.service.AssayService;

import javax.validation.Valid;
import java.util.Collection;

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

    @GetMapping("/{assayId}")
    public AssaySummary findById(@Valid @PathVariable Long assayId) throws AssayAccessException {
        return assayService.findById(assayId);
    }

    @GetMapping("/accession/{assayAccession}")
    public AssaySummary findByAccession(@Valid @PathVariable String assayAccession) throws AssayAccessException {
        return assayService.findByAccession(assayAccession);
    }

    @GetMapping("/by-project-id/{projectId}")
    public Collection<FileSummary> findAllByProjectId(@Valid @PathVariable Long projectId) throws FileAccessException {
        return assayService.findAllByProjectId(projectId);
    }

    @GetMapping("/by-project-accession/{projectAccession}")
    public Collection<AssaySummary> findAllByProjectAccession(@Valid @PathVariable String projectAccession) throws AssayAccessException {
        return assayService.findAllByProjectAccession(projectAccession);
    }

    @GetMapping("/count/project/{projectAccession}")
    public Long countByProjectAccession(@Valid @PathVariable String projectAccession) throws AssayAccessException {
        return assayService.countByProjectAccession(projectAccession);
    }
}
