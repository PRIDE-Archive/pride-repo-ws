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

    @GetMapping("/findByIdSummary/{fileId}")
    public FileSummary findByIdSummary(@Valid @PathVariable Long fileId) throws FileAccessException {
        return fileService.findByIdSummary(fileId);
    }

    @GetMapping("/findAllByProjectIdSummary/{projectId}")
    public Collection<FileSummary> findAllByProjectIdSummary(@Valid @PathVariable Long projectId) throws FileAccessException {
        return fileService.findAllByProjectIdSummary(projectId);
    }

    @GetMapping("/findAllByProjectAccessionSummary/{projectAccession}")
    public Collection<FileSummary> findAllByProjectAccessionSummary(@Valid @PathVariable String projectAccession) throws FileAccessException {
        return fileService.findAllByProjectAccessionSummary(projectAccession);
    }

    @GetMapping("/findAllByAssayIdSummary/{assayId}")
    public Collection<FileSummary> findAllByAssayIdSummary(@Valid @PathVariable Long assayId) throws FileAccessException {
        return fileService.findAllByAssayIdSummary(assayId);
    }

    @GetMapping("/findAllByAssayAccessionSummary/{assayAccession}")
    public Collection<FileSummary> findAllByAssayAccessionSummary(@Valid @PathVariable String assayAccession) throws FileAccessException {
        return fileService.findAllByAssayAccessionSummary(assayAccession);
    }

}
