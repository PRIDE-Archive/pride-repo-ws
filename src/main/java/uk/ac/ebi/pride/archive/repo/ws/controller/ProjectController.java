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

    @GetMapping("/findByIdSummary/{id}")
    public ProjectSummary findByIdSummary(@Valid @PathVariable Long id) throws ProjectAccessException {
        return projectService.findByIdSummary(id);
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

    @GetMapping("/findByAccessionSummary/{accession}")
    public ProjectSummary findByAccessionSummary(@Valid @PathVariable String accession) throws ProjectAccessException {
        return projectService.findByAccessionSummary(accession);
    }

    @GetMapping("/findBySubmitterIdAndIsPublicSummary")
    public List<ProjectSummary> findBySubmitterIdAndIsPublicSummary(@Valid @RequestParam Long submitterId,
                                                                     @Valid @RequestParam Boolean isPublic) {
        return projectService.findBySubmitterIdAndIsPublicSummary(submitterId, isPublic);
    }

    @GetMapping("/findBySubmitterIdSummary/{submitterId}")
    public Collection<ProjectSummary> findBySubmitterIdSummary(@Valid @PathVariable Long submitterId) throws ProjectAccessException {
        return projectService.findBySubmitterIdSummary(submitterId);
    }

    @GetMapping("/findByReviewerSummary/{user_aap_ref}")
    public List<ProjectSummary> findByReviewerSummary(@Valid @PathVariable String user_aap_ref) {
       return projectService.findByReviewerSummary(user_aap_ref);
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

