package com.example.image_process_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;



@SpringBootApplication
@EnableMongoRepositories
public class ImageProcessApiApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(ImageProcessApiApplication.class, args);
	}

}
