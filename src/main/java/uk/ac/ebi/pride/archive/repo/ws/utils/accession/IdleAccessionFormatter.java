package uk.ac.ebi.pride.archive.repo.ws.utils.accession;

/**
 * @author Rui Wang
 * @version $Id$
 */
public class IdleAccessionFormatter implements AccessionFormatter {

    @Override
    public String formatAccession(long accessionNumber) {
        return accessionNumber + "";
    }
}
