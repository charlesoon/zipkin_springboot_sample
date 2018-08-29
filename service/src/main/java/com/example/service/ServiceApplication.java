package com.example.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@EnableAutoConfiguration
@RestController
public class ServiceApplication {

	@RequestMapping("/hello")
	public String hi() {
		return "Hello";
	}

	public static void main(String[] args) {
		SpringApplication.run(ServiceApplication.class,
				"--spring.application.name=backend1",
				"--server.port=9000"
		);
	}
}
