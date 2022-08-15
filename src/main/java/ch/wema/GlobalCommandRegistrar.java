package ch.wema;

import discord4j.common.JacksonResources;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class GlobalCommandRegistrar {
    private static final String commandsFolderName = "commands/";
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final RestClient restClient;

    public GlobalCommandRegistrar(RestClient restClient) {
        this.restClient = restClient;
    }

    private static List<String> getCommandsJson(List<String> fileNames) throws IOException {
        var url = GlobalCommandRegistrar.class.getClassLoader().getResource(commandsFolderName);
        Objects.requireNonNull(url, commandsFolderName + " could not be found");

        List<String> list = new ArrayList<>();
        for (String file : fileNames) {
            String resourceFileAsString = getResourceFileAsString(commandsFolderName + file);
            list.add(Objects.requireNonNull(resourceFileAsString, "Command file not found: " + file));
        }
        return list;
    }

    private static String getResourceFileAsString(String fileName) throws IOException {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        try (InputStream resourceAsStream = classLoader.getResourceAsStream(fileName)) {
            if (resourceAsStream == null) return null;
            try (InputStreamReader inputStreamReader = new InputStreamReader(resourceAsStream);
                 BufferedReader reader = new BufferedReader(inputStreamReader)) {
                return reader.lines().collect(Collectors.joining(System.lineSeparator()));
            }
        }
    }

    protected void registerCommands(List<String> fileNames) throws IOException {
        final JacksonResources d4jMapper = JacksonResources.create();

        final var applicationService = restClient.getApplicationService();
        final var applicationId = restClient.getApplicationId().block();

        List<ApplicationCommandRequest> commands = new ArrayList<>();
        for (String json : getCommandsJson(fileNames)) {
            var request = d4jMapper.getObjectMapper().readValue(json, ApplicationCommandRequest.class);

            commands.add(request);
        }

        applicationService.bulkOverwriteGlobalApplicationCommand(applicationId, commands)
                .doOnNext(cmd -> LOGGER.debug("Successfully registered Global Command " + cmd.name()))
                .doOnError(e -> LOGGER.error("Failed to register global commands", e))
                .subscribe();
    }
}
