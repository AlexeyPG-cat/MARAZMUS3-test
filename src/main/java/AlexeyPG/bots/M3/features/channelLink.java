package AlexeyPG.bots.M3.features;

import AlexeyPG.bots.M3.Main;
import AlexeyPG.bots.M3.dataManager;
import jakarta.annotation.Nullable;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;

import java.util.*;

public class channelLink {
    private static Map<Long, List<Long>> groups = new HashMap<>();

    public static void loadGroups(){loadGroups(true);}
    private static void loadGroups(boolean log){
        int counterG = 0;
        int counterC = 0;
        groups.clear();
        for(String line : dataManager.getDataListed("features/channelLink/groups")){
            if(line.isEmpty()) return;
            counterG++;
            long key = Long.parseLong(line.split(":")[0]);
            List<Long> values = new ArrayList<>();
            for(String data : line.split(":")){
                values.add(Long.parseLong(data));
                counterC++;
            }
            values.remove(0);
            groups.put(key,values);
        }
        if(log){
            System.out.println();
            System.out.println("Channel link loaded " + counterG + " groups with total " + counterC + " channels");
        }
    }

    public static void handleMessage(Message message){
        Long origChannel = message.getChannel().getIdLong();
        Long group = getGroupByChannel(origChannel);
        if(group != null){
            for(Long channel : Objects.requireNonNull(getChannels(group))){
                if(!Objects.equals(channel, origChannel)){
                    List<Webhook> webhooks = Objects.requireNonNull(Main.jda.getChannelById(TextChannel.class, channel)).retrieveWebhooks().complete();
                    if(webhooks.isEmpty()){
                        message.getChannel().asTextChannel().createWebhook("channelLink").queue();
                    }
                    WebhookMessageCreateAction<Message> msg = webhooks.get(0).sendMessage("a");
                    message.getGuild().retrieveMember(message.getAuthor()).queue(member -> {
                        if(member.getNickname() != null) msg.setUsername(member.getNickname());
                        else msg.setUsername(message.getAuthor().getEffectiveName());
                        if(member.getAvatarUrl() != null) msg.setAvatarUrl(member.getAvatarUrl());
                        else if(message.getAuthor().getAvatarUrl() != null) msg.setAvatarUrl(message.getAuthor().getAvatarUrl());
//                    msg.addComponents(message.getComponents());
//                    msg.addEmbeds(message.getEmbeds());
                        msg.applyMessage(message);
                        msg.queue();
                    });
                }
            }
        }
    }

    @Nullable
    public static Long getGroupByChannel(Long channelId){
        for(Map.Entry<Long,List<Long>> group : groups.entrySet()){
            if(group.getValue().contains(channelId)) return group.getKey();
        }
        return null;
    }

    @Nullable
    public static List<Long> getChannels(Long group){
        return groups.getOrDefault(group,null);
    }

    public static void link(Long channel1, Long channel2){
        TextChannel c1c = Main.jda.getChannelById(TextChannel.class,channel1);
        TextChannel c2c = Main.jda.getChannelById(TextChannel.class,channel2);
        List<Webhook> webhooks = c1c.retrieveWebhooks().complete();
        if(webhooks.isEmpty()){
            c1c.createWebhook("channelLink").queue();
        }
        webhooks = c2c.retrieveWebhooks().complete();
        if(webhooks.isEmpty()){
            c2c.createWebhook("channelLink").queue();
        }
        Long c1g = getGroupByChannel(channel1);
        Long c2g = getGroupByChannel(channel2);
        if(c1g != null && c2g != null){
            if(!Objects.equals(c1g, c2g)){
                mergeGroups(c1g,c2g);
            }
        } else {
            if(c1g == null && c2g == null){
                createGroup(channel1,channel2);
            } else {
                Long g = c1g!=null?c1g:c2g;
                Long c = c1g!=null?channel2:channel1;
                addChannel(g,c);
            }
        }
        StringBuilder channels = new StringBuilder();
        boolean first = true;
        for(Long channel : Objects.requireNonNull(getChannels(getGroupByChannel(channel1)))){
            if(first){
                channels.append("<#").append(channel.toString()).append(">");
                first = false;
            } else {
                channels.append(", <#").append(channel.toString()).append(">");
            }
        }
        for(Long channel : Objects.requireNonNull(getChannels(getGroupByChannel(channel1)))){
            Main.jda.getChannelById(TextChannel.class, channel).sendMessage("Linked channels list updated:" + channels.toString()).queue();
        }
    }

    public static void mergeGroups(Long... groups){
        List<Long> channels = new ArrayList<>();
        for(Long group : groups){
            channels.addAll(Objects.requireNonNull(getChannels(group)));
            deleteGroup(group);
        }
        System.out.println("Merged to new group: " + createGroup(channels.toArray(new Long[]{})));
    }
    public static void deleteGroup(Long group){
        dataManager.deleteData("features/channelLink/groups",group.toString());
        loadGroups(false);
    }
    public static Long createGroup(Long... channels){
        StringBuilder channelsSB = new StringBuilder();
        boolean first = true;
        for(Long channel : channels){
            if(first){
                channelsSB.append(channel);
                first = false;
            } else {
                channelsSB.append(":").append(channel);
            }
        }
        Long group = new Date().getTime();
        dataManager.saveData("features/channelLink/groups",group.toString(),channelsSB.toString());
        loadGroups(false);
        return group;
    }
    public static void addChannel(Long group, Long chanel){
        dataManager.saveData("features/channelLink/groups",group.toString(),dataManager.getData("features/channelLink/groups",group.toString()) + ":" + chanel.toString());
        loadGroups(false);
    }
    public static void unlink(Long channel){
        StringBuilder channels = new StringBuilder();
        String group = getGroupByChannel(channel).toString();
        String newData = dataManager.getData("features/channelLink/groups",group).replace(":" + channel,"");
        if(newData.split(":").length <= 2){
            dataManager.deleteData("features/channelLink/groups",group);
            channels.append("link canceled");
        } else{
            dataManager.saveData("features/channelLink/groups",group,newData);
            boolean first = true;
            for(Long channel2 : Objects.requireNonNull(getChannels(getGroupByChannel(channel)))){
                if(!channel2.equals(channel))
                    if(first){
                        channels.append("<#").append(channel2.toString()).append(">");
                        first = false;
                    } else {
                        channels.append(", <#").append(channel2.toString()).append(">");
                    }
            }
        }
        for(Long channel2 : Objects.requireNonNull(getChannels(getGroupByChannel(channel)))){
            Main.jda.getChannelById(TextChannel.class, channel2).sendMessage("Linked channels list updated: " + channels.toString()).queue();
        }
        loadGroups(false);
    }
}
