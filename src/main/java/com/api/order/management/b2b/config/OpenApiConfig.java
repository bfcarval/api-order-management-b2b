package com.api.order.management.b2b.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Order Management B2B")
                        .description("Microsserviço de alta performance para recebimento, " +
                                "processamento de pedidos e controle transacional de limite de crédito.")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Suporte Técnico B2B")
                                .email("suporte@b2b-orders.com")
                                .url("https://b2b-orders.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://springdoc.org")))
                .externalDocs(new ExternalDocumentation()
                        .description("Repositório do Projeto e Documentação Técnica")
                        .url("https://github.com/bfcarval/api-order-management-b2b"))

                .servers(List.of(
                        new Server().url("http://localhost:8000").description("Ambiente Local (Desenvolvimento)")
                ));
    }
}
