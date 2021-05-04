package com.bala.kafka.springbootstudentconsumer.configuration;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import com.bala.kafka.springbootstudentconsumer.model.Student;



@Configuration
public class KafkaConfiguration {
	private static final String BOOT_STRAP_SERVER="localhost:9092";
	private static final String CONSUMER_GROUP_ID="student_topic_group1";
	
	@Bean
	public ConsumerFactory<String,Student> studentConsumerFactory() {
		return new DefaultKafkaConsumerFactory<String, Student>(getServerConfiguration(), 
				new StringDeserializer(), new JsonDeserializer<>(Student.class, false));  // false is important or else it will serialize using producer object
	}
	
	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, Student> kafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, Student> concurrentKafkaListenerContainerFactory
						=new ConcurrentKafkaListenerContainerFactory<>();
		concurrentKafkaListenerContainerFactory.setConsumerFactory(studentConsumerFactory());
		return concurrentKafkaListenerContainerFactory;
	}
	

	private Map<String,Object> getServerConfiguration() {
		Map<String,Object> serverConfiguration=new HashMap<>();
		serverConfiguration.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOT_STRAP_SERVER);
		serverConfiguration.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		serverConfiguration.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
		serverConfiguration.put(ConsumerConfig.GROUP_ID_CONFIG, CONSUMER_GROUP_ID);
		return serverConfiguration;
	}
}
