package com.bala.kafka.springbootkafkaproducer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.bala.kafka.springbootkafkaproducer.model.Student;

@RestController
public class KafkaController {
	
	private static final String TOPIC_NAME="student_topic";
	
	@Autowired
	KafkaTemplate<String,Student> studentKafkaTemplate;
	
	@PostMapping("/kafka/student")
	public void postStudentToKafka(@RequestBody Student student) {
		studentKafkaTemplate.send(TOPIC_NAME,student);
	}
	
}
