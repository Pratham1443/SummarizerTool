package com.example.javaspringbootservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class JavaSpringbootServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(JavaSpringbootServiceApplication.class, args);
	}

}
