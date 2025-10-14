package AlexeyPG.bots.M3.features;

import AlexeyPG.bots.M3.dataManager;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;

import java.util.ArrayList;
import java.util.Collections;


public class autoReact {
    public static ArrayList<String> autoRoleChannels;
    public static ArrayList<String> autoReactUsers;

    public static void react(Message message){
        try {
            for (String channel : autoReact.autoRoleChannels) {
                if (channel.equals(message.getChannelId())) {
                    String[] reactions = dataManager.getData("features/autoreact/reactionMappings", message.getChannelId()).split(";");
                    for (String reaction : reactions) {
                        if(!reaction.isEmpty()) message.addReaction(Emoji.fromFormatted(reaction)).queue();
                    }
                    break;
                }
            }
            for(String user : autoReact.autoReactUsers){
                if(user.equals(message.getAuthor().getId())){
                    String[] reactions = dataManager.getData("features/autoreact/reactionMappings", user).split(";");
                    for (String reaction : reactions) {
                        if(!reaction.isEmpty()) message.addReaction(Emoji.fromFormatted(reaction)).queue();
                    }
                    break;
                }
            }
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
    public static void loadChannels(){
        try {
            autoReact.autoRoleChannels = new ArrayList<>();
            Collections.addAll(autoReact.autoRoleChannels, dataManager.getData("features/autoreact/trigger", "channels").split(";"));
            System.out.println("Autoreact channels loaded");
        } catch (Exception e){
            System.out.println("Error loading autoreact channels");
            autoReact.autoRoleChannels = new ArrayList<>();
        }
    }
    public static void addChannel(String channelId, String emoji){
        dataManager.saveData("features/autoreact/trigger","channels",dataManager.getData("features/autoreact/trigger","channels") + ";" + channelId);
        dataManager.saveData("features/autoreact/reactionMappings",channelId,emoji);
        loadChannels();

    }
    public static boolean removeChannel(String channelId){
        try{
            dataManager.saveData("features/autoreact/trigger","channels", dataManager.getData("features/autoreact/trigger","channels").replace(";" + channelId,""));
            loadChannels();
            return true;
        } catch (Exception e){
            return false;
        }
    }

    public static void loadUsers(){
        try {
            autoReact.autoReactUsers = new ArrayList<>();
            Collections.addAll(autoReact.autoReactUsers, dataManager.getData("features/autoreact/trigger", "users").split(";"));
            System.out.println("Autoreact users loaded");
        } catch (Exception e){
            System.out.println("Error loading autoreact users");
            autoReact.autoReactUsers = new ArrayList<>();
        }
    }
    public static void addUser(String userId, String emoji){
        dataManager.saveData("features/autoreact/trigger","users",dataManager.getData("features/autoreact/trigger","users") + ";" + userId);
        dataManager.saveData("features/autoreact/reactionMappings",userId,emoji);
        loadUsers();
    }
    public static boolean removeUser(String userId){
        try{
            dataManager.saveData("features/autoreact/trigger","users", dataManager.getData("features/autoreact/trigger","users").replace(";" + userId,""));
            loadUsers();
            return true;
        } catch (Exception e){
            return false;
        }
    }
}
