package uk.ac.ebi.pride.archive.repo.ws.exception;

import org.springframework.core.NestedRuntimeException;

/**
 * {@code FileAccessException} is thrown when there is an error during accessing files.
 *
 * <p>As this class is a runtime exception, there is no need for user to catch it or subclasses it
 * if the error is considered to be fatal.
 *
 * <p>For more reference, please refer to Chapter 9 of <a
 * href="http://www.amazon.com/exec/obidos/tg/detail/-/0764543857/">Expert One-On-One J2EE Design
 * and Development</a>.
 *
 * @author Rui Wang
 * @version $Id$
 */
public class FileAccessException extends NestedRuntimeException {

  private String projectAccession = null;
  private String assayAccession = null;

  public FileAccessException(String msg) {
    super(msg);
  }

  public FileAccessException(String msg, Throwable cause) {
    super(msg, cause);
  }

  public FileAccessException(String msg, String projectAccession, String assayAccession) {
    super(msg);
    this.projectAccession = projectAccession;
    this.assayAccession = assayAccession;
  }

  public FileAccessException(
      String msg, Throwable cause, String projectAccession, String assayAccession) {
    super(msg, cause);
    this.projectAccession = projectAccession;
    this.assayAccession = assayAccession;
  }

  public String getProjectAccession() {
    return projectAccession;
  }

  public String getAssayAccession() {
    return assayAccession;
  }
}
