package uk.ac.ebi.pride.archive.repo.ws.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import uk.ac.ebi.pride.archive.repo.models.project.Project;
import uk.ac.ebi.pride.archive.repo.models.project.ProjectSummary;
import uk.ac.ebi.pride.archive.repo.util.ObjectMapper;
import uk.ac.ebi.pride.archive.repo.ws.exception.ProjectAccessException;
import uk.ac.ebi.pride.archive.repo.ws.repository.ProjectRepository;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@Slf4j
public class ProjectService {

    private final ProjectRepository projectRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Optional<Project> findById(Long projectId) throws ProjectAccessException {
        Assert.notNull(projectId, "Project id cannot be null");
        try {
            return projectRepository.findById(projectId);
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

    public List<Project> findBySubmitterId(Long submitterId) throws ProjectAccessException {
        Assert.notNull(submitterId, "Submitter id cannot be null");
        try {
            return projectRepository.findAllBySubmitterId(submitterId);
        } catch (Exception ex) {
            String msg = "Failed to find projects by submitter id: " + submitterId;
            log.error(msg, ex);
            throw new ProjectAccessException(msg, ex);
        }
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

    public Project findByAccession(String accession) throws ProjectAccessException {
        Assert.notNull(accession, "Project accession cannot be null");
        try {
            return projectRepository.findByAccession(accession);
        } catch (Exception ex) {
            String msg = "Failed to find project using project accession: " + accession;
            log.error(msg, ex);
            throw new ProjectAccessException(msg, ex, accession);
        }
    }

    public List<Project> findBySubmitterIdAndIsPublic(Long submitterId, Boolean isPublic) {
        Assert.notNull(submitterId, "submitterId cannot be null");
        Assert.notNull(isPublic, "isPublic cannot be null");
        try {
           return projectRepository.findFilteredBySubmitterIdAndIsPublic(submitterId, isPublic);
        } catch (Exception ex) {
            String msg = "Failed to find project using submitterId : " + submitterId + "& isPublic: " + isPublic;
            log.error(msg, ex);
            throw new ProjectAccessException(msg, ex);
        }
    }

    public List<Project> findByReviewer(String user_aap_ref) {
        Assert.notNull(user_aap_ref, "user_aap_ref cannot be null");
        try {
            return projectRepository.findFilteredByReviewer(user_aap_ref);
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

    @Transactional(readOnly = false)
    public Project save(Project project) {
        return projectRepository.save(project);
    }

    @Transactional(readOnly = false)
    public void delete(Project project) {
        projectRepository.delete(project);
    }
}

