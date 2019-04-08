package com.yinhai.client;

import com.alibaba.fastjson.JSONObject;
import com.yinhai.ta404.core.restservice.resultbaen.Page;
import org.elasticsearch.index.query.QueryBuilder;

import java.io.IOException;
import java.util.List;

public interface ElasticSearchClient {

    /**
     * 判断索引是否存在
     *
     * @param index
     * @return
     * @throws IOException
     */
    boolean indexExist(String index) throws IOException;

    /**
     * 创建索引
     *
     * @param index
     * @param mappingJSON
     * @throws IOException
     */
    void createIndex(String index, String mappingJSON) throws IOException;

    /**
     * 判断指定id的document是否存在
     *
     * @param index
     * @param docId
     * @return
     */
    boolean docExist(String index, String docId);

    /**
     * 单条插入document
     *
     * @param index
     * @param source
     * @throws IOException
     */
    boolean index(String index, JSONObject source) throws IOException;

    /**
     * 批量插入document
     *
     * @param index
     * @param sources
     * @return
     * @throws IOException
     */
    boolean bulkIndex(String index, List<JSONObject> sources) throws IOException;

    /**
     * 删除document
     *
     * @param index
     * @param docId
     * @return
     * @throws IOException
     */
    boolean delete(String index, String docId) throws IOException;

    /**
     * 批量删除 document
     *
     * @param index
     * @param docIds
     * @return
     * @throws IOException
     */
    boolean bulkDelete(String index, List<String> docIds) throws IOException;

    /**
     * 更新document
     *
     * @param index
     * @param source
     * @return
     * @throws IOException
     */
    boolean update(String index, JSONObject source) throws IOException;

    /**
     * 获取单条数据
     *
     * @param index
     * @param docId
     * @param fields
     * @return
     * @throws IOException
     */
    JSONObject get(String index, String docId, List<String> fields) throws IOException;

    /**
     * 搜索全部数据
     * @param index
     * @param queryBuilder
     * @param fields
     * @param orderByField
     * @param orderBySort
     * @return
     * @throws IOException
     */
    List<JSONObject> getList(String index, QueryBuilder queryBuilder, List<String> fields, String orderByField, String orderBySort) throws IOException;

    /**
     * 分页搜索数据
     * @param index
     * @param queryBuilder
     * @param fields
     * @param orderByField
     * @param orderBySort
     * @param pageNum
     * @param pageSize
     * @return
     * @throws IOException
     */
    Page<JSONObject> getPage(String index, QueryBuilder queryBuilder, List<String> fields, String orderByField, String orderBySort, int pageNum, int pageSize) throws IOException;
}
