package com.example.client;

import brave.Span;
import brave.Tracer;
import brave.Tracing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.logging.Logger;
import static java.lang.Thread.sleep;

@EnableAutoConfiguration
@RestController
@CrossOrigin // So that ja vascript can be hosted elsewhere
public class ClientApplication {


	//
	// it's very importtant
	//
	@Autowired
	private RestTemplate restTemplate;

	String backendBaseUrl = System.getProperty("spring.example.backendBaseUrl", "http://localhost:9000");

	@Bean
	RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Autowired
	Tracer tracer;

	private final static Logger LOG = Logger.getGlobal();

	@RequestMapping("/hi")
	public String hi() {
		/**
		// Create tracing with name as "in_process_local_job_1"
		*/
		Tracing tracing = TracingFactory.create("in_process_local_job_1");

		/**
		// This is the span used when /hi was called
		*/
		Span currentSpan = tracer.currentSpan();


		/**
		// Assign new span to childSpan with parentSpan
		// This childSpan has a tag(key:local_action_tag, value: local_action_tag_value).
		*/
		Span childSpan = tracing.tracer().newChild(currentSpan.context());
		childSpan.name("local_action_1");
		childSpan.tag("local_action_tag", "local_action_tag_value");
		childSpan.start();
		try {
			doJob(100);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		childSpan.finish();

		/**
		// Request to http://localhost:9000/hello
		// restTemplate has a interceptor of "brave" as library because restTemplate get injected by using @Autowired
		*/
		String body = restTemplate.getForObject(backendBaseUrl + "/hello", String.class);


		/**
		// Create tracing with name as "in_process_local_job_2"
		*/
		tracing = TracingFactory.create("in_process_local_job_2");


		/**
		// Assign new span to childSpan with parentSpan
		*/
		childSpan = tracing.tracer().newChild(currentSpan.context());
		childSpan.name("local_action_2");
		childSpan.tag("clnt/finagle.version", "6.36.0");
		childSpan.start();
		try {
			doJob(100);

		}catch (Exception e) {
			e.printStackTrace();
		}
		childSpan.finish();

		return body;
	}

	private void doJob(long millis) throws InterruptedException {
		sleep(millis);
	}

	public static void main(String[] args) {
		SpringApplication.run(ClientApplication.class,
				"--spring.application.name=frontend1",
				"--server.port=8082"
		);
	}
}
