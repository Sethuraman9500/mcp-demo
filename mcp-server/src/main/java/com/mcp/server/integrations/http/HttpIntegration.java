package com.mcp.server.integrations.http;

import com.mcp.server.core.IntegrationAdapter;
import com.mcp.server.core.McpTool;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;
import java.util.logging.Logger;

@Service
public class HttpIntegration extends IntegrationAdapter {

    private static final Logger log = Logger.getLogger(HttpIntegration.class.getName());
    private final WebClient webClient;

    public HttpIntegration() {
        super("http");
        this.webClient = WebClient.builder()
                .build();
    }

    @McpTool(
            name = "http_get",
            description = "Make a GET request to any URL and return the response",
            parameters = {"url"}
    )
    public String httpGet(String url) {
        log.info("Making GET request to: " + url);
        try {
            String response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(10))  // ← Add 10 second timeout
                    .onErrorResume(e -> Mono.just("Error: " + e.getMessage()))
                    .block();
            return response != null ? response : "Empty response";
        } catch (Exception e) {
            log.severe("GET request failed: " + e.getMessage());
            return "Error: " + e.getMessage();
        }
    }

    @McpTool(
            name = "http_post",
            description = "Make a POST request with a JSON body",
            parameters = {"url", "body"}
    )
    public String httpPost(String url, String body) {
        log.info("Making POST request to: " + url);
        try {
            String response = webClient.post()
                    .uri(url)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(10))  // ← Add 10 second timeout
                    .onErrorResume(e -> Mono.just("Error: " + e.getMessage()))
                    .block();
            return response != null ? response : "Empty response";
        } catch (Exception e) {
            log.severe("POST request failed: " + e.getMessage());
            return "Error: " + e.getMessage();
        }
    }

    @Override
    public boolean healthCheck() {
        return true;
    }

    @Override
    public Map<String, Object> getMetadata() {
        return Map.of(
                "type", "http",
                "description", "Generic HTTP client for REST API calls",
                "capabilities", "GET, POST"
        );
    }
}