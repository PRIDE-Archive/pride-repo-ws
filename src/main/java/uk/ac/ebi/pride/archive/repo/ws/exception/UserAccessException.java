package uk.ac.ebi.pride.archive.repo.ws.exception;

import org.springframework.core.NestedRuntimeException;

/**
 * {@code UserAccessException} is thrown when there is an error during accessing user details.
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
public class UserAccessException extends NestedRuntimeException {

  private String email;

  public UserAccessException(String msg) {
    super(msg);
  }

  public UserAccessException(String msg, Throwable cause) {
    super(msg, cause);
  }

  public UserAccessException(String msg, String email) {
    super(msg);
    this.email = email;
  }

  public UserAccessException(String msg, Throwable cause, String email) {
    super(msg, cause);
    this.email = email;
  }

  public String getEmail() {
    return email;
  }
}
