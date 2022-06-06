package vn.edu.fpt.capstone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;


@SpringBootApplication
@ComponentScan(basePackages = "vn.edu.fpt.capstone")
@EnableAutoConfiguration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableCaching
@EnableAsync
public class CapstoneApplication {


	public static void main(String[] args) {
		SpringApplication.run(CapstoneApplication.class, args);

	}

}
