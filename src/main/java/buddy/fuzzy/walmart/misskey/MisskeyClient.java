package buddy.fuzzy.walmart.misskey;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sarxos.webcam.Webcam;
import misskey4j.Misskey;
import misskey4j.api.request.files.FilesCreateRequest;
import misskey4j.api.request.notes.NotesCreateRequest;
import misskey4j.api.response.files.FilesCreateResponse;
import misskey4j.api.response.notes.NotesCreateResponse;
import misskey4j.entity.share.Response;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static buddy.fuzzy.walmart.Main.*;

public record MisskeyClient(Misskey misskey) {
    private static final Random random = new Random();
    private static final Logger logger = LogManager.getLogger(MisskeyClient.class);

    public String captureAndUpload() throws IOException {
        Webcam webcam = Webcam.getDefault();
        webcam.open();
        ImageIO.write(webcam.getImage(), "PNG", new File("lastPhoto.png"));

        Response<FilesCreateResponse> response = misskey.files().create(
                FilesCreateRequest
                        .builder()
                        .name(UUID.randomUUID().toString())
                        .file(new File("lastPhoto.png"))
                        .build()
        );

        return response.get().getId();
    }

    public Response<NotesCreateResponse> makeCaptureNote() throws IOException {
        String emojiJson = readFile("emoji.json");
        ObjectMapper mapper = new ObjectMapper();
        List<?> emojiStrings = mapper.readValue(emojiJson, List.class);

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("E, MMMM F yyyy 'at' HH:mm:ss", new Locale("en_US"));

        String fileId = this.captureAndUpload();
        ArrayList<String> fileIds = new ArrayList<>();
        fileIds.add(fileId);

        Response<NotesCreateResponse> misskeyResponse = misskey.notes().create(
                NotesCreateRequest
                        .builder()
                        .text(String.format(
                                "New capture on %s! :%s:",
                                dateFormat.format(date),
                                emojiStrings.get(random.nextInt(emojiStrings.size()))
                        ))
                        .fileIds(fileIds)
                        .build()
        );

        try {
            TextChannel channel = discordApi.getTextChannelById(config.get("DISCORD_CHANNEL_ID").asText());

            // noinspection DataFlowIssue
            channel.sendMessage(getNoteUrl(misskeyResponse.get().getCreatedNote().getId())).queue();
        } catch (Exception exception) {
            logger.error(exception.getMessage());
        }

        return misskeyResponse;
    }

    public String getNoteUrl(String noteId) {
        return "https://" + misskeyClient.misskey.getHost() + "/notes/" + noteId;
    }
}
