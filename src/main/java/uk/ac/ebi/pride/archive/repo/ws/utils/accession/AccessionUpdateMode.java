package uk.ac.ebi.pride.archive.repo.ws.utils.accession;

/**
 * Update mode for accession service, either production or test
 *
 * @author Rui Wang
 * @version $Id$
 */
public enum AccessionUpdateMode {
    PRODUCTION, TEST;

    public static AccessionUpdateMode getMode(String mode) {

        if (PRODUCTION.toString().equalsIgnoreCase(mode)) {
            return PRODUCTION;
        } else if (AccessionUpdateMode.TEST.toString().equalsIgnoreCase(mode)) {
            return TEST;
        } else {
            return null;
        }
    }
}
