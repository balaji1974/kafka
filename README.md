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













 