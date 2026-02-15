package com.mcp.server.core;

import jakarta.annotation.PostConstruct;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

@Component
public class ToolRegistry {

    private static final Logger log = Logger.getLogger(ToolRegistry.class.getName());

    private final ApplicationContext context;
    private final Map<String, ToolDefinition> tools = new ConcurrentHashMap<>();

    public ToolRegistry(ApplicationContext context) {
        this.context = context;
    }

    @PostConstruct
    public void scanTools() {
        log.info("Scanning for MCP tools...");

        context.getBeansOfType(IntegrationAdapter.class)
                .values()
                .forEach(this::registerIntegration);

        log.info("Registered " + tools.size() + " tools from " +
                context.getBeansOfType(IntegrationAdapter.class).size() + " integrations");
    }

    private void registerIntegration(IntegrationAdapter adapter) {
        log.info("Scanning integration: " + adapter.getName());

        Arrays.stream(adapter.getClass().getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(McpTool.class))
                .forEach(method -> {
                    McpTool annotation = method.getAnnotation(McpTool.class);
                    ToolDefinition def = new ToolDefinition(
                            annotation.name(),
                            annotation.description(),
                            Arrays.asList(annotation.parameters()),
                            adapter,
                            method
                    );
                    tools.put(annotation.name(), def);
                    log.info("Registered tool: " + annotation.name() +
                            " from integration: " + adapter.getName());
                });
    }

    public Object executeTool(String name, Map<String, Object> params) throws Exception {
        ToolDefinition tool = tools.get(name);
        if (tool == null) {
            throw new IllegalArgumentException("Tool not found: " + name);
        }

        Object[] args = tool.parameters().stream()
                .map(params::get)
                .toArray();

        return tool.method().invoke(tool.adapter(), args);
    }

    public Collection<ToolDefinition> getAllTools() {
        return tools.values();
    }
}