package uk.ac.ebi.pride.archive.repo.ws.utils.accession;

/**
 * Accession types
 *
 * @author Rui Wang
 * @version $Id$
 */
public enum AccessionType {
    PROJECT(new ProjectAccessionFormatter(ProjectAccessionFormatter.PRODUCTION_PROJECT_ACCESSION_PREFIX), new ProjectAccessionFormatter(ProjectAccessionFormatter.TEST_PROJECT_ACCESSION_PREFIX)),
    ASSAY(new IdleAccessionFormatter(), new IdleAccessionFormatter());

    private AccessionFormatter productionAccessionFormatter;
    private AccessionFormatter testAccessionFormatter;

    private AccessionType(AccessionFormatter productionAccessionFormatter,
                          AccessionFormatter testAccessionFormatter) {
        this.productionAccessionFormatter = productionAccessionFormatter;
        this.testAccessionFormatter = testAccessionFormatter;
    }

    public AccessionFormatter getProductionAccessionFormatter() {
        return productionAccessionFormatter;
    }

    public AccessionFormatter getTestAccessionFormatter() {
        return testAccessionFormatter;
    }

    public static AccessionType getType(String type) {
        if (PROJECT.toString().equalsIgnoreCase(type)) {
            return PROJECT;
        } else if (ASSAY.toString().equalsIgnoreCase(type)) {
            return ASSAY;
        } else {
            return null;
        }
    }
}
