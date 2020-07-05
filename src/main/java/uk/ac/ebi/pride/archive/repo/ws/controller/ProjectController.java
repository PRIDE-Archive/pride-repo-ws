package uk.ac.ebi.pride.archive.repo.ws.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.pride.archive.repo.models.file.ProjectFile;
import uk.ac.ebi.pride.archive.repo.models.project.Project;
import uk.ac.ebi.pride.archive.repo.ws.exception.ProjectAccessException;
import uk.ac.ebi.pride.archive.repo.ws.service.ProjectService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

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
    public Project findById(@Valid @PathVariable Long id) throws ProjectAccessException {
        Optional<Project> optionalProject = projectService.findById(id);
        return optionalProject.orElse(null);
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
    public Project findByAccession(@Valid @PathVariable String accession) throws ProjectAccessException {
        return projectService.findByAccession(accession);
    }

    @GetMapping("/findAllAccessionsChanged")
    public List<String> findAllAccessionsChanged() {
        return projectService.findAllAccessionsChanged();
    }

    @GetMapping("/findMonthlySubmissions")
    public List<List<String>> findMonthlySubmissions() {
        return projectService.findMonthlySubmissions();
    }

    @GetMapping("/findBySubmitterIdAndIsPublic")
    public List<Project> findBySubmitterIdAndIsPublic(@Valid @RequestParam Long submitterId,
                                                      @Valid @RequestParam Boolean isPublic) {
        return projectService.findBySubmitterIdAndIsPublic(submitterId, isPublic);
    }

    @GetMapping("/findBySubmitterId/{submitterId}")
    public List<Project> findBySubmitterId(@Valid @PathVariable Long submitterId) throws ProjectAccessException {
        return projectService.findBySubmitterId(submitterId);
    }

    @GetMapping("/findByReviewer/{user_aap_ref}")
    public List<Project> findByReviewer(@Valid @PathVariable String user_aap_ref) {
        return projectService.findByReviewer(user_aap_ref);
    }

    @PostMapping(path = "/save", consumes = "application/json")
    public Project save(@RequestBody Project project) {
        return projectService.save(project);
    }

    @DeleteMapping(path = "/delete", consumes = "application/json")
    public void delete(@RequestBody Project project) {
        projectService.delete(project);
    }
}

