package uk.ac.ebi.pride.archive.repo.ws.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import uk.ac.ebi.pride.archive.repo.models.assay.Assay;
import uk.ac.ebi.pride.archive.repo.models.file.ProjectFile;
import uk.ac.ebi.pride.archive.repo.models.project.Project;
import uk.ac.ebi.pride.archive.repo.ws.exception.FileAccessException;
import uk.ac.ebi.pride.archive.repo.ws.repository.AssayRepository;
import uk.ac.ebi.pride.archive.repo.ws.repository.FileRepository;
import uk.ac.ebi.pride.archive.repo.ws.repository.ProjectRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@Slf4j
public class FileService {

    private final FileRepository fileRepository;
    private final ProjectRepository projectRepository;
    private final AssayRepository assayRepository;

    @Autowired
    public FileService(
            FileRepository fileRepository,
            ProjectRepository projectRepository,
            AssayRepository assayRepository) {
        this.fileRepository = fileRepository;
        this.projectRepository = projectRepository;
        this.assayRepository = assayRepository;
    }

    public Optional<ProjectFile> findById(Long fileId) throws FileAccessException {
        Assert.notNull(fileId, "File id cannot be empty");
        try {
            return fileRepository.findById(fileId);
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
            return fileRepository.findAllByProjectId(project.getId());
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
            return fileRepository.findAllByAssayId(assay.getId());
        } catch (Exception ex) {
            String msg = "Failed to find files by assay accession: " + assayAccession;
            log.error(msg, ex);
            throw new FileAccessException(msg, ex, null, assayAccession);
        }
    }

    public List<ProjectFile> findAllByProjectId(Long projectId) throws FileAccessException {
        Assert.notNull(projectId, "Project id cannot be empty");
        try {
            return fileRepository.findAllByProjectId(projectId);
        } catch (Exception ex) {
            String msg = "Failed to find files by project id: " + projectId;
            log.error(msg, ex);
            throw new FileAccessException(msg, ex);
        }
    }

    public List<ProjectFile> findAllByAssayId(Long assayId) throws FileAccessException {
        Assert.notNull(assayId, "Assay id cannot be empty");
        try {
            return fileRepository.findAllByAssayId(assayId);
        } catch (Exception ex) {
            String msg = "Failed to find files by assay id: " + assayId;
            log.error(msg, ex);
            throw new FileAccessException(msg, ex);
        }
    }

    @Transactional(readOnly = false)
    public ProjectFile save(ProjectFile projectFile) {
        return fileRepository.save(projectFile);
    }

    @Transactional(readOnly = false)
    //to support saving multiple files in one transaction
    public List<ProjectFile> saveMultiple(List<ProjectFile> files) {
        List<ProjectFile> savedFiles = new ArrayList<>(files.size());
        for(ProjectFile file: files) {
            savedFiles.add(fileRepository.save(file));
        }
        return savedFiles;
    }

    @Transactional(readOnly = false)
    public void delete(ProjectFile file) {
        fileRepository.delete(file);
    }
}
