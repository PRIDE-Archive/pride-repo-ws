package uk.ac.ebi.pride.archive.repo.ws.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.pride.archive.repo.models.file.ProjectFile;
import uk.ac.ebi.pride.archive.repo.ws.exception.FileAccessException;
import uk.ac.ebi.pride.archive.repo.ws.service.FileService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@Validated
@RequestMapping("/files")
@Slf4j
@Tag(name = "File")
public class FileController {

    private final FileService fileService;

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping("/findById/{fileId}")
    public ProjectFile findById(@Valid @PathVariable Long fileId) throws FileAccessException {
        Optional<ProjectFile> byId = fileService.findById(fileId);
        return byId.orElse(null);
    }

    @GetMapping("/findAllByProjectId/{projectId}")
    public List<ProjectFile> findAllByProjectId(@Valid @PathVariable Long projectId) throws FileAccessException {
        return fileService.findAllByProjectId(projectId);
    }

    @GetMapping("/findAllByProjectAccession/{projectAccession}")
    public List<ProjectFile> findAllByProjectAccession(@Valid @PathVariable String projectAccession) throws FileAccessException {
        return fileService.findAllByProjectAccession(projectAccession);
    }

    @GetMapping("/findAllByAssayId/{assayId}")
    public List<ProjectFile> findAllByAssayId(@Valid @PathVariable Long assayId) throws FileAccessException {
        return fileService.findAllByAssayId(assayId);
    }

    @GetMapping("/findAllByAssayAccession/{assayAccession}")
    public List<ProjectFile> findAllByAssayAccession(@Valid @PathVariable String assayAccession) throws FileAccessException {
        return fileService.findAllByAssayAccession(assayAccession);
    }

    @PostMapping(path = "/save", consumes = "application/json")
    public ProjectFile save(@RequestBody ProjectFile projectFile) {
        return fileService.save(projectFile);
    }

    @DeleteMapping(path = "/delete", consumes = "application/json")
    public void delete(@RequestBody ProjectFile file) {
        fileService.delete(file);
    }
}
