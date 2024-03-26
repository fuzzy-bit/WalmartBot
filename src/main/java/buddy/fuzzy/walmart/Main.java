package buddy.fuzzy.walmart;

import buddy.fuzzy.walmart.misskey.MisskeyClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kaktushose.jda.commands.JDACommands;
import com.github.sarxos.webcam.Webcam;
import misskey4j.MisskeyFactory;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static MisskeyClient misskeyClient;
    public static JDA discordApi;
    public static JsonNode config;

    /**
     * Method to get our config file contents
     * @param file the fucking json file
     * */
    public static JsonNode readJsonFile(File file) throws IOException {
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        StringBuilder configContents = new StringBuilder();

        String currentLine;

        while ((currentLine = bufferedReader.readLine()) != null) {
            configContents.append(currentLine);
        }

        bufferedReader.close();

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readTree(configContents.toString());
    }

    /**
     * reads a file (why is this not streamlined)
     * @param fileName are you stupid
     */
    public static String readFile(String fileName) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
        StringBuilder stringBuilder = new StringBuilder();
        String line;

        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append(System.lineSeparator());
        }

        bufferedReader.close();
        return stringBuilder.toString();
    }

    /**
     * The entry point
     * @param args arguments (none)
     */
    public static void main(String[] args) {
        File configFile = new File("./Config.json");
        File emojiFile = new File("emoji.json");

        if (configFile.exists() && emojiFile.exists()) {
            logger.info("Loading config file...");

            try {
                config = readJsonFile(configFile);
                discordApi = JDABuilder.createDefault(config.get("DISCORD_TOKEN").asText())
                        .setStatus(OnlineStatus.ONLINE)
                        .setActivity(Activity.playing("fishman"))
                        .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_PRESENCES)
                        .build();

                misskeyClient = new MisskeyClient(MisskeyFactory.getInstanceWithOwnedAccessToken(
                        config.get("MISSKEY_HOST").asText(),
                        config.get("MISSKEY_TOKEN").asText()
                ));

                JDACommands.start(discordApi, Main.class, "buddy.fuzzy.walmart.discord.commands");

                CaptureService captureService = new CaptureService();
                captureService.start();
            } catch (Exception exception) {
                logger.error(exception.getMessage());
            }
        } else {
            logger.error("No config.json or emoji.json found!");
            System.exit(1);
        }
    }
}