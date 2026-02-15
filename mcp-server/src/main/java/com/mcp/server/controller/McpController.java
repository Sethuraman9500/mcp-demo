package com.mcp.server.controller;

import com.mcp.server.core.ToolDefinition;
import com.mcp.server.core.ToolRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/mcp")
public class McpController {

    private static final Logger log = Logger.getLogger(McpController.class.getName());

    @Autowired
    private ToolRegistry toolRegistry;

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
                "status", "UP",
                "service", "MCP Server",
                "tools_count", toolRegistry.getAllTools().size(),
                "integrations_count", toolRegistry.getAllTools().stream()
                        .map(t -> t.adapter().getName())
                        .distinct()
                        .count()
        );
    }

    @GetMapping("/tools")
    public Collection<Map<String, Object>> listTools() {
        log.info("Listing all available tools");

        return toolRegistry.getAllTools().stream()
                .map(tool -> Map.of(
                        "name", tool.name(),
                        "description", tool.description(),
                        "parameters", tool.parameters(),
                        "integration", tool.adapter().getName()
                ))
                .collect(Collectors.toList());
    }

    @PostMapping("/execute")
    public Map<String, Object> execute(@RequestBody Map<String, Object> request) {
        String toolName = (String) request.get("toolName");
        Map<String, Object> params = (Map<String, Object>) request.get("parameters");

        log.info("Executing tool: " + toolName + " with params: " + params);

        try {
            Object result = toolRegistry.executeTool(toolName, params);

            log.info("Tool executed successfully");
            return Map.of(
                    "success", true,
                    "result", result
            );

        } catch (Exception e) {
            log.severe("Tool execution failed: " + e.getMessage());
            return Map.of(
                    "success", false,
                    "error", e.getMessage()
            );
        }
    }
}