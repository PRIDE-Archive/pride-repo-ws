package uk.ac.ebi.pride.archive.repo.ws.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.pride.archive.repo.models.assay.Assay;
import uk.ac.ebi.pride.archive.repo.models.file.ProjectFile;
import uk.ac.ebi.pride.archive.repo.models.param.CvParam;
import uk.ac.ebi.pride.archive.repo.models.project.Project;
import uk.ac.ebi.pride.archive.repo.models.submission.SubmissionDto;
import uk.ac.ebi.pride.archive.repo.ws.param.AssayCvParamFinder;
import uk.ac.ebi.pride.archive.repo.ws.param.ProjectCvParamFinder;
import uk.ac.ebi.pride.archive.repo.ws.repository.AssayRepository;
import uk.ac.ebi.pride.archive.repo.ws.repository.CvParamRepository;
import uk.ac.ebi.pride.archive.repo.ws.repository.FileRepository;
import uk.ac.ebi.pride.archive.repo.ws.repository.ProjectRepository;

import java.util.*;

@Service
@Slf4j
@Transactional(readOnly = true)
public class SubmissionService {

    private final AssayRepository assayRepository;
    private final ProjectRepository projectRepository;
    private final FileRepository fileRepository;
    private final CvParamRepository cvParamRepository;

    @Autowired
    public SubmissionService(AssayRepository assayRepository, ProjectRepository projectRepository,
                             FileRepository fileRepository, CvParamRepository cvParamRepository) {
        this.assayRepository = assayRepository;
        this.projectRepository = projectRepository;
        this.fileRepository = fileRepository;
        this.cvParamRepository = cvParamRepository;
    }

    @Transactional
    public void save(SubmissionDto submissionDto) {

        final Project project = submissionDto.getProject();
        final List<Assay> assays = submissionDto.getAssays();
        final Map<ProjectFile, String> projectFilesMap = submissionDto.getProjectFilesMap();

        Map<String, CvParam> allParams = new HashMap<>();
        try {
            for (CvParam param : cvParamRepository.findAll()) {
                allParams.put(param.getAccession(), param);
            }

            //persist project
            Project projectSaved = persistProject(project, allParams);

            List<Assay> assaysSaved = null;
            //persist assays
            if (!assays.isEmpty()) {
                assaysSaved = persistAssays(projectSaved, assays, allParams);
            }

            // persist project related files
            persistFiles(projectSaved, assaysSaved, projectFilesMap);

        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new IllegalStateException(ex.getMessage(), ex);
        }
    }

    private Project persistProject(final Project project, Map<String, CvParam> allParams) {
        ProjectCvParamFinder projectCvParamFinder = new ProjectCvParamFinder();
        Collection<CvParam> cvParams = projectCvParamFinder.find(project);
        persistCvParams(allParams, cvParams);

        return projectRepository.save(project);
    }

    private List<Assay> persistAssays(final Project project, final List<Assay> assays,
                                      Map<String, CvParam> allParams) {

        List<Assay> assaysSaved = new ArrayList<>(assays.size());

        Long projectId = project.getId();
        for (Assay assay : assays) {
            assay.setProjectId(projectId);

            AssayCvParamFinder assayCvParamFinder = new AssayCvParamFinder();
            Collection<CvParam> cvParams = assayCvParamFinder.find(assay);
            persistCvParams(allParams, cvParams);
            log.info("Saving assay: " + assay.getAccession());
            Assay assaySaved = assayRepository.save(assay);
            assaysSaved.add(assaySaved);
        }
        return assaysSaved;
    }

    private void persistFiles(final Project project,
                              final List<Assay> assays,
                              final Map<ProjectFile, String> projectFilesMap) {

        for (Map.Entry<ProjectFile, String> projectFileEntry : projectFilesMap.entrySet()) {
            String assayAccession = projectFileEntry.getValue();
            ProjectFile projectFile = projectFileEntry.getKey();

            // set project id
            projectFile.setProjectId(project.getId());

            Long assayId = null;
            if (assayAccession != null) {
                assayId = getAssayId(assayAccession, assays);
            }
            projectFile.setAssayId(assayId);
            log.info("Saving project files : " + projectFile.getProjectId() + " assayID: " + projectFile.getAssayId() + " file: " + projectFile.getFileName());
            fileRepository.save(projectFile);
        }
    }

    private Long getAssayId(String assayAccession, List<Assay> assays) {
        for (Assay assay : assays) {
            if (assay.getAccession().equals(assayAccession)) {
                return assay.getId();
            }
        }
        return null;
    }

    public void persistCvParams(Map<String, CvParam> allParams, Collection<CvParam> cvParams) {
        for (CvParam cvParam : cvParams) {
            CvParam persistedCvParam = allParams.get(cvParam.getAccession());
            if (persistedCvParam == null) {
                persistedCvParam = putCvParam(allParams, cvParam);
            }
            cvParam.setId(persistedCvParam.getId());
        }
    }

    /**
     * If a param isn't already loaded from the database, it is new and will be stored. If it already exists,
     * nothing happens.
     */
    public CvParam putCvParam(Map<String, CvParam> allParams, CvParam cvParam) {

        String accession = cvParam.getAccession();
        String name = cvParam.getName();
        String cvLabel = cvParam.getCvLabel();

        if (cvLabel == null || "".equals(cvLabel.trim())) {
            throw new IllegalArgumentException("CV LABEL cannot be null to store cv param");
        }
        if (accession == null || "".equals(accession.trim())) {
            throw new IllegalArgumentException("ACCESSION cannot be null to store cv param");
        }
        if (name == null || "".equals(name.trim())) {
            throw new IllegalArgumentException("NAME cannot be null to store cv param");
        }

        if (!allParams.containsKey(accession)) {
            try {
                CvParam cvParamSaved = cvParamRepository.save(cvParam);
                log.warn("Storing cv param: " + accession);
                allParams.put(accession, cvParamSaved);
                return cvParamSaved;
            } catch (RuntimeException e) {
                log.error("Error saving param: " + e.getMessage(), e);
                throw e;
            }
        }
        return cvParam;
    }
}
