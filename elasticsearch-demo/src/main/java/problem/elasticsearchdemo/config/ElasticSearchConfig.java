package problem.elasticsearchdemo.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import problem.elasticsearchdemo.config.property.ElasticsearchEnv;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class ElasticSearchConfig {

    private static final String COMMA = ",";

    @Autowired
    private ElasticsearchEnv elasticsearchEnv;

    @Bean
    public RestHighLevelClient getRestHighLevelClient() {
        //es密码
        String[] hosts = elasticsearchEnv.getAddress().split(COMMA);
        List<HttpHost> hostList = new ArrayList<>();
        //拆分es地址
        for (String address : hosts) {
            int index = address.lastIndexOf(":");
            hostList.add(new HttpHost(address.substring(0, index), Integer.parseInt(address.substring(index + 1)), elasticsearchEnv.getScheme()));
        }
        //转换成 HttpHost 数组
        HttpHost[] httpHosts = hostList.toArray(new HttpHost[0]);

        //构建连接对象
        RestClientBuilder builder = RestClient.builder(httpHosts);

        //使用账号密码连接
        String password = elasticsearchEnv.getPassword();
        if (StringUtils.hasText(password)) {
            String username = elasticsearchEnv.getUsername();
            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
            builder.setHttpClientConfigCallback(f -> f.setDefaultCredentialsProvider(credentialsProvider));
        }

        // 异步连接延时配置
        builder.setRequestConfigCallback(requestConfigBuilder -> {
            requestConfigBuilder.setConnectTimeout(elasticsearchEnv.getConnectTimeout());
            requestConfigBuilder.setSocketTimeout(elasticsearchEnv.getSocketTimeout());
            requestConfigBuilder.setConnectionRequestTimeout(elasticsearchEnv.getConnectionRequestTimeout());
            return requestConfigBuilder;
        });


        // 异步连接数配置
        builder.setHttpClientConfigCallback(httpClientBuilder -> {
            httpClientBuilder.setMaxConnTotal(elasticsearchEnv.getMaxConnectNum());
            httpClientBuilder.setMaxConnPerRoute(elasticsearchEnv.getMaxConnectPerRoute());
            return httpClientBuilder;
        });

        return new RestHighLevelClient(builder);
    }
}
