package uk.ac.ebi.pride.archive.repo.ws.exception;

import org.springframework.core.NestedRuntimeException;

public class CvParamAccessException extends NestedRuntimeException {

    private String accession = null;

    public CvParamAccessException(String msg) {
        super(msg);
    }

    public CvParamAccessException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public CvParamAccessException(String msg, String accession) {
        super(msg);
        this.accession = accession;
    }

    public CvParamAccessException(
            String msg, Throwable cause, String accession) {
        super(msg, cause);
        this.accession = accession;
    }

    public String getAccession() {
        return accession;
    }
}
