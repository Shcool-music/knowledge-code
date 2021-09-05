package problem.elasticsearchdemo.controller;

import org.apache.http.HttpEntity;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.settings.Settings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class IndexCreateController {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @GetMapping("/create-index/{indexName}")
    public HttpEntity createIndex(@PathVariable("indexName") String indexName) {
        //注意索引名要小写
        CreateIndexRequest request = new CreateIndexRequest(indexName);
        request.settings(Settings.builder()
                // 设置分片数为3， 副本为2
                .put("index.number_of_shards", 1)
                .put("index.number_of_replicas", 0)
        );
        try {
            CreateIndexResponse response = restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
