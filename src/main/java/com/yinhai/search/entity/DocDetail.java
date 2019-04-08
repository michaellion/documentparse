package com.yinhai.search.entity;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.io.Serializable;

/**
 * @author licb
 * @create 2019-04-03 13:54
 **/
public @Data class DocDetail implements Serializable {
    private String id;
    private String title;
    private String content;
    private String fileType;
    private String type1;
    private String type2;
    private String type3;
    private String type4;
    private String type5;
    private String[] tags;
    private String author;
    private long uploadDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType1() {
        return type1;
    }

    public void setType1(String type1) {
        this.type1 = type1;
    }

    public String getType2() {
        return type2;
    }

    public void setType2(String type2) {
        this.type2 = type2;
    }

    public String getType3() {
        return type3;
    }

    public void setType3(String type3) {
        this.type3 = type3;
    }

    public String getType4() {
        return type4;
    }

    public void setType4(String type4) {
        this.type4 = type4;
    }

    public String getType5() {
        return type5;
    }

    public void setType5(String type5) {
        this.type5 = type5;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public long getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(long uploadDate) {
        this.uploadDate = uploadDate;
    }

    public static String getMappingJSON(String indexName) {
        return "  {\n" +
                "    \"" + indexName + "\": " +
                "      {\n" +
                "      \"properties\": {\n" +
                "       \"id\":{ \"type\": \"keyword\"},\n" +
                "       \"title\":{ \"type\": \"text\",\"analyzer\":\"jcseg\"},\n" +
                "       \"content\":{ \"type\": \"text\",\"analyzer\":\"jcseg\"},\n" +
                "       \"fileType\":{ \"type\": \"keyword\"},\n" +
                "       \"author\":{ \"type\": \"keyword\"},\n" +
                "       \"uploadDate\":{ \"type\": \"long\"},\n" +
                "       \"type1\":{ \"type\": \"keyword\"},\n" +
                "       \"type2\":{ \"type\": \"keyword\"},\n" +
                "       \"type3\":{ \"type\": \"keyword\"},\n" +
                "       \"type4\":{ \"type\": \"keyword\"},\n" +
                "       \"type5\":{ \"type\": \"keyword\"},\n" +
                "       \"tags\":{ \"type\": \"keyword\"}\n" +
                "        }\n" +
                "    }\n" +
                "}";
    }

    public JSONObject buildSource() {
        Assert.isTrue(StringUtils.isNotBlank(getId()), "id不能为空");
        JSONObject source = new JSONObject();
        source.put("id", getId());
        if (StringUtils.isNotBlank(getTitle())) {
            source.put("title", getTitle());
        }
        if (StringUtils.isNotBlank(getContent())) {
            source.put("content", getContent());
        }
        if (StringUtils.isNotBlank(getFileType())) {
            source.put("fileType", getFileType());
        }
        if (StringUtils.isNotBlank(getType1())) {
            source.put("type1", getType1());
        }
        if (StringUtils.isNotBlank(getType2())) {
            source.put("type2", getType2());
        }
        if (StringUtils.isNotBlank(getType3())) {
            source.put("type3", getType3());
        }
        if (StringUtils.isNotBlank(getType4())) {
            source.put("type4", getType4());
        }
        if (StringUtils.isNotBlank(getType5())) {
            source.put("type5", getType5());
        }
        if (getTags() != null && getTags().length > 0) {
            source.put("tags", getTags());
        }
        if (StringUtils.isNotBlank(getAuthor())) {
            source.put("author", getAuthor());
        }
        if (getUploadDate() > 0) {
            source.put("uploadDate", getUploadDate());
        }
        return source;
    }
}
