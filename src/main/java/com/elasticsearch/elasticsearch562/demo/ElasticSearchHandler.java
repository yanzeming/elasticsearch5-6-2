package com.elasticsearch.elasticsearch562.demo;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import static org.elasticsearch.common.xcontent.XContentFactory.*;

public class ElasticSearchHandler {
    public static void main(String[] args) {
        try {
            /* 创建客户端 */
            // client startup
            String str = "10.0.0.30";       String[] ipStr = str.split("\\.");
            byte[] ipBuf = new byte[4];
            for(int i = 0; i < 4; i++){
                ipBuf[i] = (byte)(Integer.parseInt(ipStr[i])&0xff);
            }

            InetAddress ia = InetAddress.getByAddress(ipBuf);

             InetSocketTransportAddress node = new InetSocketTransportAddress(ia, 9300);
            Settings settings = Settings.builder().put("cluster.name", "fc").build();
            TransportClient client = new PreBuiltTransportClient(settings);
            client.addTransportAddress(node);
            List<String> jsonData = DataFactory.getInitJsonData();

            for (int i = 0; i < jsonData.size(); i++) {
                IndexResponse response = client.prepareIndex("indexs", "article")
                        .setSource(jsonData.get(i)).get();
                /*if (response.isCreated()) {
                    System.out.println("创建成功!");
                }*/
            }
            client.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}