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
&nbsp;&nbsp;&nbsp;zookeeper-server-start.bat config\zookeeper.properties  
&nbsp;&nbsp;&nbsp;kafka-server-start.bat config\server.properties  

Kafka must be up and running now.  




 