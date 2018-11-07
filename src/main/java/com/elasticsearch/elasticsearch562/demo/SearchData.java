package com.elasticsearch.elasticsearch562.demo;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.swing.text.html.parser.Entity;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class SearchData {
    @Autowired
    private TransportClient client;
    @RequestMapping(value = "searchTime", method = RequestMethod.GET)
    public ResponseEntity searchTime(@RequestParam("time") String time){
        BoolQueryBuilder boolQuery= QueryBuilders.boolQuery();
        System.out.println(time);
        if(time!=null){
          //  boolQuery.must(QueryBuilders.matchQuery("posttime",time)); //posttime
            boolQuery.must(QueryBuilders.matchQuery("title", time));
        }
        SearchRequestBuilder builder=this.client.prepareSearch("blog").
                setTypes("article").setSearchType(SearchType.DFS_QUERY_THEN_FETCH).
                setQuery(boolQuery);
        List<Map<String,Object>> result=new ArrayList();
        SearchResponse response=builder.get();
        for(SearchHit hit:response.getHits()){
            result.add(hit.getSource());
        }
        return new ResponseEntity(result, HttpStatus.OK);
    }
}
