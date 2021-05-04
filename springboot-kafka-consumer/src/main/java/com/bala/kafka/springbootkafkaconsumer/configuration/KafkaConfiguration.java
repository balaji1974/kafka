package com.bala.kafka.springbootkafkaconsumer.configuration;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import com.google.gson.Gson;

@Configuration
public class KafkaConfiguration {
	private static final String BOOT_STRAP_SERVER="localhost:9092";
	private static final String CONSUMER_GROUP_ID="student_topic_group1";
	
	@Bean
	public ConsumerFactory<String,String> consumerFactory() {
		return new DefaultKafkaConsumerFactory<String, String>(getServerConfiguration(), 
				new StringDeserializer(), new StringDeserializer());
	}
	
	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, String> concurrentKafkaListenerContainerFactory
						=new ConcurrentKafkaListenerContainerFactory<>();
		concurrentKafkaListenerContainerFactory.setConsumerFactory(consumerFactory());
		return concurrentKafkaListenerContainerFactory;
	}
	
	@Bean
	Gson gson() {
		return new Gson();
	}
	

	private Map<String,Object> getServerConfiguration() {
		Map<String,Object> serverConfiguration=new HashMap<>();
		serverConfiguration.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOT_STRAP_SERVER);
		serverConfiguration.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		serverConfiguration.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		serverConfiguration.put(ConsumerConfig.GROUP_ID_CONFIG, CONSUMER_GROUP_ID);
		return serverConfiguration;
	}
}
