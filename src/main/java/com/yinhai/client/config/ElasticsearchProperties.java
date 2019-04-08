package com.yinhai.client.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author licb
 * @create 2019-04-03 18:50
 **/
@ConfigurationProperties(prefix = "ta404.modules.elasticsearch")
public class ElasticsearchProperties {
    private boolean enable;
    private String nodes;
    private String token;
    private int maxRetryTimeoutMillis;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getNodes() {
        return nodes;
    }

    public void setNodes(String nodes) {
        this.nodes = nodes;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getMaxRetryTimeoutMillis() {
        return maxRetryTimeoutMillis;
    }

    public void setMaxRetryTimeoutMillis(int maxRetryTimeoutMillis) {
        this.maxRetryTimeoutMillis = maxRetryTimeoutMillis;
    }
}
