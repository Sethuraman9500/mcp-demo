package com.mcp.server.core;

import java.util.Map;

public abstract class IntegrationAdapter {
    protected final String name;

    protected IntegrationAdapter(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract boolean healthCheck();

    public abstract Map<String, Object> getMetadata();
}