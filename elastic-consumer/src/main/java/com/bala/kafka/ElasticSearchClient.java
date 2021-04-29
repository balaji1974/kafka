package com.bala.kafka;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElasticSearchClient {
	
	private static Logger logger=LoggerFactory.getLogger(ElasticSearchClient.class.getName());
	
	private static final String ELASTIC_SERVER_NAME1="localhost";
	private static final Integer ELASTIC_SERVER_PORT1=9200;
	private static final String ELASTIC_SERVER_PROTOCOL1="http";
	
	private static final String INDEX_NAME="twitter";
	private static final String INDEX_TYPE="tweets";
	
	public static void main(String str[]) throws IOException {
		RestHighLevelClient client=createClient();
		
		String jsonString="{\"name\":\"Balaji\"}";
		
		IndexRequest indexRequest=new IndexRequest(INDEX_NAME,INDEX_TYPE).source(jsonString,XContentType.JSON);
		
		IndexResponse indexResponse=client.index(indexRequest, RequestOptions.DEFAULT);
		String id=indexResponse.getId();
		logger.info(id);
		
		client.close();
		
	}
	
	private static RestHighLevelClient createClient() {
		RestHighLevelClient client = new RestHighLevelClient(
		        RestClient.builder(
		                new HttpHost(ELASTIC_SERVER_NAME1, ELASTIC_SERVER_PORT1, ELASTIC_SERVER_PROTOCOL1))); 
		return client;
		
	}
}
