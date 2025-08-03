package com.Agri.AgriBack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableCaching(proxyTargetClass = true)
@EnableAsync(proxyTargetClass = true)
public class AgriBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(AgriBackApplication.class, args);
	}

}
