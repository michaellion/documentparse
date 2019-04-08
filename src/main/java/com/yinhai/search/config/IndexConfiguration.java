package com.yinhai.search.config;

import com.yinhai.client.ElasticSearchClient;
import com.yinhai.search.entity.DocDetail;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;

/**
 * @author licb
 * @create 2019-04-04 11:18
 **/
@Configuration
@ConditionalOnBean(ElasticSearchClient.class)
public class IndexConfiguration implements InitializingBean {
    @Resource
    private ElasticSearchClient elasticSearchClient;
    @Resource
    private Environment environment;

    @Override
    public void afterPropertiesSet() throws Exception {
        String indexName = environment.getProperty("spring.application.name") + "_doc";
        if (!elasticSearchClient.indexExist(indexName)) {
            elasticSearchClient.createIndex(indexName, DocDetail.getMappingJSON(indexName));
        }
    }
}
