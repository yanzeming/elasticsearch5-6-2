package com.elasticsearch.elasticsearch562.es;

import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.mapper.Mapping;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Analysis {


    public String init() throws IOException {
        InetSocketTransportAddress node = new InetSocketTransportAddress(
                InetAddress.getByName("localhost"), 9300
        );
        Settings settings = Settings.builder().put("cluster.name", "fc").build();
        TransportClient client = new PreBuiltTransportClient(settings);
        client.addTransportAddress(node);



        IndexResponse
                indexResponse = client.prepareIndex("test_index6",
                "testType",
                "1").setSource(XContentFactory.jsonBuilder()
                .startObject()
                .field("id",
                        1)
                .field("type",
                        2)
                .field("title",
                        "hello world")
                .field("content",
                        "hello world content")
                .field("content2",
                        "中文和英文内容")
                .field("content4",
                        "中文和英文内容")
                .endObject()).execute().actionGet();
        indexResponse =
                client.prepareIndex("test_index6",
                        "testType",
                        "2").setSource(XContentFactory.jsonBuilder()
                        .startObject()
                        .field("id",
                                2)
                        .field("type",
                                3)
                        .field("title",
                                "hello world")
                        .field("content",
                                "hello world content")
                        .field("content2",
                                "中文内容和其他内容")
                        .field("content4",
                                "英文内容")
                        .endObject()).execute().actionGet();


    /*SearchResponse searchResponse =
            transportClient.prepareSearch("test_index6").setTypes("testType")
                    .setQuery(QueryBuilders.termQuery("type",
                            2)).execute().actionGet();
System.out.println(searchResponse);
System.out.println("termQuery准确查询");

    searchResponse =
            transportClient.prepareSearch("test_index6").setTypes("testType")
.setQuery(QueryBuilders.matchPhraseQuery("content4",
"英文内容")).execute().actionGet();
System.out.println(searchResponse);
System.out.println("matchPhraseQuery只查询连在一起的");

    searchResponse =
            transportClient.prepareSearch("test_index6").setTypes("testType")
.setQuery(QueryBuilders.matchQuery("content4",
"中文内容")).execute().actionGet();
System.out.println(searchResponse);
System.out.println("matchQuery查询包括不连在一起的");

        */

        return "chaugnjain成各";
    }

    public static void main(String[] args) throws IOException {
        Analysis analysis = new Analysis();
        analysis.init();
    }

}
