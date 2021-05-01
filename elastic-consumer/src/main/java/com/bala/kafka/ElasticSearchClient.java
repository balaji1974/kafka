package com.bala.kafka;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

import org.apache.http.HttpHost;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonParser;

public class ElasticSearchClient {
	
	private static Logger logger=LoggerFactory.getLogger(ElasticSearchClient.class.getName());
	
	private static final String ELASTIC_SERVER_NAME1="localhost";
	private static final Integer ELASTIC_SERVER_PORT1=9200;
	private static final String ELASTIC_SERVER_PROTOCOL1="http";
	
	private static final String INDEX_NAME="twitter";
	private static final String INDEX_TYPE="tweets";

	private static final String BOOT_STRAP_SERVER="localhost:9092";
	private static final String GROUP_ID="kafka-elasticsearch";
	private static final String TOPIC_NAME="twitter_tweets";
	
	private static JsonParser jsonParser=new JsonParser();
	
	public static void main(String str[]) throws IOException {
		//First create a elastic search client that will be used to insert data into elasticsearch
		RestHighLevelClient client=createClient();
		
		// Create a Kafka consumer that will listen to the topic 
		KafkaConsumer<String, String> kafkaConsumer=createConsumer();
		
		while(true) {
			ConsumerRecords<String, String> consumerRecords = kafkaConsumer.poll(Duration.ofMillis(100));
			
			int recordCount=consumerRecords.count();
			
			logger.info("Received Records is "+recordCount);
			
			BulkRequest bulkRequest=new BulkRequest();
			
			for(ConsumerRecord<String, String> consumerRecord: consumerRecords) {
				
				// Two strategies to generate ID to make our Kafka consumer 
				// Kafka generic ID
				//String id= consumerRecord.topic() + "_" + consumerRecord.partition() + "_" + consumerRecord.offset();
				try {
					// Twitter feed specific ID
					String id=extractIDFromTweet(consumerRecord.value());
					
					IndexRequest indexRequest=new IndexRequest(
							INDEX_NAME,
							INDEX_TYPE, 
							id // This is to make the consumer idempotent - unique id  is inserted 
							).source(consumerRecord.value(),XContentType.JSON);
					
					// Not need the below line as it has been replaced by Bulk Request
					//IndexResponse indexResponse=client.index(indexRequest, RequestOptions.DEFAULT);
					
					bulkRequest.add(indexRequest);
					
					// Not needed as we are getting id already
					//String id=indexResponse.getId();
					
					//logger.info(indexResponse.getId());
				}
				catch(NullPointerException e) {
					logger.warn("Skipping bad data : "+consumerRecord.value());
				}
				
			}
			if(recordCount>0) {
				BulkResponse bulkResponse=client.bulk(bulkRequest, RequestOptions.DEFAULT);
				
				logger.info("Committing the offsets");
				kafkaConsumer.commitSync(); 
				logger.info("Offset has been committed");
				try {
					Thread.sleep(1000); // Just to wait and watch -- must be removed later
				}
				catch (InterruptedException e){
					e.printStackTrace();
				}
			}
		}
		
		
		//client.close();
		
	}
	
	
	private static String extractIDFromTweet(String tweetJson) {
	// Gson library 
	return jsonParser.parse(tweetJson)
			.getAsJsonObject()
			.get("id_str")
			.getAsString();
		
	}

	

	private static RestHighLevelClient createClient() {
		RestHighLevelClient client = new RestHighLevelClient(
		        RestClient.builder(
		                new HttpHost(ELASTIC_SERVER_NAME1, ELASTIC_SERVER_PORT1, ELASTIC_SERVER_PROTOCOL1))); 
		return client;
		
	}
	
	private static KafkaConsumer<String, String> createConsumer() {
		Properties properties=new Properties();
		properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOT_STRAP_SERVER);
		properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
		properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); //"latest/none" is also possible smallest/largest 
		properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false"); // Disable auto commit of offsets 
		properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "100"); // We only can get 20 records max before which we need to commit 
		
		KafkaConsumer<String, String> kafkaConsumer=new KafkaConsumer<String,String>(properties);
		kafkaConsumer.subscribe(Arrays.asList(TOPIC_NAME));
		return kafkaConsumer;
	}
}
