package AlexeyPG.bots.M3.interactions;

import AlexeyPG.bots.M3.Main;
import AlexeyPG.bots.M3.config;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import java.util.List;
import java.util.Objects;

public class roleMasterButton {
    public static boolean record = false;
    static String data = "";
    public static void start(ButtonInteractionEvent event){
        System.out.println(event.getComponentId().substring(8));
        data += event.getComponentId().substring(8) + ",";
        if(config.get("Player roles").contains(event.getComponentId().substring(8))){
            System.out.println("Role confirmed");
            Role role = Main.jda.getRoleById(event.getComponentId().substring(8));
            Member member = event.getMember();
            if(member != null && role != null){
                if(event.getComponentId().contains("roleadd")){
                    if(!member.getRoles().contains(role)){
                        Objects.requireNonNull(event.getGuild()).addRoleToMember(member,role).queue();
                        Main.jda.getTextChannelById(config.get("Game message channel")).sendMessage("<@" + event.getUser().getId() + "> теперь ищет в **" + role.getName() + "**").setSuppressedNotifications(true).setAllowedMentions(List.of()).queue();
                        event.reply("Выдаю роль <@&" + role.getId() + ">").setEphemeral(true).setSuppressedNotifications(true).setAllowedMentions(List.of()).queue();
                    } else {
                        event.reply("Роль <@&" + role.getId() + "> уже выдана").setEphemeral(true).setSuppressedNotifications(true).setAllowedMentions(List.of()).queue();
                    }
                } else {
                    if(member.getRoles().contains(role)){
                        Objects.requireNonNull(event.getGuild()).removeRoleFromMember(member,role).queue();
                        Main.jda.getTextChannelById(config.get("Game message channel")).sendMessage("<@" + event.getUser().getId() + "> перестал искать в **" + role.getName() + "**").setSuppressedNotifications(true).setAllowedMentions(List.of()).queue();
                        event.reply("Забираю роль <@&" + role.getId() + ">").setEphemeral(true).setSuppressedNotifications(true).setAllowedMentions(List.of()).queue();
                    } else {
                        event.reply("У вас нет роли <@&" + role.getId() + ">").setEphemeral(true).setSuppressedNotifications(true).setAllowedMentions(List.of()).queue();
                    }
                }
            } else {
                System.out.println("Role " + event.getComponentId().substring(8) + " not found on server");
            }
        } else {
            System.out.println("Role " + event.getComponentId().substring(8) + " not found in config");
        }
    }
    public static void swRecord(){
        roleMasterButton.record = !roleMasterButton.record;
        if(record){
            data = "";
        } else {
            System.out.println(data);
        }
    }
}
