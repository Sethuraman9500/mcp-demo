package com.mcp.server.mcp;

import com.mcp.server.core.ToolRegistry;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class ToolMcpAdapter {

    private final ToolRegistry toolRegistry;

    public ToolMcpAdapter(ToolRegistry toolRegistry) {
        this.toolRegistry = toolRegistry;
    }

    // Spring AI 2.0.0-M2 detects this and exposes it via /mcp
    @McpTool(name = "execute_tool", description = "Invoke a tool from the internal registry")
    public String executeTool(
            @McpToolParam(description = "The name of the tool to execute") String toolName,
            @McpToolParam(description = "Key-value parameters for the tool") Map<String, Object> parameters) {

        try {
            Object result = toolRegistry.executeTool(toolName, parameters);
            return result != null ? result.toString() : "Success (no output)";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}