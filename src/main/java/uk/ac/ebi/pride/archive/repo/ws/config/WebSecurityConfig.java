package uk.ac.ebi.pride.archive.repo.ws.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import javax.servlet.http.HttpServletRequest;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${security.header-name}")
    private String headerName;

    @Value("${security.api-key}")
    private String apiKey;

    @Value("${springdoc.api-docs.path}")
    private String apiDocsPath;

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {

        APIKeyAuthFilter apiKeyAuthFilter = new APIKeyAuthFilter(headerName);
        apiKeyAuthFilter.setAuthenticationManager(authentication -> {
            String principal = (String) authentication.getPrincipal();
            if (!apiKey.equals(principal)) {
                throw new BadCredentialsException("The API key is invalid");
            }
            authentication.setAuthenticated(true);
            return authentication;
        });

        httpSecurity.csrf().disable();
        httpSecurity.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        httpSecurity.addFilter(apiKeyAuthFilter);
        // Entry points
        httpSecurity.authorizeRequests()
                // Disallow everything else..
                .anyRequest().authenticated();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        // Allow swagger to be accessed without authentication
        web.ignoring()
                .antMatchers("/actuator/**/**")
                .antMatchers(apiDocsPath + "/**/**")
                .antMatchers( "/swagger-ui/**/**")
                .antMatchers("/"); //this is redirected to swagger-ui page
    }

    private class APIKeyAuthFilter extends AbstractPreAuthenticatedProcessingFilter {

        private String requestHeaderName;

        public APIKeyAuthFilter(String requestHeaderName) {
            this.requestHeaderName = requestHeaderName;
        }

        @Override
        protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
            return request.getHeader(requestHeaderName);
        }

        @Override
        protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
            return "N/A";
        }

    }

}
