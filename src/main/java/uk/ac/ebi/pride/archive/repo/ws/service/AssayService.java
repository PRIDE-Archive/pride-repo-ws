package uk.ac.ebi.pride.archive.repo.ws.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import uk.ac.ebi.pride.archive.repo.models.assay.Assay;
import uk.ac.ebi.pride.archive.repo.models.assay.AssaySummary;
import uk.ac.ebi.pride.archive.repo.models.project.Project;
import uk.ac.ebi.pride.archive.repo.util.ObjectMapper;
import uk.ac.ebi.pride.archive.repo.ws.exception.AssayAccessException;
import uk.ac.ebi.pride.archive.repo.ws.repository.AssayRepository;
import uk.ac.ebi.pride.archive.repo.ws.repository.ProjectRepository;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * @author Rui Wang
 * @author Jose A. Dianes
 * @version $Id$
 * <p>
 */
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

    public AssaySummary findByIdSummary(Long assayId) throws AssayAccessException {
        Assert.notNull(assayId, "Assay id cannot be empty");

        try {
            Optional<Assay> assay = assayRepository.findById(assayId);
            return ObjectMapper.mapAssayToAssaySummary(assay.get());
        } catch (Exception ex) {
            String msg = "Failed to find assay by id: " + assayId;
            log.error(msg, ex);
            throw new AssayAccessException(msg, ex);
        }
    }

    public AssaySummary findByAccessionSummary(String assayAccession) throws AssayAccessException {
        Assert.notNull(assayAccession, "Assay accession cannot be empty");

        try {
            Assay assay = assayRepository.findByAccession(assayAccession);
            return ObjectMapper.mapAssayToAssaySummary(assay);
        } catch (Exception ex) {
            String msg = "Failed to find assay by accession: " + assayAccession;
            log.error(msg, ex);
            throw new AssayAccessException(msg, ex, null, assayAccession);
        }
    }

    public Collection<AssaySummary> findAllByProjectIdSummary(Long projectId) throws AssayAccessException {
        Assert.notNull(projectId, "Project accession cannot be null");
        try {
            Collection<AssaySummary> assaySummaries = new LinkedList<>();
            Collection<Assay> assays = assayRepository.findAllByProjectId(projectId);
            for (Assay assay : assays) {
                AssaySummary assaySummary = ObjectMapper.mapAssayToAssaySummary(assay);
                assaySummaries.add(assaySummary);
            }
            return assaySummaries;
        } catch (
                Exception ex) {
            String msg = "Failed to find assays by projectId: " + projectId;
            log.error(msg, ex);
            throw new AssayAccessException(msg, ex);
        }
    }

    public Collection<AssaySummary> findAllByProjectAccessionSummary(String projectAccession) throws AssayAccessException {
        Assert.notNull(projectAccession, "Project accession cannot be null");

        // get the project
        // TODO: in the future we will need a DAO method directly in the assay DAO to get all the
        // experiments by Project accession
        try {
            Collection<AssaySummary> assaySummaries = new LinkedList<>();

            Project project = projectRepository.findByAccession(projectAccession);

            if (project != null) {
                Collection<Assay> assays = assayRepository.findAllByProjectId(project.getId());
                for (Assay assay : assays) {
                    AssaySummary assaySummary = ObjectMapper.mapAssayToAssaySummary(assay);
                    assaySummaries.add(assaySummary);
                }
            }

            return assaySummaries;
        } catch (Exception ex) {
            String msg = "Failed to find assays by project accession: " + projectAccession;
            log.error(msg, ex);
            throw new AssayAccessException(msg, ex, projectAccession, null);
        }
    }

    public Page<AssaySummary> findAllByProjectAccession(String projectAccession, Pageable pageable) throws AssayAccessException {
        Assert.notNull(projectAccession, "Project accession cannot be null");

        // get the project
        // TODO: in the future we will need a DAO method directly in the assay DAO to get all the
        // experiments by Project accession
        try {
            List<AssaySummary> assaySummaries = new LinkedList<>();

            Project project = projectRepository.findByAccession(projectAccession);
            Page<Assay> assays = null;
            Page<AssaySummary> page;

            if (project != null) {
                assays = assayRepository.findAllByProjectId(project.getId(), pageable);
                for (Assay assay : assays) {
                    AssaySummary assaySummary = ObjectMapper.mapAssayToAssaySummary(assay);
                    assaySummaries.add(assaySummary);
                }
            }

            if (assays != null) {
                page = new PageImpl<>(assaySummaries, pageable, assays.getTotalElements());
            } else {
                page = new PageImpl<>(assaySummaries);
            }

            return page;

        } catch (Exception ex) {
            String msg = "Failed to find assays by project accession: " + projectAccession;
            log.error(msg, ex);
            throw new AssayAccessException(msg, ex, projectAccession, null);
        }
    }

    public Long countByProjectAccession(String projectAccession) throws AssayAccessException {
        Assert.notNull(projectAccession, "Project accession cannot be null");
        Long assayCount = 0L;

        // get the project
        // TODO: in the future we will need a DAO method directly in the assay DAO to get all the
        // experiments by Project accession
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
}
