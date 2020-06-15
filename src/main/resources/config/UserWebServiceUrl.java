package uk.ac.ebi.pride.archive.repo.ws.config;

import org.springframework.stereotype.Component;

/**
 * @author Rui Wang
 * @version $Id$
 */
@Component
public class UserWebServiceUrl {
  private String signUpUrl;
  private String updateUrl;
  private String passwordResetUrl;
  private String aapRegisterUrl;

  public UserWebServiceUrl() {}

  public UserWebServiceUrl(String signUpUrl, String updateUrl, String passwordResetUrl, String aapRegisterUrl) {
    this.signUpUrl = signUpUrl;
    this.updateUrl = updateUrl;
    this.passwordResetUrl = passwordResetUrl;
    this.aapRegisterUrl = aapRegisterUrl;
  }

  public String getSignUpUrl() {
    return signUpUrl;
  }

  public void setSignUpUrl(String signUpUrl) {
    this.signUpUrl = signUpUrl;
  }

  public String getUpdateUrl() {
    return updateUrl;
  }

  public void setUpdateUrl(String updateUrl) {
    this.updateUrl = updateUrl;
  }

  public String getPasswordResetUrl() {
    return passwordResetUrl;
  }

  public void setPasswordResetUrl(String passwordResetUrl) {
    this.passwordResetUrl = passwordResetUrl;
  }

  public String getAapRegisterUrl() {
    return aapRegisterUrl;
  }

  public void setAapRegisterUrl(String aapRegisterUrl) {
    this.aapRegisterUrl = aapRegisterUrl;
  }
}
