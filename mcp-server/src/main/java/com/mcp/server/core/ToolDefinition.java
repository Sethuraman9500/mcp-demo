package com.mcp.server.core;

import java.lang.reflect.Method;
import java.util.List;

public record ToolDefinition(
        String name,
        String description,
        List<String> parameters,
        IntegrationAdapter adapter,
        Method method
) {}