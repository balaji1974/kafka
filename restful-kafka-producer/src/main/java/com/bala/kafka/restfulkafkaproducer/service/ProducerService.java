package com.bala.kafka.restfulkafkaproducer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.bala.kafka.restfulkafkaproducer.model.Student;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class ProducerService {
	
	@Autowired
	KafkaTemplate<String, Student> kafkaTemplate;
	
	public void sendMessage(String topic, String key, Student value) {
		log.info(String.format("The topic is %s, key is %s and value is %s",topic, key, value));
		kafkaTemplate.send(topic, key, value);
	}
}
