package com.bala.kafka.objectkafkaconsumer.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.RestController;

import com.bala.kafka.objectkafkaconsumer.model.Student;


@RestController
public class KafkaController {
	
	@Value("${student.topic.name}")
	private String TOPIC_NAME;
	
	@KafkaListener(topics="student_topic")
	public void getStudentsFromKafka(Student student) {
		System.out.println("Here :"+student);
	}

}
