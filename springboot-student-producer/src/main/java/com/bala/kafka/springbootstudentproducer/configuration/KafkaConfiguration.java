package com.bala.kafka.springbootstudentproducer.configuration;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.bala.kafka.springbootstudentproducer.model.Student;

@Configuration
public class KafkaConfiguration {
	private static final String BOOT_STRAP_SERVER="localhost:9092";
	//private static final String TOPIC_NAME="my_topic";
	
	
	@Bean
	public ProducerFactory<String,Student> studentProducerFactory() {
		return new DefaultKafkaProducerFactory<String, Student>(getServerConfiguration());
	}
	
	@Bean
	public KafkaTemplate<String, Student> studentKafkaTemplate() {
		return new KafkaTemplate<String, Student>(studentProducerFactory());
	}
	
	
	
	private Map<String,Object> getServerConfiguration() {
		Map<String,Object> serverConfiguration=new HashMap<>();
		serverConfiguration.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOT_STRAP_SERVER);
		serverConfiguration.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		serverConfiguration.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
		return serverConfiguration;
	}
}
