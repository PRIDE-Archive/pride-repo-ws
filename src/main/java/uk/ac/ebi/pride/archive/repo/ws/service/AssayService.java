package uk.ac.ebi.pride.archive.repo.ws.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import uk.ac.ebi.pride.archive.repo.models.assay.Assay;
import uk.ac.ebi.pride.archive.repo.models.project.Project;
import uk.ac.ebi.pride.archive.repo.ws.exception.AssayAccessException;
import uk.ac.ebi.pride.archive.repo.ws.repository.AssayRepository;
import uk.ac.ebi.pride.archive.repo.ws.repository.ProjectRepository;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@Slf4j
public class AssayService {

    private AssayRepository assayRepository;

    private ProjectRepository projectRepository;

    @Autowired
    public AssayService(AssayRepository assayRepository, ProjectRepository projectRepository) {
        this.assayRepository = assayRepository;
        this.projectRepository = projectRepository;
    }

    public Optional<Assay> findById(Long assayId) throws AssayAccessException {
        Assert.notNull(assayId, "Assay id cannot be empty");
        try {
            return assayRepository.findById(assayId);
        } catch (Exception ex) {
            String msg = "Failed to find assay by id: " + assayId;
            log.error(msg, ex);
            throw new AssayAccessException(msg, ex);
        }
    }

    public Assay findByAccession(String assayAccession) throws AssayAccessException {
        Assert.notNull(assayAccession, "Assay accession cannot be empty");
        try {
            return assayRepository.findByAccession(assayAccession);
        } catch (Exception ex) {
            String msg = "Failed to find assay by accession: " + assayAccession;
            log.error(msg, ex);
            throw new AssayAccessException(msg, ex, null, assayAccession);
        }
    }

    public List<Assay> findAllByProjectId(Long projectId) throws AssayAccessException {
        Assert.notNull(projectId, "Project accession cannot be null");
        try {
            return assayRepository.findAllByProjectId(projectId);
        } catch (
                Exception ex) {
            String msg = "Failed to find assays by projectId: " + projectId;
            log.error(msg, ex);
            throw new AssayAccessException(msg, ex);
        }
    }

    public List<Assay> findAllByProjectAccession(String projectAccession) throws AssayAccessException {
        Assert.notNull(projectAccession, "Project accession cannot be null");
        try {
            List<Assay> assays = new LinkedList<>();
            Project project = projectRepository.findByAccession(projectAccession);
            if (project != null) {
               return assayRepository.findAllByProjectId(project.getId());
            }
            return assays;
        } catch (Exception ex) {
            String msg = "Failed to find assays by project accession: " + projectAccession;
            log.error(msg, ex);
            throw new AssayAccessException(msg, ex, projectAccession, null);
        }
    }

    public Long countByProjectAccession(String projectAccession) throws AssayAccessException {
        Assert.notNull(projectAccession, "Project accession cannot be null");
        Long assayCount = 0L;

        try {
            Project project = projectRepository.findByAccession(projectAccession);
            if (project != null) {
                assayCount = assayRepository.countByProjectId(project.getId());
            }
        } catch (Exception ex) {
            String msg = "Failed to find assays by project accession: " + projectAccession;
            log.error(msg, ex);
            throw new AssayAccessException(msg, ex, projectAccession, null);
        }

        return assayCount;
    }

    @Transactional(readOnly = false)
    public Assay save(Assay assay) {
        return assayRepository.save(assay);
    }

    @Transactional(readOnly = false)
    public void delete(Assay assay) {
        assayRepository.delete(assay);
    }
}
