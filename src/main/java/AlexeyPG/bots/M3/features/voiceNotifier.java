package AlexeyPG.bots.M3.features;

import AlexeyPG.bots.M3.Main;
import AlexeyPG.bots.M3.dataManager;
import net.dv8tion.jda.api.events.guild.voice.GenericGuildVoiceEvent;

import java.util.ArrayList;

public class voiceNotifier {
    public static ArrayList<String> watchedChannels;


    public static void loadWatchers(){
        try{
            watchedChannels = new ArrayList<>();
            String data = dataManager.getData("features/voiceNotifier/watchers","");
            if(data != null){
                watchedChannels.addAll(data.lines().toList());
                System.out.println("Voice watcher list loaded");
            } else {
                watchedChannels = new ArrayList<>();
                System.out.println("No watched voice channels, skipping");
            }
        } catch (Exception e){
            System.out.println("Error loading voice watchers");
            System.out.println(e.getMessage());
        }
    }
    public static void addWatcher(String chanelId, String UserId){
        String watchers = dataManager.getData("features/voiceNotifier/watchers",chanelId);
        if(!watchers.contains(UserId))
            dataManager.saveData("features/voiceNotifier/watchers",chanelId,watchers+UserId+",");
        loadWatchers();
    }
    public static void removeWatcher(String chanelId, String UserId){
        String availableData = dataManager.getData("features/voiceNotifier/watchers",chanelId);
        if(availableData != null) {
            dataManager.saveData("features/voiceNotifier/watchers", chanelId, availableData.replace(UserId + ",",""));
            if(availableData.replace(UserId + ",","").isEmpty()){
                dataManager.deleteData("features/voiceNotifier/watchers",chanelId);
            }
        }
        dataManager.deleteData("data/users/" + UserId + "/apps/voiceNotifier/savedChannels", chanelId);
        loadWatchers();
    }
    public static void updateUserWatchedChannel(String UserId, String channelId, String data){
        dataManager.saveUserData(UserId, "apps/voiceNotifier/savedChannels",channelId, data);
    }
    public static void handleVoiceConnection(GenericGuildVoiceEvent event){
        if(event.getVoiceState().getChannel() != null) if(watchedChannels!=null) for(String dataLine : watchedChannels){
            if(dataLine.contains(event.getVoiceState().getChannel().getId())){
                for(String userId : dataLine.replace(event.getVoiceState().getChannel().getId() + ":","").split(",")){
                    if(!userId.isEmpty()){
                        String watchedList = dataManager.getUserData(userId,"apps/voiceNotifier/savedChannels",event.getVoiceState().getChannel().getId());
                        if(!watchedList.isEmpty()) if(watchedList.contains(event.getMember().getId())){
                            try {
                                Main.jda.getUserById(userId).openPrivateChannel().flatMap(channel -> channel.sendMessage("<@" + event.getMember().getId() + "> joined <#" + event.getVoiceState().getChannel().getId() + ">")).queue();
                            } catch (Exception e){
                                System.out.println("Unable to send DM");
                            }
                        } else if(watchedList.contains("all")){
                            try {
                                Main.jda.getUserById(userId).openPrivateChannel().flatMap(channel -> channel.sendMessage("Someone joined <#" + event.getVoiceState().getChannel().getId() + ">")).queue();
                            } catch (Exception e){
                                System.out.println("Unable to send DM");
                            }
                        }
                    }
                }
            }
        }
    }
}
