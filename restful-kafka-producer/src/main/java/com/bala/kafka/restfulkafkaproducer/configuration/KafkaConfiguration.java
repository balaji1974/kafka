package com.bala.kafka.restfulkafkaproducer.configuration;

import org.springframework.context.annotation.Bean;

import com.google.gson.Gson;

public class KafkaConfiguration {
	@Bean
	public Gson gson() {
		return new Gson();
	}
}
