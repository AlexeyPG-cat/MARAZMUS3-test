package AlexeyPG.bots.M3.features;

import AlexeyPG.bots.M3.dataManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.awt.*;

public class counter {
    public static void handle(SlashCommandInteractionEvent event){
        EmbedBuilder reply = null;
        boolean flag = false;
        if(event.getOption("name") != null){
            String name = event.getOption("name").getAsString();
            if(event.getOption("action") != null) {
                String action = event.getOption("action").getAsString();
                if(action.equals("get")) flag = true;
                if ("+-reset".contains(action)) {
                    int inValue = 0;
                    try {
                        inValue = Integer.parseInt(dataManager.getUserData(event.getUser(), "apps/counter/values", name));
                    } catch (Exception e) {/**/}
                    int actionValue = (action.equals("set")) ? 0 : 1;
                    if (event.getOption("amount") != null) {
                        try {
                            actionValue = Integer.parseInt(event.getOption("amount").getAsString());
                        } catch (Exception e) {/**/}
                    }
                    int outValue = switch (action) {
                        case "+" -> inValue + actionValue;
                        case "-" -> inValue - actionValue;
                        case "set" -> actionValue;
                        default -> 0;
                    };
                    dataManager.saveUserData(event.getUser(), "apps/counter/values", name, outValue + "");
                    String description = dataManager.getUserData(event.getUser(), "apps/counter/descriptions", name);
                    if (description.isEmpty()){
                        dataManager.saveUserData(event.getUser(), "apps/counter/descriptions", name, name + " =");
                        description = name + " =";
                    }
                    reply = new EmbedBuilder()
                            .setTitle("Counter")
                            .setColor(new Color(121, 2, 163))
                            .setDescription(description + " " + outValue + " (" + inValue + "->" + outValue + ")");
                } else if(action.equals("description")) {
                    if(event.getOption("amount") != null){
                        String value = event.getOption("amount").getAsString();
                        dataManager.saveUserData(event.getUser(), "apps/counter/descriptions", name,value);
                        reply = new EmbedBuilder()
                                .setTitle("Counter")
                                .setColor(new Color(121, 2, 163))
                                .setDescription("Description updated");
                    } else flag = true;
                }
            } else flag = true;
            if(flag){
                String data = dataManager.getUserData(event.getUser(), "apps/counter/values", name);
                String description = dataManager.getUserData(event.getUser(), "apps/counter/descriptions", name);
                if (description.isEmpty()) description = name + " =";
                if (data.isEmpty()) data = "0";
                reply = new EmbedBuilder()
                        .setTitle("Counter")
                        .setColor(new Color(121, 2, 163))
                        .setDescription(description + " " + data);
            }
        }
        if(reply!=null){
            event.getHook().sendMessage(MessageCreateData.fromEmbeds(reply.build())).queue();
        } else {
            event.getHook().sendMessage("Something went wrong").queue();
        }
    }
}
