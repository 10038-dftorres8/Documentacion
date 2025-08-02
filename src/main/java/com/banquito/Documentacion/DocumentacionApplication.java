package com.banquito.Documentacion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class DocumentacionApplication {

	public static void main(String[] args) {
		SpringApplication.run(DocumentacionApplication.class, args);
	}

}
