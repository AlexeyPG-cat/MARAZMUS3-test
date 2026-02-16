package AlexeyPG.bots.M3;

import AlexeyPG.bots.M3.features.autoReact;
import AlexeyPG.bots.M3.features.channelLink;
import AlexeyPG.bots.M3.interactions.interactionHandler;
import AlexeyPG.bots.M3.interactions.roleMasterButton;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GenericGuildVoiceEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static AlexeyPG.bots.M3.features.voiceNotifier.handleVoiceConnection;

public class Bot extends ListenerAdapter {
    @Override
    public void onReady(@NotNull ReadyEvent event) {
        config.loadAdminLists();
        autoReact.loadChannels();
        autoReact.loadUsers();
        interactionHandler.updateCommands();
        channelLink.loadGroups();
        if(Main.debug_mode) return;
    }
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        autoReact.react(event.getMessage());
        handleIncomingMessage.handle(event);
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event){
        if(Main.debug_mode) return;
        sendWelcomeMessage(event);
    }
    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        roleMasterButton.start(event);
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        interactionHandler.slashCommand(event);
    }

    private static void sendWelcomeMessage(GuildMemberJoinEvent event){
        String channel = dataManager.getServerData(event.getGuild(),"selectedChannels","welcome");
        if(channel != null){
            Main.jda.getTextChannelById(channel).sendMessage("Приветствуем <@" + event.getUser().getId() + ">").setAllowedMentions(List.of()).queue((message -> {
                message.addReaction(Emoji.fromFormatted("👋")).queue();
            }));
        }
    }

    @Override
    public void onGenericGuildVoice(@NotNull GenericGuildVoiceEvent event){
        handleVoiceConnection(event);
    }
}
