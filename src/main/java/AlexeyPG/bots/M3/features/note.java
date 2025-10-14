package AlexeyPG.bots.M3.features;

import AlexeyPG.bots.M3.dataManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.List;

public class note {
    public static void handle(SlashCommandInteractionEvent event){
        switch (Objects.requireNonNull(event.getOption("action")).getAsString()){
            case "get":
                String result = get(event.getUser().getId(), event.getOption("name").getAsString());
                if(result == null) result = "";
                if(result.isEmpty()){
                    event.reply("Note not found").setEphemeral(true).queue();
                } else {
                    EmbedBuilder reply = new EmbedBuilder()
                            .setTitle("Note")
                            .setColor(new Color(121,2,163))
                            .setDescription(result);
                    event.reply(MessageCreateData.fromEmbeds(reply.build())).queue();
                }
                break;
            case "add":
                String result2 = get(event.getUser().getId(), event.getOption("name").getAsString());
                if(result2 == null) result2 = "";
                if(result2.isEmpty()){
                    if(event.getOption("text") != null) if(!event.getOption("text").getAsString().isEmpty()){
                        if(event.getOption("name").getAsString().contains(":")){
                            event.reply("Note name cannot contain `:` symbol").queue();
                            return;
                        }
                        create(event.getUser().getId(),event.getOption("name").getAsString(),event.getOption("text").getAsString());
                        event.reply("Noted.").queue();
                    } else {
                        event.reply("No text provided").setEphemeral(true).queue();
                    } else {
                        event.reply("No text provided").setEphemeral(true).queue();
                    }
                } else {
                    event.reply("Note with this name already exists").setEphemeral(true).queue();
                }
                break;
            case "edit":
                String result4 = get(event.getUser().getId(), event.getOption("name").getAsString());
                if(result4 == null) result4 = "";
                if(result4.isEmpty()){
                    event.reply("Note not found").setEphemeral(true).queue();
                } else {
                    if(event.getOption("text") != null) if(!event.getOption("text").getAsString().isEmpty()){
                        if(event.getOption("name").getAsString().contains(":")){
                            event.reply("Note name cannot contain `:` symbol").queue();
                            return;
                        }
                        create(event.getUser().getId(),event.getOption("name").getAsString(),event.getOption("text").getAsString());
                        event.reply("Note edited").queue();
                    } else {
                        event.reply("No text provided").setEphemeral(true).queue();
                    } else {
                        event.reply("No text provided").setEphemeral(true).queue();
                    }
                }
                break;
            case "delete":
                String result3 = get(event.getUser().getId(), event.getOption("name").getAsString());
                if(result3 == null) result3 = "";
                if(result3.isEmpty()){
                    event.reply("Note not found").setEphemeral(true).queue();
                } else {
                    create(event.getUser().getId(),event.getOption("name").getAsString(),"");
                    event.reply("Note deleted").queue();
                }
                break;
            case "list":
                String fileData = dataManager.getUserData(event.getUser().getId(),"apps/notes/saved","");
                if(fileData == null){
                    event.reply("You haven't made any notes yet").setEphemeral(true).queue();
                    return;
                }
                List<String> lines = new ArrayList<>();
                for(String line : fileData.split("\n")){
                    try {
                        boolean flag = false;
                        String[] data = line.split(":");
                        if (data.length > 2) flag = true;
                        if (data.length > 1) if (!data[1].isEmpty()) flag = true;
                        if (flag) lines.add(data[0]);
                    } catch (Exception e){System.out.println(e.getMessage());}
                }
                StringBuilder listed = new StringBuilder();
                int page = getValidPage(event.getOption("name").getAsString(),lines.size()/10+1);
                for(int i = 0; i<10; i++){
                    if(lines.size()>i+(10*(page-1))) listed.append("\n").append(lines.get(i+(10*(page-1))));
                }
                if(lines.size()>9) listed.append("\nPage ").append(page).append("/").append(lines.size()/10+1);

                EmbedBuilder reply = new EmbedBuilder()
                        .setTitle("Note list")
                        .setColor(new Color(121,2,163))
                        .setDescription(listed.toString())
                        .setFooter("Use \"/note list <page>\" to see more");
                event.reply(MessageCreateData.fromEmbeds(reply.build())).queue();
                break;
        }
    }
    public static int getValidPage(String input, int maxPages){
        try {
            return Math.max(Math.min(Integer.parseInt(input),maxPages),1);
        } catch (Exception e) {
            return 1;
        }
    }
    public static void create(String userId, String name, String data){
        dataManager.saveUserData(userId,"apps/notes/saved", name, data);
            }

    public static String get(String userId, String name){
        return dataManager.getUserData(userId,"apps/notes/saved", name);
    }
}
