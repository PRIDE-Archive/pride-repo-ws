package uk.ac.ebi.pride.archive.repo.ws.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ebi.pride.archive.repo.models.stats.StatisticsSummary;
import uk.ac.ebi.pride.archive.repo.ws.service.StatisticsService;

@RestController
@Validated
@RequestMapping("/stats")
@Slf4j
@Tag(name="Statistics")
public class StatsController {

    private final StatisticsService statisticsService;

    @Autowired
    public StatsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/latest-statistics")
    public StatisticsSummary getLatestStatistics() {
        return statisticsService.getLatestStatistics();
    }
}
