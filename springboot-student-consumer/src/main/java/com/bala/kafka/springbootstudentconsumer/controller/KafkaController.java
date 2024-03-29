package com.bala.kafka.springbootstudentconsumer.controller;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.bala.kafka.springbootstudentconsumer.model.Student;

@RestController
public class KafkaController {
	
	private static final String TOPIC_NAME="student_topic";
	
	
	@KafkaListener(topics=TOPIC_NAME)
	public void getStudentsFromKafka(@RequestBody Student student) {
		System.out.println(student);
	}
	
}
