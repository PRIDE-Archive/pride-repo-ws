package uk.ac.ebi.pride.archive.repo.ws.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import uk.ac.ebi.pride.archive.repo.models.project.Project;
import uk.ac.ebi.pride.archive.repo.models.project.ProjectSummary;
import uk.ac.ebi.pride.archive.repo.util.ObjectMapper;
import uk.ac.ebi.pride.archive.repo.ws.exception.ProjectAccessException;
import uk.ac.ebi.pride.archive.repo.ws.repository.ProjectRepository;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Rui Wang
 * @author Jose A. Dianes
 * @version $Id$
 * <p>
 */
@Service
@Transactional(readOnly = true)
@Slf4j
public class ProjectService {

    private ProjectRepository projectRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Iterable<Project> findAll() throws ProjectAccessException {
        return projectRepository.findAll();
    }

    public ProjectSummary findByIdSummary(Long projectId) throws ProjectAccessException {
        Assert.notNull(projectId, "Project id cannot be null");

        try {
            Optional<Project> project = projectRepository.findById(projectId);
            return project.map(ObjectMapper::mapProjectToProjectSummary).orElse(null);
        } catch (Exception ex) {
            String msg = "Failed to find project using project id: " + projectId;
            log.error(msg, ex);
            throw new ProjectAccessException(msg, ex);
        }
    }

    public long count() {
        return projectRepository.count();
    }

    public List<String> getAllAccessions() throws ProjectAccessException {
        return projectRepository.findAllAccessions();
    }

    public List<String> getAllPublicAccessions() throws ProjectAccessException {
        return projectRepository.findAllPublicAccessions();
    }

    public Collection<ProjectSummary> findBySubmitterIdSummary(Long submitterId) throws ProjectAccessException {
        Assert.notNull(submitterId, "Submitter id cannot be null");
        try {
            Collection<ProjectSummary> projectSummaries = new LinkedList<>();

            for (Project project : projectRepository.findAllBySubmitterId(submitterId)) {
                ProjectSummary projectSummary = ObjectMapper.mapProjectToProjectSummary(project);
                projectSummaries.add(projectSummary);
            }
            return projectSummaries;
        } catch (Exception ex) {
            String msg = "Failed to find projects by submitter id: " + submitterId;
            log.error(msg, ex);
            throw new ProjectAccessException(msg, ex);
        }
    }

    public ProjectSummary findByAccessionSummary(String accession) throws ProjectAccessException {
        Assert.notNull(accession, "Project accession cannot be null");

        try {
            Project project = projectRepository.findByAccession(accession);
            return ObjectMapper.mapProjectToProjectSummary(project);
        } catch (Exception ex) {
            String msg = "Failed to find project using project accession: " + accession;
            log.error(msg, ex);
            throw new ProjectAccessException(msg, ex, accession);
        }
    }


    public List<ProjectSummary> findBySubmitterIdAndIsPublicSummary(Long submitterId, Boolean isPublic) {
        Assert.notNull(submitterId, "submitterId cannot be null");
        Assert.notNull(isPublic, "isPublic cannot be null");

        try {
            List<Project> projects = projectRepository.findFilteredBySubmitterIdAndIsPublic(submitterId, isPublic);
            return projects.stream().map(ObjectMapper::mapProjectToProjectSummary).collect(Collectors.toList());
        } catch (Exception ex) {
            String msg = "Failed to find project using submitterId : " + submitterId + "& isPublic: " + isPublic;
            log.error(msg, ex);
            throw new ProjectAccessException(msg, ex);
        }
    }


    public List<ProjectSummary> findByReviewerSummary(String user_aap_ref) {
        Assert.notNull(user_aap_ref, "user_aap_ref cannot be null");
        try {
            List<Project> projects = projectRepository.findFilteredByReviewer(user_aap_ref);
            return projects.stream().map(ObjectMapper::mapProjectToProjectSummary).collect(Collectors.toList());
        } catch (Exception ex) {
            String msg = "Failed to find project using Reviewer user_aap_ref : " + user_aap_ref;
            log.error(msg, ex);
            throw new ProjectAccessException(msg, ex);
        }
    }

    public List<List<String>> findMonthlySubmissions() {
        return projectRepository.findMonthlySubmissions();
    }

    public List<String> findAllAccessionsChanged() {
        return projectRepository.findAllAccessionsChanged();
    }
}

