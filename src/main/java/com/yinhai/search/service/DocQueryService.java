package com.yinhai.search.service;

import com.alibaba.fastjson.JSONObject;
import com.yinhai.ta404.core.restservice.requestbean.PageParam;
import com.yinhai.ta404.core.restservice.resultbaen.Page;

import java.util.List;

public interface DocQueryService {

    /**
     * 查询单个文档
     * @param id
     * @return
     */
    JSONObject get(String id);
    /**
     * 分页查询文档
     * @param queryParam
     * @param pageParam
     * @return
     */
    Page<JSONObject> getDocsPage(QueryParam queryParam, PageParam pageParam);

    /**
     * 查询文档列表
     * @param queryParam
     * @return
     */
    List<JSONObject> getDocs(QueryParam queryParam);
}
