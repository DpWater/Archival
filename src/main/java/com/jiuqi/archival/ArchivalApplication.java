package com.jiuqi.archival;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

@SpringBootApplication
@EnableSwagger2WebMvc
public class ArchivalApplication {

    public static void main(String[] args) {
        SpringApplication.run(ArchivalApplication.class, args);
    }

}
