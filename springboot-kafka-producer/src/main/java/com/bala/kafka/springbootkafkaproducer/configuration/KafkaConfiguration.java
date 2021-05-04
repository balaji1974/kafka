package com.bala.kafka.springbootkafkaproducer.configuration;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import com.google.gson.Gson;

@Configuration
public class KafkaConfiguration {
	private static final String BOOT_STRAP_SERVER="localhost:9092";
	
	@Bean
	public ProducerFactory<String,String> producerFactory() {
		return new DefaultKafkaProducerFactory<String, String>(getServerConfiguration());
	}
	
	@Bean
	public KafkaTemplate<String, String> kafkaTemplate() {
		return new KafkaTemplate<String, String>(producerFactory());
	}
	
	@Bean
	public Gson gson() {
		return new Gson();
	}
	
	
	private Map<String,Object> getServerConfiguration() {
		Map<String,Object> serverConfiguration=new HashMap<>();
		serverConfiguration.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOT_STRAP_SERVER);
		serverConfiguration.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		serverConfiguration.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		return serverConfiguration;
	}
	
	
}
