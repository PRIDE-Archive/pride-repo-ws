package uk.ac.ebi.pride.archive.repo.ws.exception;

import org.springframework.core.NestedRuntimeException;

/**
 * {@code AccessionProviderException} is specific to accession provider web service
 *
 * @author Rui Wang
 * @version $Id$
 */
public class AccessionUpdateException extends NestedRuntimeException {

    public AccessionUpdateException(String message) {
        super(message);
    }

    public AccessionUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}
