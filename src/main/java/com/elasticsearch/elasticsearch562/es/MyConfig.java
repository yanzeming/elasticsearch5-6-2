package com.elasticsearch.elasticsearch562.es;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Controller
public class MyConfig {
  private static final Logger logger= LoggerFactory.getLogger(MyConfig.class);
    @Autowired
    private TransportClient client;

  /*  @Autowired
    private UserRepository userRepository;*/

   /* @RequestMapping("/es/create")
    @ResponseBody//http://blog.csdn.net/ljc2008110/article/details/48652937
    public ResponseEntity create(){
        logger.info("es测试");
        CreateIndexResponse response= client.admin().indices().prepareCreate
                ("productIndex").execute().actionGet();
        return new ResponseEntity(response.index(),HttpStatus.OK);
    }*/
    @RequestMapping("/es/book/novel")
    @ResponseBody
    public ResponseEntity get(@RequestParam("id")String id ){
        if(id.isEmpty()) return new ResponseEntity(HttpStatus.NOT_FOUND);
        GetResponse result=this.client.prepareGet("book","novel",id).get();
        if(!result.isExists()){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(result.getSource(),HttpStatus.OK);
    }

    @RequestMapping("/es/books/novel")
    @ResponseBody
    public ResponseEntity getNovels(@RequestParam("id")String id ){
        if(id.isEmpty()) return new ResponseEntity(HttpStatus.NOT_FOUND);
        GetResponse result=this.client.prepareGet("book","novel",id).get();
        if(!result.isExists()){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(result.getSource(),HttpStatus.OK);
    }
   @RequestMapping("/es/books/novels/add")
    @ResponseBody
    public ResponseEntity add(@RequestParam("name")String name,
                              @RequestParam("id")String id,@RequestParam("num")Integer num){
        try {
         /*   User user=userRepository.findOne(1);
            Gson gson=new Gson();
            String json=gson.toJson(user);
            logger.info(json);*/
            XContentBuilder content= XContentFactory.jsonBuilder().startObject().
                    field("id",id).field("num",num).
                    field("auther","三毛").
                    field("user",id + name + num).
                    field("name",name).endObject();
            IndexResponse result=this.client.prepareIndex("book","novel").
                    setSource(content).get();
            return new ResponseEntity(result.getId(), HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
   @RequestMapping("/es/book/novel/delete")
    @ResponseBody
    public ResponseEntity delete(@RequestParam("id")String id){
       DeleteResponse result=this.client.prepareDelete
               ("book","novle",id).get();
       return new ResponseEntity(result.getResult().toString(),HttpStatus.OK);
   }
   @RequestMapping("/es/book/novel/update")
    @ResponseBody
    public ResponseEntity update(@RequestParam("id")String id,@RequestParam("name")
                                 String name){
        UpdateRequest update= new UpdateRequest("novel","book",id);
        try {
            XContentBuilder builder=XContentFactory.jsonBuilder().startObject();
            if(name!=null)builder.field("name",name);
            builder.endObject();
            update.doc(builder);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        try {
            UpdateResponse result=this.client.update(update).get();
            return new ResponseEntity(result.getResult().toString(),HttpStatus.OK);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (ExecutionException e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
   @RequestMapping("/es/book/novel/search")
    @ResponseBody
    public ResponseEntity search(@RequestParam("name")String name){
       BoolQueryBuilder boolQuery= QueryBuilders.boolQuery();
       if(name!=null){
           boolQuery.must(QueryBuilders.matchQuery("name",name));
       }
       RangeQueryBuilder rangeQuery=QueryBuilders.rangeQuery("num");
       rangeQuery.from("1");
       rangeQuery.to("1000");
       boolQuery.filter(rangeQuery);
       SearchRequestBuilder builder=this.client.prepareSearch("book").
               setTypes("novel").setSearchType(SearchType.DFS_QUERY_THEN_FETCH).
               setQuery(boolQuery).setFrom(0).setSize(10);
       logger.info(builder.toString());
       List<Map<String,Object>> result=new ArrayList();
       SearchResponse response=builder.get();
       for(SearchHit hit:response.getHits()){
           result.add(hit.getSource());
       }
       return new ResponseEntity(result,HttpStatus.OK);
   }
}

