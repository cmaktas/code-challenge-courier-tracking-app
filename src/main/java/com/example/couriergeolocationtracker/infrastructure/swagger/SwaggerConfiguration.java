package com.example.couriergeolocationtracker.infrastructure.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.IntegerSchema;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

/**
 * Configuration class for setting up Swagger documentation.
 */
@Configuration
public class SwaggerConfiguration {

    @Value("${courier-app.max-number-of-courier-entities}")
    private int maxCourierId;

    /**
     * Provides metadata for the API documentation.
     *
     * @return an {@link OpenAPI} instance with metadata like title, version, and description.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Courier Tracking System API")
                .version("1.0")
                .description("API documentation for the Courier Tracking System")
            );
    }

    /**
     * Customizes Swagger documentation for the "courierId" parameter.
     *
     * @return an {@link OpenApiCustomizer} that applies constraints and dynamic descriptions.
     */
    @Bean
    public OpenApiCustomizer customizeMaxCourierId() {
        return openApi -> openApi.getPaths()
            .values()
            .stream()
            .flatMap(pathItem -> pathItem.readOperations().stream())
            .flatMap(operation -> operation.getParameters().stream())
            .filter(parameter -> "courierId".equals(parameter.getName()))
            .forEach(parameter -> {
                parameter.description("ID of the courier whose distance needs to be retrieved. Must be between 1 and the value of max-courier-id in application.yml. Current max-courier-id: " + maxCourierId + ".");
                parameter.setSchema(new IntegerSchema()
                    .example(1)
                    .minimum(BigDecimal.ONE)
                    .maximum(BigDecimal.valueOf(maxCourierId)));
            });
    }
}
