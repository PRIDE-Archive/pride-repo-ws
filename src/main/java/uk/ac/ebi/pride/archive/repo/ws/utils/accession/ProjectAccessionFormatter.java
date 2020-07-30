package uk.ac.ebi.pride.archive.repo.ws.utils.accession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Format project accession
 *
 * @author Rui Wang
 * @version $Id$
 */
public class ProjectAccessionFormatter implements AccessionFormatter {
    private static final Logger logger = LoggerFactory.getLogger(ProjectAccessionFormatter.class);

    public final static String PRODUCTION_PROJECT_ACCESSION_PREFIX = "PRD";
    public static final String TEST_PROJECT_ACCESSION_PREFIX = "PRDT";

    private final static int PROJECT_ACCESSION_NUMBER_LENGTH = 6;

    private String projectAccessionPrefix;

    public ProjectAccessionFormatter(String projectAccessionPrefix) {
        this.projectAccessionPrefix = projectAccessionPrefix;
    }

    @Override
    public String formatAccession(long accessionNumber) {
        return projectAccessionPrefix + formatProjectNumber(accessionNumber);
    }

    private String formatProjectNumber(long projectNumber) {
        String pn = projectNumber + "";
        int currLength = pn.length();

        if (pn.length() <= PROJECT_ACCESSION_NUMBER_LENGTH) {
            for (int i = PROJECT_ACCESSION_NUMBER_LENGTH; i > currLength; i--) {
                pn = "0" + pn;
            }
        } else {
            String msg = "Project number: " + projectNumber + "exceed the limit of : " + PROJECT_ACCESSION_NUMBER_LENGTH;
            logger.error(msg);
            throw new IllegalStateException(msg);
        }

        return pn;
    }
}
