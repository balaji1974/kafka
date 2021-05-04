package com.bala.kafka.springbootkafkaconsumer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.bala.kafka.springbootkafkaconsumer.model.Student;
import com.google.gson.Gson;



@RestController
public class KafkaController {
	
	private static final String TOPIC_NAME="student_topic";
	
	@Autowired
	Gson gson;
	
	
	@KafkaListener(topics=TOPIC_NAME)
	public void getStudentsFromKafka(@RequestBody String student) {
		System.out.println(gson.fromJson(student, Student.class));
	}
	
}
