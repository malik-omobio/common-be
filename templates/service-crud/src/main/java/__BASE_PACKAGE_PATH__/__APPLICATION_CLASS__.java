package __BASE_PACKAGE__;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "__BASE_PACKAGE__.repository")
@EntityScan(basePackages = "__BASE_PACKAGE__.model")
public class __APPLICATION_CLASS__ {

    public static void main(String[] args) {
        SpringApplication.run(__APPLICATION_CLASS__.class, args);
    }
}
