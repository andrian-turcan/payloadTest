package com.andrianturcan.dipatcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@SpringBootApplication
public class DipatcherServiceApplication {
	@Bean
	@LoadBalanced
	public RestTemplate shipmentRestTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory("http://SHIPMENT-SERVICE"));
		return restTemplate;
	}

	@Bean
	@LoadBalanced
	public RestTemplate vehicleRestTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory("http://VEHICLE-SERVICE"));
		return restTemplate;
	}

	@Bean
	@LoadBalanced
	public RestTemplate tariffRestTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory("http://TARIFF-SERVICE"));
		return restTemplate;
	}


	public static void main(String[] args) {
		SpringApplication.run(DipatcherServiceApplication.class, args);
	}

}
