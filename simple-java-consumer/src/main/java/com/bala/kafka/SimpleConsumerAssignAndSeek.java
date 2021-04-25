package com.bala.kafka;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Assign & Seek is used to replay data or fetch a specific message 
public class SimpleConsumerAssignAndSeek {
	
	private static Logger logger=LoggerFactory.getLogger(SimpleConsumerAssignAndSeek.class);
	private static final String BOOT_STRAP_SERVER="localhost:9092";
	//private static final String GROUP_ID="my_topic_group";
	private static final String TOPIC_NAME="my_topic";
	
	
	public static void main(String str[]) {
		System.out.println("Hello World");
		
		Properties properties=new Properties();
		properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOT_STRAP_SERVER);
		properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		//properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
		properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); //"latest/none" is also possible smallest/largest 
		
		KafkaConsumer<String, String> kafkaConsumer=new KafkaConsumer<String,String>(properties);
		//kafkaConsumer.subscribe(Arrays.asList(TOPIC_NAME));
		
		int partitionToReadFrom=0;
		long offsetToReadFrom=8L;
		
		// Assign 
		TopicPartition topicPartition=new TopicPartition(TOPIC_NAME, partitionToReadFrom); // To read from a partition of a topic 
		kafkaConsumer.assign(Arrays.asList(topicPartition));
		
		// Seek
		kafkaConsumer.seek(topicPartition,offsetToReadFrom);
		
		boolean keepOnReading=true;
		int totalNoOfMessagesToRead=5;
		int noOfMessageReadSoFar=0;
		
		while(keepOnReading) {
			ConsumerRecords<String, String> consumerRecords = kafkaConsumer.poll(Duration.ofMillis(100));
			for(ConsumerRecord<String, String> consumerRecord: consumerRecords) {
				noOfMessageReadSoFar++;
				logger.info("Value : "+consumerRecord.value());
				logger.info("Partition : "+consumerRecord.partition());
				logger.info("Offset : "+consumerRecord.offset());
				if(noOfMessageReadSoFar>=totalNoOfMessagesToRead) {
					keepOnReading=false;
					break;
				}
			}
			logger.info("Exiting the application");
		}
 		
		
	}
}
