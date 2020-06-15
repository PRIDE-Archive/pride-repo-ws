package uk.ac.ebi.pride.archive.repo.ws.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import uk.ac.ebi.pride.archive.repo.models.assay.Assay;
import uk.ac.ebi.pride.archive.repo.models.file.FileSummary;
import uk.ac.ebi.pride.archive.repo.models.file.ProjectFile;
import uk.ac.ebi.pride.archive.repo.models.project.Project;
import uk.ac.ebi.pride.archive.repo.util.ObjectMapper;
import uk.ac.ebi.pride.archive.repo.ws.exception.FileAccessException;
import uk.ac.ebi.pride.archive.repo.ws.repository.AssayRepository;
import uk.ac.ebi.pride.archive.repo.ws.repository.ProjectFileRepository;
import uk.ac.ebi.pride.archive.repo.ws.repository.ProjectRepository;

import java.util.Collection;
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
public class FileService {

    private ProjectFileRepository projectFileRepository;

    private ProjectRepository projectRepository;

    private AssayRepository assayRepository;

    @Autowired
    public FileService(
            ProjectFileRepository projectFileRepository,
            ProjectRepository projectRepository,
            AssayRepository assayRepository) {
        this.projectFileRepository = projectFileRepository;
        this.projectRepository = projectRepository;
        this.assayRepository = assayRepository;
    }

    public Optional<ProjectFile> findById(Long fileId) throws FileAccessException {
        Assert.notNull(fileId, "File id cannot be empty");
        try {
            return projectFileRepository.findById(fileId);
        } catch (Exception ex) {
            String msg = "Failed to find file by id: " + fileId;
            log.error(msg, ex);
            throw new FileAccessException(msg, ex);
        }
    }

    public List<ProjectFile> findAllByProjectAccession(String projectAccession) throws FileAccessException {
        Assert.notNull(projectAccession, "Project accession cannot be null");
        try {
            Project project = projectRepository.findByAccession(projectAccession);
            return projectFileRepository.findAllByProjectId(project.getId());
        } catch (Exception ex) {
            String msg = "Failed to find files by project accession: " + projectAccession;
            log.error(msg, ex);
            throw new FileAccessException(msg, ex, projectAccession, null);
        }
    }

    public List<ProjectFile> findAllByAssayAccession(String assayAccession) throws FileAccessException {
        Assert.notNull(assayAccession, "Assay accession cannot be null");
        try {
            Assay assay = assayRepository.findByAccession(assayAccession);
            return projectFileRepository.findAllByAssayId(assay.getId());
        } catch (Exception ex) {
            String msg = "Failed to find files by assay accession: " + assayAccession;
            log.error(msg, ex);
            throw new FileAccessException(msg, ex, null, assayAccession);
        }
    }

    public List<ProjectFile> findAllByProjectId(Long projectId) throws FileAccessException {
        Assert.notNull(projectId, "Project id cannot be empty");
        try {
            return projectFileRepository.findAllByProjectId(projectId);
        } catch (Exception ex) {
            String msg = "Failed to find files by project id: " + projectId;
            log.error(msg, ex);
            throw new FileAccessException(msg, ex);
        }
    }

    public List<ProjectFile> findAllByAssayId(Long assayId) throws FileAccessException {
        Assert.notNull(assayId, "Assay id cannot be empty");
        try {
            return projectFileRepository.findAllByAssayId(assayId);
        } catch (Exception ex) {
            String msg = "Failed to find files by assay id: " + assayId;
            log.error(msg, ex);
            throw new FileAccessException(msg, ex);
        }
    }

    public FileSummary findByIdSummary(Long fileId) throws FileAccessException {
        Assert.notNull(fileId, "File id cannot be empty");
        try {
            Optional<ProjectFile> projectFile = projectFileRepository.findById(fileId);

            return ObjectMapper.mapProjectFileToFileSummary(projectFile.get());
        } catch (Exception ex) {
            String msg = "Failed to find file by id: " + fileId;
            log.error(msg, ex);
            throw new FileAccessException(msg, ex);
        }
    }

    public Collection<FileSummary> findAllByProjectAccessionSummary(String projectAccession)
            throws FileAccessException {
        Assert.notNull(projectAccession, "Project accession cannot be null");
        try {
            Project project = projectRepository.findByAccession(projectAccession);
            List<ProjectFile> projectFiles = projectFileRepository.findAllByProjectId(project.getId());

            return ObjectMapper.mapProjectFileToFileSummaries(projectFiles);
        } catch (Exception ex) {
            String msg = "Failed to find files by project accession: " + projectAccession;
            log.error(msg, ex);
            throw new FileAccessException(msg, ex, projectAccession, null);
        }
    }

    public Collection<FileSummary> findAllByAssayAccessionSummary(String assayAccession)
            throws FileAccessException {
        Assert.notNull(assayAccession, "Assay accession cannot be null");
        try {
            Assay assay = assayRepository.findByAccession(assayAccession);
            List<ProjectFile> projectFiles = projectFileRepository.findAllByAssayId(assay.getId());
            return ObjectMapper.mapProjectFileToFileSummaries(projectFiles);
        } catch (Exception ex) {
            String msg = "Failed to find files by assay accession: " + assayAccession;
            log.error(msg, ex);
            throw new FileAccessException(msg, ex, null, assayAccession);
        }
    }

    public Collection<FileSummary> findAllByProjectIdSummary(Long projectId) throws FileAccessException {
        Assert.notNull(projectId, "Project id cannot be empty");
        try {
            List<ProjectFile> projectFiles = projectFileRepository.findAllByProjectId(projectId);

            return ObjectMapper.mapProjectFileToFileSummaries(projectFiles);
        } catch (Exception ex) {
            String msg = "Failed to find files by project id: " + projectId;
            log.error(msg, ex);
            throw new FileAccessException(msg, ex);
        }
    }

    public Collection<FileSummary> findAllByAssayIdSummary(Long assayId) throws FileAccessException {
        Assert.notNull(assayId, "Assay id cannot be empty");
        try {
            List<ProjectFile> projectFiles = projectFileRepository.findAllByAssayId(assayId);

            return ObjectMapper.mapProjectFileToFileSummaries(projectFiles);
        } catch (Exception ex) {
            String msg = "Failed to find files by assay id: " + assayId;
            log.error(msg, ex);
            throw new FileAccessException(msg, ex);
        }
    }
}
