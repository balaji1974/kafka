# Apache Kafka  

## Key Concepts  

### Topics: 
Stream of data (like table in a database)
### Partitions: 
Topics are split into partitions and they are ordered. 
### Offset: 
Each message within a partition gets an incremental id called offset. Data is read by consumers within a partition in the order of offset
### Brokers: 
A kafka cluster consists of multiple brokers (Servers). 
### Bootstrap server: 
Every broker is a bootstrap server which has information for all other brokers. 
### Replication: 
Partitions are replicated automatically across brokers. 
### Leader/ISR: 
In replication we have a leader partition which receives data directly while the ISR (in sync Replica) replicates data from the leader.
### Zookeeper: 
The process of leader and ISR is managed by Zoo keeper automatically. A Zoo keeper manages brokers. Zookeeper by design works only with an odd number of servers. Eg. we cannot have 2 zookeepers. We need to have 1,3,5 etc. with the concept of leader/followers
### Producers: 
Producers write data to topics and can choose to receive or not receive acknowledgement for the writes.   
[acks-0 - producer will not wait for acknowledgement (possible data loss)  
acks-1 - producer will wait for leader to acknowledgement (limited data loss) - This is default   
acks-All - producer will wait for leader and replicas to acknowledge - No data loss]   
### Keys: 
Producers can send Keys with messages. If the key is null then write partition cannot be determined and data is written in round robin. Key is useful for message ordering
### Consumers: 
Consumers read data from topics 
### Consumer group: 
Consumers read data in consumer groups and each consumer within a group reads from an exclusive partition.
### Commit: 
When a consumer in the group has processed data then it would commit this offset so that it is not read again. 
### Commit delivery semantics: 
At most once (maybe be data loss), At least once. Exactly once. 

&nbsp;
&nbsp;
&nbsp;
## Kafka Installation  

Download Kafka from page: https://kafka.apache.org/downloads  
Unzip and copy it to your base folder  
Add Kafka bin folder or bin\windows folder to your path variable  
Go to the kafka config folder and edit zookeeper.properties file and change the dataDir= to your desired location (probably inside Kakfa folder inside a data directory)  
Go to the kafka config folder and edit server.properties file and change the log.dirs=/ to your desired location (probably inside Kakfa folder inside a data directory)  

Start the zookeeper first with the following command from the Kafka base folder:   
&nbsp;&nbsp;&nbsp;bin/zookeeper-server-start.sh config/zookeeper.properties   

Start Kafka next with the following command from the Kafka base folder:   
&nbsp;&nbsp;&nbsp;bin/kafka-server-start.sh config/server.properties  

Incase of windows the command would be:  
&nbsp;&nbsp;&nbsp;zookeeper-server-start config\zookeeper.properties  
&nbsp;&nbsp;&nbsp;kafka-server-start config\server.properties  

To stop zookeepr on windows:  
&nbsp;&nbsp;&nbsp;zookeeper-server-stop  

Kafka must be up and running now.  

&nbsp;
&nbsp;
&nbsp;
## Kafka Commands    
### Create a topic  
kafka-topics --zookeeper localhost:2181 --topic my-topic --create --partitions 3 --replication-factor 1  

### List topics  
kafka-topics --zookeeper localhost:2181 --list  

### Describe information about topic  
kafka-topics --zookeeper localhost:2181 --topic my-topic --describe  

### Delete topic 
kafka-topics --zookeeper localhost:2181 --topic my-topic --delete  

### Producer
kafka-console-producer --broker-list localhost:9092 --topic my_topic  

### Adding Producer Properties  
kafka-console-producer --broker-list localhost:9092 --topic my_topic --producer-property acks=all  

### Producers with Key/Value  
kafka-console-producer --broker-list localhost:9092 --topic my_topic --property parse.key=true --property key.separator=,  

### Consumer (this will read messages from the time the consumer was launched)  
kafka-console-consumer --bootstrap-server localhost:9092 --topic my_topic  

### Consumer  
kafka-console-consumer --bootstrap-server localhost:9092 --topic my_topic  --from-beginning  
This will read all messages from the beggining that were produced   
But ordering is per partition order and not total ordering   
Also if one consumer from the same group consumed all messages from beginning then another consumer from the same group will not get messages from the beginning   

### Consumers with Key/value  
kafka-console-consumer --bootstrap-server localhost:9092 --topic my_topic --from-beginning --property print.key=true --property key.separator=,   

### Consumer Group  
kafka-console-consumer --bootstrap-server localhost:9092 --topic my_topic   --group my_topic_group  
If multiple consumers start with the same group then messages will be consumed by each of them one by one in a round robin since they are all in the same group   
 
### To List all consumer groups  
kafka-consumer-groups --bootstrap-server localhost:9092 --list  

### To see the details of a particular consumer group   
kafka-consumer-groups --bootstrap-server localhost:9092 --group my_topic_group --describe   

### To reset offsets in a group  
kafka-consumer-groups --bootstrap-server localhost:9092 --group my_topic_group --topic my_topic --reset-offsets --to-earliest --execute   
"to-earliest" which is to be beginning can be changed with other available options (check docmumentation)   

&nbsp;
&nbsp;
&nbsp;
## Kafka Sample Programs

### Simple Java Producer
1. This simple java producer project has two programs one with topic and value and another with topic, key and value. The program is simple and self explainatory.   

2. The main dependency for this project is   
```xml
	<dependency>
	    <groupId>org.apache.kafka</groupId>
	    <artifactId>kafka-clients</artifactId>
	    <version>2.8.0</version>
	</dependency>
	<dependency>
	    <groupId>org.slf4j</groupId>
	    <artifactId>slf4j-simple</artifactId>
	    <version>1.7.30</version>
	</dependency>
``` 


### Simple Java Consumer
1. This simple java consumer project which is self explainatory       

2. The main dependency for this project is the same as before    
```xml
	<dependency>
	    <groupId>org.apache.kafka</groupId>
	    <artifactId>kafka-clients</artifactId>
	    <version>2.8.0</version>
	</dependency>
	<dependency>
	    <groupId>org.slf4j</groupId>
	    <artifactId>slf4j-simple</artifactId>
	    <version>1.7.30</version>
	</dependency>
``` 

3. If you start multiple consumers within the same group, the partitions are rebalanced and each consumer is assigned to a patition automatically by the kafka framework.    
The same is the case while dropping consumeers also where the partitions are automatically rebalanced within the available consumers.    

4. If you need to seek a particular partition and read out a range of offsets starting from a particular offset then I have a sample program called SimpleConsumerAssignAndSeek.java which does exactly this.    

#### Important to note that an older client can always talk to newer brokers and newer clients can also talk to older brokers starting from Kafka 0.10.2 onwards   
Link to configuring consumers:  https://kafka.apache.org/documentation/#consumerconfigs    
Link to configuring producers:  https://kafka.apache.org/documentation/#producerconfigs     


### Twitter Producer   
In this application we can monitor twittter for certian keywords and if we find tweets on them, we can stream them using our twitter producer.    

1. For this to work, we need to create a developer account with Twitter and create a new application and get the relevent API Key, API Secret Key, access token and its secret.   

2. Next start the zookeeper   
bin/zookeeper-server-start.sh config/zookeeper.properties   

Then start kafka   
bin/kafka-server-start.sh config/server.properties   

Then create a topic that we need   
bin/kafka-topics.sh --zookeeper localhost:2181 --topic twitter_tweets --create --partitions 6 --replication-factor 1   

Next create a console consumer to consume our streamed data from the producer with the following command   
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic twitter_tweets   

3. With this in place we need to create a maven project add dependencies to pull our twitter client for java. The dependency for this is   
```xml
<dependency>
  <groupId>com.twitter</groupId>
  <artifactId>hbc-core</artifactId> <!-- or hbc-twitter4j -->
  <version>2.2.0</version> <!-- or whatever the latest version is -->
</dependency>
``` 
Other than this, the other 2 dependciens are the same as our simple java producer sample program that we already created   

4. Creating a real time twitter monitoring client is a 4 step process as follows:   
a. First create a twitter client   
b. Next create a Kafka producer   
c. Connect to the client and poll the messages based on our keywords   
d. Iterate over the messages and send the message to the kafka producer   
f. Run a client who will receive this steamed message from the Kafka producer in real time.   

5. Also added safe producer and high throughput configurations. Two advanced configurations have also been commented out. Please check this section under the getKafKaProducer() method to have a problem less producer    

6. The reference for twitter client can be found in the below URL:    
https://github.com/twitter/hbc   


### Elasticsearch Basic commands & Elasticsearch consumer:    

# Basics of Elastic Search
Start Elasticserch after installation    
./bin/elasticsearch   

Query the server status   
GET localhost:9200/   

Create index    
PUT localhost:9200/twitter   

Query the index   
GET localhost:9200/\_cat/indices   

Insert Data (tweets is the index name and 1 is id of the index)   
PUT localhost:9200/twitter/tweets/1   
Json Body   
{    
&nbsp;&nbsp;&nbsp;"student_name": "Balaji",   
&nbsp;&nbsp;&nbsp;"course_name": "ElasticSearch",    
&nbsp;&nbsp;&nbsp;"level":"beginners"   
}   

View the inserted data   
GET localhost:9200/twitter/tweets/1   

Delete the data   
DELETE localhost:9200/twitter/tweets/1   

Delete the index   
DELETE localhost:9200/twitter   

Query all records
http://localhost:9200/twitter/\_search?pretty=true&q=\*:\*   

1. To add our twitter consumer data to Elastic search we need to add the following two depenedencies:    

```xml
<dependency>
    <groupId>org.elasticsearch.client</groupId>
    <artifactId>elasticsearch-rest-high-level-client</artifactId>
    <version>7.12.1</version>
</dependency>
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.8.6</version>
</dependency>
```

2. Now create an elastic search client that will be used to insert data into elasticsearch. The steps are as follows:    
Create a Kafka consumer that will listen to the topic   
Poll every 100 milliseconds and iterate through the consumer records that has been fetched based.    
We have set the Kafka client parameters ENABLE_AUTO_COMMIT_CONFIG to false and set MAX_POLL_RECORDS_CONFIG to 100. So from Kafka topic every 100 records will be fetched.    
Create an elastic search IndexRequest and pass the Kafka consumerRecords that were fetched as a bulk into it   
Add this index request that was created into the elastic search BulkRequest   
Since autocommit is false for every 100 records fetched BulkRequest will be committed and BulkResponse will be returned.   
We can always iterate the BulkResponse if we want to check the response details.   
Finally commitSync() on the kafka consumer.  
Note, that jsonParser was used from Gson library to extact the tweet id 'id_str' that was used as an index in the IndexRequest used to store inside Elasticsearch.   

Finally we can query our inserted record using postman method GET http://localhost:9200/twitter/\_doc/id_str where id_str is like 1387826975499300874 which is the id_str from twitter data.    















 