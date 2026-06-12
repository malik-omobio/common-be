package com.demo.hr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.demo.hr.repository")
@EntityScan(basePackages = "com.demo.hr.model")
public class DemoHrServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoHrServiceApplication.class, args);
    }
}
