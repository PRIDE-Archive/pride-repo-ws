package uk.ac.ebi.pride.archive.repo.ws.exception;

import org.springframework.core.NestedRuntimeException;

public class UserExistsException extends NestedRuntimeException {

  private String email;

  public UserExistsException(String msg) {
    super(msg);
  }

  public UserExistsException(String msg, Throwable cause) {
    super(msg, cause);
  }

  public UserExistsException(String msg, String email) {
    super(msg);
    this.email = email;
  }

  public UserExistsException(String msg, Throwable cause, String email) {
    super(msg, cause);
    this.email = email;
  }

  public String getEmail() {
    return email;
  }
}
