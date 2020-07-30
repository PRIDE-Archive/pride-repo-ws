package uk.ac.ebi.pride.archive.repo.ws.utils.accession;

/**
 * Interface for formatting accession from a number to a string
 *
 * @author Rui Wang
 * @version $Id$
 */
public interface AccessionFormatter {

    String formatAccession(long accessionNumber);
}
