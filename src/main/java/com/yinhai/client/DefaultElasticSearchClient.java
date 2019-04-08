package com.yinhai.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yinhai.ta404.core.restservice.resultbaen.Page;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.*;
import org.elasticsearch.common.document.DocumentField;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.VersionType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.pipeline.PipelineAggregatorBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author licb
 * @create 2018-06-12 9:35
 **/
public class DefaultElasticSearchClient implements ElasticSearchClient, InitializingBean, DisposableBean {
    private static final Logger logger = LoggerFactory.getLogger(DefaultElasticSearchClient.class);
    private RestHighLevelClient client;

    private String nodes;
    private String token;
    private int maxRetryTimeoutMillis;

    public DefaultElasticSearchClient(String nodes, String token, int maxRetryTimeoutMillis) {
        this.nodes = nodes;
        this.token = token;
        this.maxRetryTimeoutMillis = maxRetryTimeoutMillis;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        initialize();
    }

    @Override
    public void destroy() throws Exception {
        shutdown();
    }


    private void initialize() {
        String[] elasticsearchNodes = nodes.split(",");
        HttpHost[] httpHosts = new HttpHost[elasticsearchNodes.length];
        for (int i = 0, len = elasticsearchNodes.length; i < len; i++) {
            String[] addressInfo = elasticsearchNodes[i].split(":");
            httpHosts[i] = new HttpHost(addressInfo[0], Integer.valueOf(addressInfo[1]), "http");
        }
        RestClientBuilder restClientBuilder = RestClient.builder(httpHosts);
        restClientBuilder.setMaxRetryTimeoutMillis(maxRetryTimeoutMillis == 0 ? 30000 : maxRetryTimeoutMillis);
        if (StringUtils.isNotBlank(token)) {
            restClientBuilder.setDefaultHeaders(new Header[]{new BasicHeader("Authorization", "Basic " + token)});
        }
        client = new RestHighLevelClient(restClientBuilder);
    }

    private void shutdown() {
        if (client != null) {
            try {
                client.close();
            } catch (IOException e) {
                logger.error("shutdown error", e);
            }
        }
    }

    @Override
    public void createIndex(String index, String mappingJSON) throws IOException {
        CreateIndexRequest request = new CreateIndexRequest(index);
        request.mapping(index, mappingJSON, XContentType.JSON);
        request.settings(Settings.builder().put("index.blocks.read_only_allow_delete", "false").build());
        client.indices().create(request, RequestOptions.DEFAULT);
        logger.info("创建索引{}", index);
    }

    @Override
    public boolean indexExist(String index) throws IOException {
        GetIndexRequest request = new GetIndexRequest();
        request.indices(index);
        return client.indices().exists(request, RequestOptions.DEFAULT);
    }

    private void updateUnlockIndexSetting(String... indices) throws IOException {
        UpdateSettingsRequest updateSettingsRequest = new UpdateSettingsRequest();
        updateSettingsRequest.indices(indices);
        updateSettingsRequest.settings(Settings.builder().put("index.blocks.read_only_allow_delete", "false").build());
        client.indices().putSettings(updateSettingsRequest, RequestOptions.DEFAULT);
    }

    @Override
    public boolean docExist(String index, String docId) {
        GetRequest getRequest = new GetRequest(index, index, docId);
        GetResponse getResponse = null;
        try {
            getResponse = client.get(getRequest, RequestOptions.DEFAULT);
            return getResponse.isExists();
        } catch (IOException e) {
            return false;
        }
    }


    private IndexRequest buildIndexRequest(String index, JSONObject source) {
        IndexRequest indexRequest = null;
        String docId = source.getString("id");
        if (StringUtils.isNotBlank(docId)) {
            indexRequest = new IndexRequest(index, index, docId);
        } else {
            indexRequest = new IndexRequest(index, index);
        }
        indexRequest.source(source.toJSONString(), XContentType.JSON);
        indexRequest.opType(DocWriteRequest.OpType.CREATE);
        indexRequest.versionType(VersionType.INTERNAL);
        return indexRequest;
    }

    @Override
    public boolean index(String index, JSONObject source) throws IOException {
        IndexRequest indexRequest = buildIndexRequest(index, source);
        IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
        return indexResponse.getResult() == DocWriteResponse.Result.CREATED;
    }


    @Override
    public boolean bulkIndex(String index, List<JSONObject> sources) throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        for (JSONObject source : sources) {
            bulkRequest.add(buildIndexRequest(index, source));
        }
        BulkResponse responses = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        if (responses.hasFailures()) {
            String errorMsg = responses.buildFailureMessage();
            //索引被锁定
            if (errorMsg.contains("index read-only / allow delete")) {
                //检查磁盘是否满了
                String stateJson = getESNodeState();
                boolean isDiskFull = false;
                float diskPercent = 0f;
                String diskAvail = "0m";
                if (StringUtils.isEmpty(stateJson)) {
                    JSONArray nodeStates = JSON.parseArray(stateJson);
                    for (int i = 0, len = nodeStates.size(); i < len; i++) {
                        diskPercent = nodeStates.getJSONObject(i).getFloat("disk.used_percent");
                        diskAvail = nodeStates.getJSONObject(i).getString("disk.avail");
                        if (diskPercent > 98F || diskAvail.endsWith("mb")) {
                            isDiskFull = true;
                            break;
                        }
                    }
                }
                if (!isDiskFull) {
                    //解锁
                    updateUnlockIndexSetting(index);
                    client.bulk(bulkRequest, RequestOptions.DEFAULT);
                } else {
                    String diskPercentStr = String.valueOf(diskPercent);
                    logger.error("批量插入索引失败 ,磁盘占用率已达到{}%，可用空间{}，索引已锁定", diskPercentStr, diskAvail);
                }
            } else {
                logger.error("批量插入索引失败 : {}", errorMsg);
            }
        }
        return true;
    }

    private String getESNodeState() throws IOException {
        Request request = new Request("GET", "/_cat/nodes?format=json&pretty&h=id,http_address,version,node.role,cpu,heap.current,heap.percent,heap.max,disk.total,disk.used,disk.avail,disk.used_percent,ram.current,ram.percent,ram.max");
        Response response = client.getLowLevelClient().performRequest(request);
        HttpEntity httpEntity = response.getEntity();
        return EntityUtils.toString(httpEntity);
    }

    @Override
    public boolean delete(String index, String docId) throws IOException {
        DeleteRequest request = new DeleteRequest(index, index, docId);
        DeleteResponse deleteResponse = client.delete(request, RequestOptions.DEFAULT);
        return DocWriteResponse.Result.DELETED == deleteResponse.getResult();
    }

    @Override
    public boolean bulkDelete(String index, List<String> docIds) throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        for (String docId : docIds) {
            bulkRequest.add(new DeleteRequest(index, index, docId));
        }
        BulkResponse responses = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        return !responses.hasFailures();
    }

    @Override
    public boolean update(String index, JSONObject source) throws IOException {
        String docId = source.getString("id");
        Assert.isTrue(StringUtils.isNotBlank(docId), "id不能为空！");
        UpdateRequest updateRequest = new UpdateRequest(index, index, docId);
        updateRequest.doc(source.toJSONString(), XContentType.JSON);
        UpdateResponse updateResponse = client.update(updateRequest, RequestOptions.DEFAULT);
        return DocWriteResponse.Result.UPDATED == updateResponse.getResult();
    }

    @Override
    public JSONObject get(String index, String docId, List<String> fields) throws IOException {
        GetRequest getRequest = new GetRequest(index, index, docId);
        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
        if (getResponse.isExists()) {
            JSONObject record = new JSONObject();
            Map<String, DocumentField> fieldMap = getResponse.getFields();
            if (fields == null || fields.isEmpty()) {
                fieldMap.forEach((k, v) -> {
                    record.put(k, v.getValue());
                });
            } else {
                for (String field : fields) {
                    record.put(field, fieldMap.containsKey(field) ? fieldMap.get(field).getValue() : null);
                }
            }
            return record;
        }
        return null;
    }

    @Override
    public List<JSONObject> getList(String index, QueryBuilder queryBuilder, List<String> fields,String orderByField,String orderBySort) throws IOException {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(queryBuilder);
        if(StringUtils.isNotBlank(orderByField)){
            searchSourceBuilder.sort(orderByField, StringUtils.isNotBlank(orderBySort) ? SortOrder.valueOf(orderBySort.toUpperCase()):SortOrder.ASC);
        }
        SearchHits searchHits = getSearchHits(index, searchSourceBuilder);
        return searchHits2Records(searchHits, fields);
    }

    @Override
    public Page<JSONObject> getPage(String index, QueryBuilder queryBuilder, List<String> fields,String orderByField,String orderBySort, int pageNum, int pageSize) throws IOException {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(queryBuilder);
        searchSourceBuilder.from((pageNum - 1) * pageSize);
        searchSourceBuilder.size(pageSize);
        if(StringUtils.isNotBlank(orderByField)){
            searchSourceBuilder.sort(orderByField, StringUtils.isNotBlank(orderBySort) ? SortOrder.valueOf(orderBySort.toUpperCase()):SortOrder.ASC);
        }
        SearchHits searchHits = getSearchHits(index, searchSourceBuilder);
        long total = searchHits.getTotalHits();
        List<JSONObject> records = searchHits2Records(searchHits, fields);
        Page<JSONObject> page = new Page<>();
        page.setList(records);
        page.setCurrentSize(records.size());
        page.setPageNum(pageNum);
        page.setPageSize(pageSize);
        page.setTotal(total);
        return page;
    }

    private SearchHits getSearchHits(String index, SearchSourceBuilder searchSourceBuilder) throws IOException {
        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        return searchResponse.getHits();
    }

    private List<JSONObject> searchHits2Records(SearchHits searchHits, List<String> fields) {
        SearchHit[] hits = searchHits.getHits();
        List<JSONObject> records = new ArrayList<>();
        for (SearchHit hit : hits) {
            JSONObject record = new JSONObject();
            Map<String, DocumentField> fieldMap = hit.getFields();
            if (fields == null || fields.isEmpty()) {
                fieldMap.forEach((k, v) -> {
                    record.put(k, hit.field(k).getValue());
                });
            } else {
                for (String field : fields) {
                    record.put(field, fieldMap.containsKey(field) ? fieldMap.get(field).getValue() : null);
                }
            }
            records.add(record);
        }
        return records;
    }
}
