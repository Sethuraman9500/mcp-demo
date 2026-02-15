package com.mcp.server.integrations.weather;

import com.mcp.server.core.IntegrationAdapter;
import com.mcp.server.core.McpTool;
import com.mcp.server.integrations.http.HttpIntegration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.logging.Logger;

@Service
public class WeatherIntegration extends IntegrationAdapter {

    private static final Logger log = Logger.getLogger(WeatherIntegration.class.getName());

    @Autowired
    private HttpIntegration httpIntegration;

    public WeatherIntegration() {
        super("weather");
    }

    @McpTool(
            name = "get_weather",
            description = "Get current weather for any city using wttr.in API",
            parameters = {"city"}
    )
    public String getWeather(String city) {
        log.info("Getting weather for: " + city);

        // wttr.in is a free weather API - no key needed!
        String url = "https://wttr.in/" + city + "?format=%l:+%C+%t+%h+%w";

        try {
            return httpIntegration.httpGet(url);
        } catch (Exception e) {
            return "Error fetching weather: " + e.getMessage();
        }
    }

    @Override
    public boolean healthCheck() {
        try {
            String result = httpIntegration.httpGet("https://wttr.in/?format=%t");
            return result != null && !result.contains("Error");
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Map<String, Object> getMetadata() {
        return Map.of(
                "type", "weather",
                "description", "Weather data from wttr.in",
                "api_key_required", false
        );
    }
}