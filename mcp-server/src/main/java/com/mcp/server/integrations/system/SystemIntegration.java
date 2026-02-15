package com.mcp.server.integrations.system;

import com.mcp.server.core.IntegrationAdapter;
import com.mcp.server.core.McpTool;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class SystemIntegration extends IntegrationAdapter {

    private static final Logger log = Logger.getLogger(SystemIntegration.class.getName());

    private static final Set<String> ALLOWED_COMMANDS = Set.of(
            "ls", "dir", "pwd", "date", "whoami", "echo",
            "git", "docker", "mvn", "java", "node", "npm"
    );

    public SystemIntegration() {
        super("system");
    }

    @McpTool(
            name = "execute_command",
            description = "Execute safe system commands (whitelist: ls, dir, git, docker, etc.)",
            parameters = {"command"}
    )
    public String executeCommand(String command) {
        String baseCommand = command.trim().split("\\s+")[0];

        if (!ALLOWED_COMMANDS.contains(baseCommand)) {
            return "Error: Command not allowed. Allowed commands: " + ALLOWED_COMMANDS;
        }

        log.info("Executing command: " + command);

        try {
            ProcessBuilder pb = new ProcessBuilder();

            // Handle Windows vs Unix
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                pb.command("cmd.exe", "/c", command);
            } else {
                pb.command("bash", "-c", command);
            }

            pb.redirectErrorStream(true);
            Process process = pb.start();

            boolean finished = process.waitFor(10, TimeUnit.SECONDS);
            if (!finished) {
                process.destroy();
                return "Error: Command timeout (10 seconds)";
            }

            String output = new BufferedReader(new InputStreamReader(process.getInputStream()))
                    .lines()
                    .collect(Collectors.joining("\n"));

            return output.isEmpty() ? "Command executed successfully (no output)" : output;

        } catch (Exception e) {
            log.severe("Command execution failed: " + e.getMessage());
            return "Error: " + e.getMessage();
        }
    }

    @McpTool(
            name = "get_system_info",
            description = "Get system information (OS, Java version, user, etc.)",
            parameters = {}
    )
    public Map<String, String> getSystemInfo() {
        return Map.of(
                "os_name", System.getProperty("os.name"),
                "os_version", System.getProperty("os.version"),
                "os_arch", System.getProperty("os.arch"),
                "java_version", System.getProperty("java.version"),
                "user_name", System.getProperty("user.name"),
                "user_home", System.getProperty("user.home"),
                "current_dir", System.getProperty("user.dir")
        );
    }

    @Override
    public boolean healthCheck() {
        return true;
    }

    @Override
    public Map<String, Object> getMetadata() {
        return Map.of(
                "type", "system",
                "description", "Execute safe system commands",
                "allowed_commands", ALLOWED_COMMANDS
        );
    }
}