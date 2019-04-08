package com.yinhai.search.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yinhai.client.ElasticSearchClient;
import com.yinhai.search.service.DocQueryService;
import com.yinhai.search.service.QueryParam;
import com.yinhai.ta404.core.exception.AppException;
import com.yinhai.ta404.core.restservice.requestbean.PageParam;
import com.yinhai.ta404.core.restservice.resultbaen.Page;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * @author licb
 * @create 2019-04-04 15:34
 **/
public class DocQueryServiceImpl implements DocQueryService {
    @Resource
    private ElasticSearchClient elasticSearchClient;
    @Resource
    private Environment environment;

    @Override
    public JSONObject get(String id) {
        String indexName = environment.getProperty("spring.application.name") + "_doc";
        List<String> fields = Arrays.asList("id", "title", "fileType", "tags", "author", "uploadDate");
        try {
            return elasticSearchClient.get(indexName,id,fields);
        }catch (Exception e){
            throw new AppException("查询文档失败：" + e.getMessage());
        }
    }

    @Override
    public Page<JSONObject> getDocsPage(QueryParam queryParam, PageParam pageParam) {
        String indexName = environment.getProperty("spring.application.name") + "_doc";
        QueryBuilder queryBuilder = buildQueryBuilder(queryParam);
        List<String> fields = Arrays.asList("id", "title", "fileType", "tags", "author", "uploadDate");
        try {
            return elasticSearchClient.getPage(indexName, queryBuilder, fields,queryParam.getOrderBy(),queryParam.getSort(), pageParam.getPageNumber(), pageParam.getPageSize());
        } catch (Exception e) {
            throw new AppException("分页查询文档失败：" + e.getMessage());
        }
    }

    @Override
    public List<JSONObject> getDocs(QueryParam queryParam) {
        String indexName = environment.getProperty("spring.application.name") + "_doc";
        QueryBuilder queryBuilder = buildQueryBuilder(queryParam);
        List<String> fields = Arrays.asList("id", "title", "fileType", "tags", "author", "uploadDate");
        try {
            return elasticSearchClient.getList(indexName, queryBuilder, fields,queryParam.getOrderBy(),queryParam.getSort());
        } catch (Exception e) {
            throw new AppException("查询文档列表失败：" + e.getMessage());
        }
    }

    private QueryBuilder buildQueryBuilder(QueryParam queryParam) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (StringUtils.isNotBlank(queryParam.getText())) {
            boolQueryBuilder.must(QueryBuilders.boolQuery()
                    .should(QueryBuilders.disMaxQuery().add(QueryBuilders.fuzzyQuery("title", queryParam.getText())))
                    .should(QueryBuilders.disMaxQuery().add(QueryBuilders.fuzzyQuery("content", queryParam.getText()))));
        }
        if (StringUtils.isNotBlank(queryParam.getFileType())) {
            boolQueryBuilder.must(QueryBuilders.termQuery("fileType", queryParam.getFileType()));
        }
        if (StringUtils.isNotBlank(queryParam.getAuthor())) {
            boolQueryBuilder.must(QueryBuilders.termQuery("author", queryParam.getAuthor()));
        }
        if (StringUtils.isNotBlank(queryParam.getType1())) {
            boolQueryBuilder.must(QueryBuilders.termQuery("type1", queryParam.getType1()));
        }
        if (StringUtils.isNotBlank(queryParam.getType2())) {
            boolQueryBuilder.must(QueryBuilders.termQuery("type2", queryParam.getType2()));
        }
        if (StringUtils.isNotBlank(queryParam.getType3())) {
            boolQueryBuilder.must(QueryBuilders.termQuery("type3", queryParam.getType3()));
        }
        if (StringUtils.isNotBlank(queryParam.getType4())) {
            boolQueryBuilder.must(QueryBuilders.termQuery("type4", queryParam.getType4()));
        }
        if (StringUtils.isNotBlank(queryParam.getType5())) {
            boolQueryBuilder.must(QueryBuilders.termQuery("type5", queryParam.getType5()));
        }
        if (queryParam.getTags() != null && queryParam.getTags().length > 0) {
            boolQueryBuilder.must(QueryBuilders.termsQuery("tags", queryParam.getTags()));
        }
        if (queryParam.getStartTime() > 0) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery("uploadDate").gte(queryParam.getStartTime()));
        }
        if (queryParam.getEndTime() > 0) {
            boolQueryBuilder.must(QueryBuilders.rangeQuery("uploadDate").lte(queryParam.getEndTime()));
        }
        System.out.println("queryString" + boolQueryBuilder.toString());
        return boolQueryBuilder;
    }

}
