package com.yinhai.store.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yinhai.client.ElasticSearchClient;
import com.yinhai.search.entity.DocDetail;
import com.yinhai.store.service.DocPersistenceService;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author licb
 * @create 2019-04-04 13:57
 **/
@Service
public class DocPersistenceServiceImpl implements DocPersistenceService {
    @Resource
    private ElasticSearchClient elasticSearchClient;
    @Resource
    private Environment environment;

    @Override
    public boolean save(List<DocDetail> docs) {
        List<JSONObject> sources = new ArrayList<>();
        for (DocDetail doc : docs) {
            sources.add(doc.buildSource());
        }
        try {
            String indexName = environment.getProperty("spring.application.name") + "_doc";
            return elasticSearchClient.bulkIndex(indexName, sources);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean update(DocDetail doc) {
        try {
            String indexName = environment.getProperty("spring.application.name") + "_doc";
            return elasticSearchClient.update(indexName, doc.buildSource());
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean delete(List<String> ids) {
        try {
            String indexName = environment.getProperty("spring.application.name") + "_doc";
            return elasticSearchClient.bulkDelete(indexName, ids);
        } catch (Exception e) {
            return false;
        }
    }
}
