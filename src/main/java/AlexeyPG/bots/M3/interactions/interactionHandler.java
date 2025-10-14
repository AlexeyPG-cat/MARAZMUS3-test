package AlexeyPG.bots.M3.interactions;

import AlexeyPG.bots.M3.Main;
import AlexeyPG.bots.M3.features.counter;
import AlexeyPG.bots.M3.features.note;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.entities.sticker.GuildSticker;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.IntegrationType;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public class interactionHandler {
    public static void updateCommands(){
        Main.jda.updateCommands().addCommands(
                Commands.slash("test2","dont")
                        .setIntegrationTypes(IntegrationType.USER_INSTALL)
                        .setContexts(InteractionContextType.ALL),
                Commands.slash("react","Reply with foreign reaction cuz im poor")
                        .setIntegrationTypes(IntegrationType.USER_INSTALL)
                        .setContexts(InteractionContextType.ALL)
                        .addOptions(
                                new OptionData(OptionType.STRING,"reaction","idk how i'll implement it yet")
                                        .setRequired(true),
                                new OptionData(OptionType.NUMBER,"server","Specify server to take emoji from")
                        ),
                Commands.slash("note","Work with prepared text")
                        .setIntegrationTypes(IntegrationType.USER_INSTALL)
                        .setContexts(InteractionContextType.ALL)
                        .addOptions(
                                new OptionData(OptionType.STRING, "action", "action")
                                        .setRequired(true)
                                        .addChoice("get","get")
                                        .addChoice("add","add")
                                        .addChoice("delete","delete")
                                        .addChoice("edit","edit")
                                        .addChoice("list","list"),
                                new OptionData(OptionType.STRING, "name", "file name / list file page")
                                        .setRequired(true),
                                new OptionData(OptionType.STRING, "text","text to save")
                                        .setRequired(false)
                        ),
                Commands.slash("softping","Soft but not fluffy :(")
                        .setIntegrationTypes(IntegrationType.USER_INSTALL)
                        .setContexts(InteractionContextType.ALL)
                        .addOptions(
                                new OptionData(OptionType.USER, "user", "Who to not ping")
                                        .setRequired(true)
                        ),
                Commands.slash("count","Yeah, just count")
                        .setIntegrationTypes(IntegrationType.USER_INSTALL)
                        .setContexts(InteractionContextType.ALL)
                        .addOptions(
                                new OptionData(OptionType.STRING,"name","Counter name")
                                        .setRequired(true),
                                new OptionData(OptionType.STRING,"action","action")
                                        .addChoice("+","+")
                                        .addChoice("-","-")
                                        .addChoice("set","set")
                                        .addChoice("reset","reset")
                                        .addChoice("get","get")
                                        .addChoice("description", "description"),
                                new OptionData(OptionType.STRING,"amount","not required")
                        )
        ).queue();
    }

    public static void slashCommand(SlashCommandInteractionEvent event){
        if(event.getName().equals("note")) {
            note.handle(event);
        }
        if (event.getName().equals("test2")) {
            String message = "\"We're in \\\\\" + event.getChannel().getAsMention()";
            message += "\n" + ((PrivateChannel)event.getMessageChannel()).getUser().getId();
            event.reply(message).queue();
        }
        if(event.getName().equals("react")){
            String reaction = event.getOption("reaction").getAsString();
            reaction = reaction.replace(":","");
            boolean done = false;
            if(event.getOption("type") == null){
                for(RichCustomEmoji emoji : Main.jda.getEmojis()){
                    if(emoji.getName().equals(reaction)){
                        event.reply(emoji.getImageUrl()).queue();
                        done = true;
                        break;
                    }
                }
                if(!done) for(Guild guild : Main.jda.getGuilds()){
                    for(GuildSticker sticker : guild.getStickers()){
                        if(sticker.getName().equals(reaction)){
                            event.reply(sticker.getIconUrl()).queue();
                            done = true;
                            break;
                        }
                    }
                }
            }


            //
            //event.getOption("reaction").getAsString()
            //<:KyuLurk:1412064337647894528>
        }
        if (event.getName().equals("softping")) {
            event.deferReply().queue();
            System.out.println(event.getTimeCreated());
            User user =  event.getOption("user").getAsUser();
            event.getHook().sendMessage(user.getAsMention()).setAllowedMentions(List.of()).queue();
        }
        if(event.getName().equals("count")){
            event.deferReply().queue();
            counter.handle(event);
        }
    }
}
