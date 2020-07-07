package uk.ac.ebi.pride.archive.repo.ws.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ebi.pride.archive.repo.models.submission.SubmissionDto;
import uk.ac.ebi.pride.archive.repo.ws.service.SubmissionService;

@RestController
@Validated
@RequestMapping("/submission")
@Slf4j
@Tag(name = "Submission")
public class SubmissionController {

    private final SubmissionService submissionService;

    @Autowired
    public SubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @PostMapping(path = "/save", consumes = "application/json")
    public void save(@RequestBody SubmissionDto submissionDto) {
        submissionService.save(submissionDto);
    }
}
