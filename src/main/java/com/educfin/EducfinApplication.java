package com.educfin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class EducfinApplication {

    public static void main(String[] args) {
        SpringApplication.run(EducfinApplication.class, args);
    }
}
