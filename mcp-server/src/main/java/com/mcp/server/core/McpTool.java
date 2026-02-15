package com.mcp.server.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) // This can only be put on methods
@Retention(RetentionPolicy.RUNTIME) // We need to see this tag while the app is running
public @interface McpTool {
    String name();        // The name the AI will use to call the tool
    String description(); // The instructions telling the AI what the tool does
    String[] parameters() default {}; // The names of the inputs required
}