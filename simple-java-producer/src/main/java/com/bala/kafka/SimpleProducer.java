package com.bala.kafka;

import java.util.Properties;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleProducer {
	
	private static final String BOOT_STRAP_SERVER="localhost:9092";
	private static Logger logger=LoggerFactory.getLogger(SimpleProducer.class);
	private static final String TOPIC_NAME="my_topic";
	
	public static void main(String str[]) {

		Properties properties=new Properties();
		properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOT_STRAP_SERVER);
		properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		
		KafkaProducer<String, String> kafkaProducer=new KafkaProducer<String,String>(properties);
		
		
		for(int xi=0;xi<10;xi++) {
			ProducerRecord<String, String> producerRecord=new ProducerRecord<String, String>(TOPIC_NAME,"balaji "+(xi+1));
			kafkaProducer.send(producerRecord, new Callback() {
				
				public void onCompletion(RecordMetadata metadata, Exception exception) {
					if(exception==null) {
						logger.info("Topic : "+metadata.topic());
						logger.info("Partition : "+metadata.partition());
						logger.info("Offset : "+metadata.offset());
						logger.info("Timestamp : "+metadata.timestamp());
					}
					else {
						logger.info(exception.getStackTrace().toString());
					}
					
				}
			});
			kafkaProducer.flush();
		}
		
		kafkaProducer.close();
	}
}
