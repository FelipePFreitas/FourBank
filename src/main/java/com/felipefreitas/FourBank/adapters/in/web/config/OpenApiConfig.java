package com.felipefreitas.FourBank.adapters.in.web.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "FourBank API: Soluções Bancárias de Alta Performance",
                version = "1.0.0",
                description = "API RESTful para gestão de contas bancárias, clientes, autenticação JWT e operações financeiras do FourBank.",
                contact = @Contact(
                        name = "Felipe Freitas",
                        email = "felipefreitas210891@gmail.com",
                        url = "https://www.linkedin.com/in/felipe-freitas-aa8651316/"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0"
                )
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Servidor de Desenvolvimento Local")
        }
)
// Prepara a interface do Swagger UI para aceitar Tokens JWT no botão 'Authorize'
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class OpenApiConfig {
}