package AlexeyPG.bots.M3;

import AlexeyPG.bots.M3.features.autoReact;
import AlexeyPG.bots.M3.features.channelLink;
import AlexeyPG.bots.M3.features.voiceNotifier;
import AlexeyPG.bots.M3.interactions.roleMasterButton;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

public class handleIncomingMessage {
    public static void handle(MessageReceivedEvent event){
        Mentions mentions = event.getMessage().getMentions();
        if(mentions.getUsers().contains(Main.jda.getSelfUser())){
            if(event.getMessage().getContentRaw().startsWith("<@" + Main.jda.getSelfUser().getId() + ">")){
                handleDirectTextCommand(event.getMessage().getContentRaw().substring(("<@" + Main.jda.getSelfUser().getId() + ">").length()),event);
            }
        } else {
            if(!event.getMessage().getAuthor().isBot()) if(!event.getMessage().getAuthor().isSystem()) channelLink.handleMessage(event.getMessage());
        }
    }

    private static void handleDirectTextCommand(String input,MessageReceivedEvent event){
        List<String> seq = new ArrayList<>(prepare(input));
        if(seq.isEmpty()) return;
        switch (seq.get(0).toLowerCase()){
            case "help":
                if(Main.debug_mode) break;
                event.getMessage().reply(infos.help()).queue();
                break;
            case "reloadconfigs":
                if(Main.debug_mode) break;
                config.addMissingConfigs();
                event.getMessage().reply(config.loadConfig()).queue();
                autoReact.loadUsers();
                autoReact.loadChannels();
                voiceNotifier.loadWatchers();
                break;
            case "stop":
                if(Main.debug_mode) break;
                if(!event.getAuthor().getId().equals("665566492158590976")) break;
                event.getMessage().reply("All responses disconnected").queue();
                Main.debug_mode = true;
                break;
            case "start":
                if(!event.getAuthor().getId().equals("665566492158590976")) break;
                if(Main.debug_mode){
                    Main.debug_mode = false;
                    event.getMessage().reply("Reloading\n" + config.loadConfig() + "\n Bot instance active").queue();
                } else {event.getMessage().reply("Bot instance already active").queue();}
                break;
            case "record":
                if(!event.getAuthor().getId().equals("665566492158590976")) break;
                roleMasterButton.swRecord();
                event.getMessage().reply("Record = " + roleMasterButton.record).queue();
                break;
            case "status":
                replyStatus(event);
                break;
            case "setwelcomechannel":
                if(Main.debug_mode) break;
                if(!event.getAuthor().getId().equals("665566492158590976")) break;
                dataManager.saveServerData(event.getGuild(),"selectedChannels","welcome",event.getChannel().getId());
                event.getMessage().reply("welcome at `" + event.getGuild().getId() + "` set to <#" + dataManager.getServerData(event.getGuild(),"selectedChannels","welcome") + ">").queue();
                break;
            case "setautoreactchannel":
                if(!event.getAuthor().getId().equals("665566492158590976")) break;
                if(Main.jda.getGuildChannelById(seq.get(1)) != null){
                    autoReact.addChannel(seq.get(1), seq.get(2));
                } else {
                    autoReact.addChannel(event.getChannel().getId(), seq.get(1));
                }
                event.getMessage().reply("ok").queue();
                break;
            case "removeautoreactchannel":
                if(!event.getAuthor().getId().equals("665566492158590976")) break;
                if(seq.size()>1){
                    event.getMessage().reply(autoReact.removeChannel(seq.get(1)) ? "ok" : "fail").queue();
                } else {
                    event.getMessage().reply(autoReact.removeChannel(event.getChannel().getId()) ? "ok" : "fail").queue();
                }
                break;
            case "setautoreactuser":
                if(!event.getAuthor().getId().equals("665566492158590976")) break;
                autoReact.addUser(seq.get(1), seq.get(2));
                event.getMessage().reply("ok").queue();
                break;
            case "removeautoreactuser":
                if(!event.getAuthor().getId().equals("665566492158590976")) break;
                event.getMessage().reply(autoReact.removeUser(seq.get(1)) ? "ok" : "fail").queue();
                break;
            case "pingall":

                event.getMessage().reply("@everyone").setAllowedMentions(List.of()).queue();
                break;
            case "pinghere":

                event.getMessage().reply("@here").setAllowedMentions(List.of()).queue();

                break;
//            case "watchchannelfor":
//                if(seq.get(1) != null && seq.get(2) != null){
//                    voiceNotifier.addWatcher(seq.get(1), event.getAuthor().getId());
//                    voiceNotifier.updateUserWatchedChannel(event.getAuthor().getId(),seq.get(1), seq.get(2));
//                    event.getMessage().reply("Now watching <#" + seq.get(1) + "> for " + seq.get(2)).queue();
//                }
//                break;
//            case "stopwatchingchannel":
//                if(seq.get(1) !=null){
//                    voiceNotifier.removeWatcher(seq.get(1), event.getAuthor().getId());
//                    event.getMessage().reply("Ok").queue();
//                }
//                break;
            case "link":
                if(!event.getAuthor().getId().equals("665566492158590976")) break;
                if(seq.get(1) != null){
                    try{
                        channelLink.link(event.getMessage().getChannel().getIdLong(),Long.parseLong(seq.get(1)));
                        event.getMessage().reply("OK").queue();
                    } catch (Exception e){
                        event.getMessage().reply("Failed to link").queue();
                    }
                }
                break;
            case "unlink":
                if(!event.getAuthor().getId().equals("665566492158590976")) break;
                channelLink.unlink(event.getChannel().getIdLong());
                event.getMessage().reply("OK").queue();
                break;
        }
    }

    private static List<String> prepare(String in){
        List<String> data = new ArrayList<>(List.of(in.split(" ")));
        data.removeIf(String::isEmpty);
        for(String str : data){
            System.out.println(str);
        }
        return data;
    }

    private static void replyStatus(MessageReceivedEvent event){
        event.getMessage().reply("MARAZMUS 3 v" + Main.version + "\nG PING: " + Main.jda.getGatewayPing() + "\n" + Main.jda.getSelfUser().getId() + "\n" + event.getGuild().getName()).queue();
    }
}
