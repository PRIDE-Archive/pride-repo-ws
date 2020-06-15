package uk.ac.ebi.pride.archive.repo.ws.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.pride.archive.repo.models.project.ProjectSummary;
import uk.ac.ebi.pride.archive.repo.ws.exception.ProjectAccessException;
import uk.ac.ebi.pride.archive.repo.ws.service.ProjectService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@Validated
@RequestMapping("/project")
@Slf4j
@Tag(name="Project")
public class ProjectController {

    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

//    @GetMapping("")
//    public Iterable<Project> findAll() throws ProjectAccessException {
//        return projectService.findAll();
//    }

    @GetMapping("/{id}")
    public ProjectSummary findById(@Valid @PathVariable Long id) throws ProjectAccessException {
        return projectService.findById(id);
    }

    @GetMapping("/count")
    public Long count() throws ProjectAccessException {
        return projectService.count();
    }

    @GetMapping("/accessions")
    public List<String> getAllAccessions() throws ProjectAccessException {
        return projectService.getAllAccessions();
    }

    @GetMapping("/accessions/public")
    public List<String> getAllAccessions() throws ProjectAccessException {
        return projectService.getAllAccessions(true);
    }

    @GetMapping("/by-accession/{accession}")
    public ProjectSummary findByAccession(@Valid @PathVariable String accession) throws ProjectAccessException {
        return projectService.findByAccession(accession);
    }

    @GetMapping("/submitter-projects")
    public List<ProjectSummary> findFilteredBySubmitterIdAndIsPublic(@Valid @RequestParam Long submitterId,
                                                                     @Valid @RequestParam Boolean isPublic) {
        return projectService.findFilteredBySubmitterIdAndIsPublic(submitterId, isPublic);
    }

    @GetMapping("/submitter-projects/{submitterId}")
    public Collection<ProjectSummary> findAllBySubmitterId(@Valid @PathVariable Long submitterId) throws ProjectAccessException {
        return projectService.findAllBySubmitterId(submitterId);
    }

    @GetMapping("/reviewer-projects/{user_aap_ref}")
    public List<ProjectSummary> findFilteredByReviewer(@Valid @PathVariable String user_aap_ref) {
       return projectService.findFilteredByReviewer(user_aap_ref);
    }

    @GetMapping("/accessions-changed")
    public List<String> findAllAccessionsChanged() {
        return projectService.findAllAccessionsChanged();
    }

    @GetMapping("/monthly-submissions")
    public List<List<String>> findMonthlySubmissions() {
        return projectService.findMonthlySubmissions();
    }
}

