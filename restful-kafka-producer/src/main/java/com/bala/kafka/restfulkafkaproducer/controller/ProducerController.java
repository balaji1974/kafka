package com.bala.kafka.restfulkafkaproducer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.bala.kafka.restfulkafkaproducer.model.Student;
import com.bala.kafka.restfulkafkaproducer.service.ProducerService;
import com.google.gson.Gson;

@RestController
public class ProducerController {
	
	@Autowired
	ProducerService producerService;
	
	@Autowired
	Gson gson;
	
	@Value("${student.topic.name}")
	private String TOPIC_NAME;
	
	@PostMapping("/kafka/student")
	public void sendMessageToKafka(@RequestBody Student student) {
		producerService.sendMessage(TOPIC_NAME, student.getId().toString(), gson.toJson(student));
	}
}
