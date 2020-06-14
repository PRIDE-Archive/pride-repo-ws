package uk.ac.ebi.pride.archive.repo.ws.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Value("${security.header-name}")
    private String headerName;

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "PrideRepoWsAuth";
        final String apiTitle = "Pride Repo API";
        final String apiVersion = "1.0";
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName)) //allows to add global security schema and to get rid of writing security to @Operation of each controller method.
                .components(
                        new Components()
                                .addSecuritySchemes(securitySchemeName,
                                        new SecurityScheme()
                                                .name(securitySchemeName)
                                                .type(SecurityScheme.Type.APIKEY)
                                                .in(SecurityScheme.In.HEADER)
                                                .name(headerName)
                                )
                )
                .info(new Info().title(apiTitle).version(apiVersion));
    }
}
