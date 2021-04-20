# Apache Kafka  

## Key Concepts  

### Topics: Stream of data (like table in a database)
Topics are split into partitions and they are ordered. 
Each message within a partition gets an incremental id called offset
A kafka cluster consists of multiple brokers (Servers). 
Every broker is a bootstrap server which has information for all other brokers. 
Partitions are replicated automatically across brokers. 
In replication we have a leader partition which receives data directly while the ISR (in sync Replica) replicates data from the leader.
The process of leader and ISR is managed by Zoo keeper automatically. A Zoo keeper manages brokers. 
Producers write data to topics and can choose to receive or not receive acknowledgement for the writes. 
[acks-0 - producer will not wait for acknowledgement (possible data loss) 
acks-1 - producer will wait for leader to acknowledgement (limited data loss) - This is default 
acks-All - producer will wait for leader and replicas to acknowledge - No data loss] 
Producers can send Keys with messages. If the key is null then write partition cannot be determined and data is written in round robin.  
Key is useful for message ordering
Consumers read data from topics 
Data is read by consumers within a partition in the order of offset 
Consumers read data in consumer groups and each consumer within a group reads from an exclusive partition.
When a consumer in the group has processed data then it would commit this offset so that it is not read again. 
Commit delivery semantics: At most once (maybe be data loss), At least once. Exactly once. 
Zookeeper by design works only with an odd number of servers. Eg. we cannot have 2 zookeepers. We need to have 1,3,5 etc. with the concept of leader/followers 