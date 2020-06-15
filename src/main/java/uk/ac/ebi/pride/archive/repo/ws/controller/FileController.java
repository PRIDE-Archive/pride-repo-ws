package uk.ac.ebi.pride.archive.repo.ws.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.pride.archive.repo.models.file.FileSummary;
import uk.ac.ebi.pride.archive.repo.ws.exception.FileAccessException;
import uk.ac.ebi.pride.archive.repo.ws.service.FileService;

import javax.validation.Valid;
import java.util.Collection;

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
    public FileSummary findById(@Valid @PathVariable Long fileId) throws FileAccessException {
        return fileService.findById(fileId);
    }

    @GetMapping("/findAllByProjectId/{projectId}")
    public Collection<FileSummary> findAllByProjectId(@Valid @PathVariable Long projectId) throws FileAccessException {
        return fileService.findAllByProjectId(projectId);
    }

    @GetMapping("/findAllByProjectAccession/{projectAccession}")
    public Collection<FileSummary> findAllByProjectAccession(@Valid @PathVariable String projectAccession) throws FileAccessException {
        return fileService.findAllByProjectAccession(projectAccession);
    }

    @GetMapping("/findAllByAssayId/{assayId}")
    public Collection<FileSummary> findAllByAssayId(@Valid @PathVariable Long assayId) throws FileAccessException {
        return fileService.findAllByAssayId(assayId);
    }

    @GetMapping("/findAllByAssayAccession/{assayAccession}")
    public Collection<FileSummary> findAllByAssayAccession(@Valid @PathVariable String assayAccession) throws FileAccessException {
        return fileService.findAllByAssayAccession(assayAccession);
    }

}
