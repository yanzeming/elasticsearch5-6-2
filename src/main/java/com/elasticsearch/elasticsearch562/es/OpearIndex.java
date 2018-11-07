package com.elasticsearch.elasticsearch562.es;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.UUID;

/**
 * 自动创建索引的时候,默认情况下,string会分次,int,double不分词,
 * 日期格式匹配的时候必须是规定的格式
 */
public class OpearIndex {
   //* @Autowired
    private static TransportClient client = null;
    static {
        InetSocketTransportAddress node = null;
        try {
            node = new InetSocketTransportAddress(
                    InetAddress.getByName("esip"), 9300
            );
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        Settings settings = Settings.builder().put("cluster.name", "fc").build();
        client = new PreBuiltTransportClient(settings);
       client.addTransportAddress(node);
   }


    public boolean isIndexExist(String index) {
        return client.admin().indices().prepareExists(index).execute().actionGet().isExists();
    }

    public boolean addIndex(String index) {
        if (isIndexExist(index)) {
            return false;
        }
        return client.admin().indices().prepareCreate(index).execute().actionGet().isAcknowledged();
    }
    public boolean deleteIndex(String index) {
        if (isIndexExist(index)) {
            return client.admin().indices().prepareDelete(index).execute().actionGet().isAcknowledged();
        }
        return false;
    }
    public boolean isTypeExist(String index, String type) {
        if (isIndexExist(index)) {
            return client.admin().indices().prepareTypesExists(index).setTypes(type).execute()
                    .actionGet().isExists();
        }
        return false;
    }
    public boolean addIndexAndType(String index, String type)  {
        if (index == null){
            index = "ahut";
        }
        if (type == null){
            type = "goods";
        }
        // 创建索引映射,相当于创建数据库中的表操作
        CreateIndexRequestBuilder cib = client.admin().indices().prepareCreate(index);
        XContentBuilder mapping = null;
        try {
            mapping = XContentFactory.jsonBuilder().startObject().startObject("properties") // 设置自定义字段
                .startObject("goodsName").field("type", "string")
                    .field("analyzer", "pinyin").endObject() // 商品名称
                .startObject("goodsPrice").field("type", "double").endObject()// 商品价格
                .startObject("goodsUser").field("type", "string")
                    .field("analyzer", "ik_smart").endObject()// 商品主人
                .startObject("goodsTime").field("type", "date").field("format",
                "yyyy-MM-dd HH:mm:ss").endObject() // 商品上架时间
                .endObject().endObject();
          /*  mapping = XContentFactory.jsonBuilder().startObject("fname")
                    .field("type", "string")
                    .field("store", "yes")
                    //.field("term_vector","with_positions_offsets")
                    .field("indexAnalyzer", "pinyin_analyzer")
                    .field("searchAnalyzer", "pinyin_analyzer")
                    .field("include_in_all", "false")
                    .field("boost", 4.0) // 打分(默认1.0)
                    .endObject().startObject("user")
                    .field("type", "string")
                    .field("store", "yes")
                   // .field("term_vector","with_positions_offsets")
                    .field("indexAnalyzer", "pinyin_analyzer")
                    .field("searchAnalyzer", "pinyin_analyzer")
                    .field("include_in_all", "false")
                    .field("boost", 4.0) // 打分(默认1.0)
                   ;*/
        } catch (IOException e) {
            e.printStackTrace();
        }
        cib.addMapping(type, mapping);
        return cib.execute().actionGet().isAcknowledged();
    }

    public long addDocument(String index, String type)  {
        if (index == null){
            index = "ahut";
        }
        if (type == null){
            type = "goods";
        }
        // 自定义主键id,这个id也可以不要,让es自动为我们生成id
        String id = UUID.randomUUID().toString().replace("-", "");
        // 创建文档,相当于往表里面insert一行数据
        IndexResponse response = null;
        try {
            response = client.prepareIndex(index, type, id)
                    .setSource(XContentFactory.jsonBuilder().startObject()// 开始
                            .field("goodsName", "大学英语")// 商品名称
                            .field("goodsPrice", 22.33)// 商品价格
                            .field("goodsUser", "中华人民共和国")// 商品主人
                            .field("goodsTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").
                                    format(new Date()))// 商品上架时间
                            .endObject())
                    .get();
                  /*  .setSource(XContentFactory.jsonBuilder().startObject()// 开始
                            .field("fname", "大学英语")// 商品名称

                            .field("user", "中华人民共和国")// 商品主人
                            .endObject())
                    .get();*/
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response.getVersion();
    }
    public String deleteDocument() {
        String index = "ahut";
        String type = "goods";
        String id = "5b1c93212c2f4d8e88e6bc91de22d08d";
        return client.prepareDelete(index, type, id).get().getId();
    }

    public static void main(String[] args){
        OpearIndex opearIndex = new OpearIndex();
        System.out.println(opearIndex.isIndexExist("people"));
        opearIndex.addIndexAndType("database", "table");
        opearIndex.addDocument("database", "table");
    }
}
