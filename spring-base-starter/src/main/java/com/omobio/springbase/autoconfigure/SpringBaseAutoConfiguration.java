package com.omobio.springbase.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Activates the spring-base infrastructure (security, RBAC, seeders, guards)
 * for applications whose base package is NOT {@code com.omobio.springbase}.
 * Apps in the same base package pick everything up via component scanning.
 */
@AutoConfiguration
@ComponentScan(basePackages = "com.omobio.springbase")
@EntityScan(basePackages = "com.omobio.springbase.model")
@EnableJpaRepositories(basePackages = "com.omobio.springbase.repository")
public class SpringBaseAutoConfiguration {
}
