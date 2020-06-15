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

    @GetMapping("/findById/{id}")
    public ProjectSummary findById(@Valid @PathVariable Long id) throws ProjectAccessException {
        return projectService.findById(id);
    }

    @GetMapping("/count")
    public Long count() throws ProjectAccessException {
        return projectService.count();
    }

    @GetMapping("/getAllAccessions")
    public List<String> getAllAccessions() throws ProjectAccessException {
        return projectService.getAllAccessions();
    }

    @GetMapping("/getAllPublicAccessions")
    public List<String> getAllPublicAccessions() throws ProjectAccessException {
        return projectService.getAllPublicAccessions();
    }

    @GetMapping("/findByAccession/{accession}")
    public ProjectSummary findByAccession(@Valid @PathVariable String accession) throws ProjectAccessException {
        return projectService.findByAccession(accession);
    }

    @GetMapping("/findBySubmitterIdAndIsPublic")
    public List<ProjectSummary> findBySubmitterIdAndIsPublic(@Valid @RequestParam Long submitterId,
                                                                     @Valid @RequestParam Boolean isPublic) {
        return projectService.findBySubmitterIdAndIsPublic(submitterId, isPublic);
    }

    @GetMapping("/findBySubmitterId/{submitterId}")
    public Collection<ProjectSummary> findBySubmitterId(@Valid @PathVariable Long submitterId) throws ProjectAccessException {
        return projectService.findBySubmitterId(submitterId);
    }

    @GetMapping("/findByReviewer/{user_aap_ref}")
    public List<ProjectSummary> findByReviewer(@Valid @PathVariable String user_aap_ref) {
       return projectService.findByReviewer(user_aap_ref);
    }

    @GetMapping("/findAllAccessionsChanged")
    public List<String> findAllAccessionsChanged() {
        return projectService.findAllAccessionsChanged();
    }

    @GetMapping("/findMonthlySubmissions")
    public List<List<String>> findMonthlySubmissions() {
        return projectService.findMonthlySubmissions();
    }
}

