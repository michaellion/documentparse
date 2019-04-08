package com.yinhai.client.config;

import com.yinhai.client.DefaultElasticSearchClient;
import com.yinhai.client.ElasticSearchClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import javax.annotation.Resource;

/**
 * @author licb
 * @create 2019-04-03 18:42
 **/
@Configuration
@ConditionalOnProperty(name = "ta404.modules.elasticsearch.enable", havingValue = "true")
@EnableConfigurationProperties({ElasticsearchProperties.class})
public class ElasticSearchClientConfiguration {
    @Resource
    private ElasticsearchProperties elasticsearchProperties;

    @Bean
    public ElasticSearchClient elasticSearchClient() {
        Assert.isTrue(StringUtils.isNotBlank(elasticsearchProperties.getNodes()), "elasticsearch nodes 配置不能为空！");
        return new DefaultElasticSearchClient(elasticsearchProperties.getNodes(), elasticsearchProperties.getToken(), elasticsearchProperties.getMaxRetryTimeoutMillis());
    }
}
