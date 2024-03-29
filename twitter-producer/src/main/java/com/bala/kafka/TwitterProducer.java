package com.bala.kafka;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

public class TwitterProducer {
	
	private static final String CONSUMER_KEY="XXXXX"; 
	private static final String CONSUMER_SECRET="XXXXX";
	private static final String TOKEN="XXXXX";
	private static final String SECRET="XXXXX";

	
	private static final String BOOT_STRAP_SERVER="localhost:9092";
	private static final String TOPIC_NAME="twitter_tweets";

	List<String> termsToFollow = Lists.newArrayList("kafka","bitcoin","covid"); // 
	
	private static final Long TWITTER_POLLING_INTERVAL=5L;
	
	Logger logger=LoggerFactory.getLogger(TwitterProducer.class.getName());
		
	
	public static void main(String strp[])  {
		new TwitterProducer().run(); 		
	}
	
	private void run()  {
		
		logger.info("Startup");
		
		/** Set up your blocking queues: Be sure to size these properly based on expected TPS of your stream */
		BlockingQueue<String> msgQueue = new LinkedBlockingQueue<String>(1000);
		
		// Create the twitter client
		Client twitterClient=createTwitterClient(msgQueue);
		
		// Create a Kafka Producer 
		KafkaProducer<String, String> kafkaProducer=getKafKaProducer();
		
		
		// Connect to the client 
		twitterClient.connect();
		
		
		// Add a shutdown hook
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			logger.info("Stopping Twitter Client");
			twitterClient.stop();
			logger.info("Twitter Client has been stopped");
			logger.info("Closing Kafka Producer");
			kafkaProducer.close();
			logger.info("Kafka Producer has been closed");
		}));
				
				
		while (!twitterClient.isDone()) {
			String msg =null;
			try {
				msg = msgQueue.poll(TWITTER_POLLING_INTERVAL, TimeUnit.SECONDS);
			} 
			catch (InterruptedException e) {
				e.printStackTrace();
				twitterClient.stop();
			}
			if(msg!=null) {
				logger.info(msg);
				kafkaProducer.send(new ProducerRecord<String,  String>(TOPIC_NAME, null, msg), new Callback() {
					public void onCompletion(RecordMetadata metadata, Exception exception) {
						if(exception!=null)
							logger.error("Their is an exception : "+ exception);
					}
				});
			}
		}
		
		logger.info("Ending the program");
		
	}
	
	private KafkaProducer<String, String> getKafKaProducer() {
		Properties properties=new Properties();
		properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOT_STRAP_SERVER);
		properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		
		//Safe Producer Configuration 
		properties.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");  // Default to false
		properties.setProperty(ProducerConfig.ACKS_CONFIG, "all"); // Defaults to 1 
		properties.setProperty(ProducerConfig.RETRIES_CONFIG, Integer.toString(Integer.MAX_VALUE)); // Defaults to this setting
		properties.setProperty(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION,"5"); // Defaults to 5 
		
		//High throughput producer configuration (at the expense of some latency and higher CPU usage)
		properties.setProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy"); // Good compression engine developed by google for text compression like JSON strings
		properties.setProperty(ProducerConfig.LINGER_MS_CONFIG, "20"); // Will wait for 20sec before all the consolidating all messages and compressing and sending it over
		properties.setProperty(ProducerConfig.BATCH_SIZE_CONFIG,Integer.toString(32*1024)); // 32KB batch size for messages 
		
		// These are advanced settings and never play with it unless you have a situation when the producer produces more than the broken can take 
		//properties.setProperty(ProducerConfig.BUFFER_MEMORY_CONFIG,Integer.toString(32*1024*1024)); // This is the default setting 32MB and can be increased if needed
		//properties.setProperty(ProducerConfig.MAX_BLOCK_MS_CONFIG, Integer.toString(60000)); // This is the default which is 60sec until the receiving is blocked if buffer is full. This can also be configured 
		
		return new KafkaProducer<String,String>(properties);
	}

	public  Client createTwitterClient(BlockingQueue<String> msgQueue) {
		
		//Declaring the connection information:
		
		
		//BlockingQueue<Event> eventQueue = new LinkedBlockingQueue<Event>(1000);
		
		/** Declare the host you want to connect to, the endpoint, and authentication (basic auth or oauth) */
		Hosts twitterHosts = new HttpHosts(Constants.STREAM_HOST);
		StatusesFilterEndpoint twitterEndpoint = new StatusesFilterEndpoint();
		// Optional: set up some followings and track terms
		
		// Followings is not needed as we are using terms
		//List<Long> followings = Lists.newArrayList(1234L, 566788L);
		//hosebirdEndpoint.followings(followings);
		
		 
		twitterEndpoint.trackTerms(termsToFollow);

		// These secrets should be read from a config file
		Authentication twitterAuth = new OAuth1(CONSUMER_KEY, CONSUMER_SECRET, TOKEN, SECRET);
		
		//Creating a client:

		ClientBuilder builder = new ClientBuilder()
		  .name("BalajiTwitter-Client-01")                              // optional: mainly for the logs
		  .hosts(twitterHosts)
		  .authentication(twitterAuth)
		  .endpoint(twitterEndpoint)
		  .processor(new StringDelimitedProcessor(msgQueue))
		  //.eventMessageQueue(eventQueue)                          // optional: use this if you want to process client events
		  ;
		  
		Client twitterClient = builder.build();
		// Attempts to establish a connection.
		return twitterClient;

	}

}
