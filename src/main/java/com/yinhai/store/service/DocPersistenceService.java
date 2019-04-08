package com.yinhai.store.service;

import com.yinhai.search.entity.DocDetail;

import java.util.List;

public interface DocPersistenceService {
    /**
     * 保存文档
     * @param docs
     * @return
     */
    boolean save(List<DocDetail> docs);

    /**
     * 修改文档
     * @param doc
     * @return
     */
    boolean update(DocDetail doc);

    /**
     * 删除文档
     * @param ids
     * @return
     */
    boolean delete(List<String> ids);
}
