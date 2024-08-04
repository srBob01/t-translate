package ru.arsentiev.translator.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Nikita",
                        email = "necit717@gmail.com",
                        url = "https://vk.com/qwartu"
                ),
                description = "OpenApi documentation for Spring Security",
                title = "OpenApi specification - Nikita",
                version = "1.0",
                license = @License(
                        name = "Licence name",
                        url = "https://some-url.com"
                ),
                termsOfService = "Terms of service"
        ),
        servers = {
                @Server(
                        description = "Local",
                        url = "http://localhost:8080"
                )
        }
)
public class OpenApiConfig {
}

