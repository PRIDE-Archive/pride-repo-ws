package uk.ac.ebi.pride.archive.repo.ws.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.pride.archive.repo.models.stats.StatisticsSummary;
import uk.ac.ebi.pride.archive.repo.ws.repository.AssayRepository;
import uk.ac.ebi.pride.archive.repo.ws.repository.ProjectRepository;

/**
 * @author Rui Wang
 * @version $Id$
 *     <p>todo: update to retrieve additional statistics
 */
@Service
@Transactional(readOnly = true)
public class StatisticsService {

  private final AssayRepository assayRepository;
  private final ProjectRepository projectRepository;

  @Autowired
  public StatisticsService(
      AssayRepository assayRepository, ProjectRepository projectRepository) {
    this.assayRepository = assayRepository;
    this.projectRepository = projectRepository;
  }

  public StatisticsSummary getLatestStatistics() {
    StatisticsSummary res = new StatisticsSummary();

    res.setNumAssay(assayRepository.count());
    res.setNumProjects(projectRepository.count());

    return res;
  }
}
