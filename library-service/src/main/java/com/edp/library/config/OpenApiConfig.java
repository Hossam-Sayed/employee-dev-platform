package com.edp.library.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "EDP Library API",
                version = "1.0.0",
                description = "API documentation for the EDP Library Management Service. This Service allows users to manage their library materials including learnings, blogs, and wikis, providing the capability of submission, review, and public access to approved content.",
                contact = @Contact(
                        name = "Support Team",
                        email = "support@edp.com"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"
                )
        ),
        servers = {
                @Server(
                        url = "http://localhost:8082",
                        description = "Local Development Server"
                ),
//                @Server(
//                        url = "https://edp-library.com/api",
//                        description = "Production Server"
//                )
        }
)
public class OpenApiConfig {
}