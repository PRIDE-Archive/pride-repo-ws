package uk.ac.ebi.pride.archive.repo.ws.exception;

import org.springframework.core.NestedRuntimeException;

/**
 * {@code UserModificationException} is thrown when there is an error during modifying user details.
 * This include sign up, reset password and update.
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
public class UserModificationException extends NestedRuntimeException {

  private String email;

  public UserModificationException(String msg) {
    super(msg);
  }

  public UserModificationException(String msg, Throwable cause) {
    super(msg, cause);
  }

  public UserModificationException(String msg, String email) {
    super(msg);
    this.email = email;
  }

  public UserModificationException(String msg, Throwable cause, String email) {
    super(msg, cause);
    this.email = email;
  }

  public String getEmail() {
    return email;
  }
}
