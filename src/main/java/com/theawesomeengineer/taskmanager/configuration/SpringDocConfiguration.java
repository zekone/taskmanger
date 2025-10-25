package com.theawesomeengineer.taskmanager.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;

@Configuration
public class SpringDocConfiguration {

    @Bean(name = "org.openapitools.configuration.SpringDocConfiguration.apiInfo")
    OpenAPI apiInfo() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("Task Manager API")
                                .description("A simple task management API for managing tasks with CRUD operations")
                                .contact(
                                        new Contact()
                                                .name("Task Manager API Support")
                                                .email("support@taskmanager.com")
                                )
                                .version("1.0.0")
                )
        ;
    }
}