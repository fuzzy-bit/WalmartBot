package buddy.fuzzy.walmart.discord.commands;

import com.github.kaktushose.jda.commands.annotations.interactions.Interaction;
import com.github.kaktushose.jda.commands.annotations.interactions.Permissions;
import com.github.kaktushose.jda.commands.annotations.interactions.SlashCommand;
import com.github.kaktushose.jda.commands.dispatching.interactions.commands.CommandEvent;
import misskey4j.api.response.notes.NotesCreateResponse;
import misskey4j.entity.share.Response;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static buddy.fuzzy.walmart.Main.config;
import static buddy.fuzzy.walmart.Main.misskeyClient;
import static buddy.fuzzy.walmart.discord.Util.findRole;

@Interaction
public class Capture {
    @SlashCommand(value = "capture", desc = "Take a photo of the retail laptop")
    public void onCommand(CommandEvent event) {
        try {
            if (findRole(Objects.requireNonNull(event.getMember()), config.get("DISCORD_ROLE_ID").asText()) == null) {
                event.editReply(true).reply(new EmbedBuilder()
                        .setTitle("Camera")
                        .setDescription("You're not authorized to capture!")
                        .setColor(0xeb2323)
                );

                return;
            }

            event.reply(new EmbedBuilder()
                    .setTitle("Camera")
                    .setDescription("Capturing, give me a moment...")
                    .setColor(0x237feb)
            );

            Response<NotesCreateResponse> misskeyResponse = misskeyClient.makeCaptureNote();
            String noteUrl = misskeyClient.getNoteUrl(misskeyResponse.get().getCreatedNote().getId());

            event.editReply(true).reply(new EmbedBuilder()
                    .setTitle("Camera")
                    .setUrl(noteUrl)
                    .setDescription(noteUrl)
                    .setThumbnail(misskeyResponse.get().getCreatedNote().getFiles().get(0).getUrl())
                    .setColor(0x51eb23)
            );
        } catch (IOException exception) {
            event.editReply(true).reply(new EmbedBuilder()
                    .setTitle("Camera")
                    .setDescription("Failed to capture!")
                    .setColor(0xeb2323)
            );
        }
    }
}