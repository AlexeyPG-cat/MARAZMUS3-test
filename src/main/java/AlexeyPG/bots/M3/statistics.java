package AlexeyPG.bots.M3;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Member;


import java.util.*;


public class statistics {
    private static Map<String, List<Integer>> statisticsMap = new HashMap<>();
    private static Map<String, Long> lastUpdateTime = new HashMap<>();
    private static boolean isBusy = false;
    private static long updateInterval = 60000L;

    private static void updateOnlineStatistics(String serverId){
        while (isBusy) {try {Thread.sleep(100);} catch (InterruptedException e) {throw new RuntimeException(e);}}
        isBusy = true;
        Main.jda.getGuildById(serverId).loadMembers().onSuccess(members -> {
            int online = 0;
            int total = 0;
            int inGame = 0;
            for (Member member : members) {
                if(!member.getActivities().isEmpty()) inGame++;
                if(member.getOnlineStatus() != OnlineStatus.OFFLINE) online++;
                total++;
            }
            statisticsMap.put(serverId, Arrays.asList(total,online,inGame));
            lastUpdateTime.put(serverId,System.currentTimeMillis());
            //System.out.println("Total: " + total + " online: " + online + ", in game: " + inGame);
            isBusy = false;
        });
    }

    //If it works it works
    public static int getTotal(String serverId){
        if(lastUpdateTime.get(serverId) == null) {updateOnlineStatistics(serverId);}
        else if(lastUpdateTime.get(serverId) < System.currentTimeMillis() - updateInterval){
            updateOnlineStatistics(serverId);
        }
        while (isBusy) {try {Thread.sleep(100);} catch (InterruptedException e) {throw new RuntimeException(e);}}
        return statisticsMap.get(serverId).get(0);
    }
    public static int getOnline(String serverId){
        if(lastUpdateTime.get(serverId) == null) {updateOnlineStatistics(serverId);}
        else if(lastUpdateTime.get(serverId) < System.currentTimeMillis() - updateInterval){
            updateOnlineStatistics(serverId);
        }
        while (isBusy) {try {Thread.sleep(100);} catch (InterruptedException e) {throw new RuntimeException(e);}}
        return statisticsMap.get(serverId).get(1);
    }
    public static int getInGame(String serverId){
        if(lastUpdateTime.get(serverId) == null) {updateOnlineStatistics(serverId);}
        else if(lastUpdateTime.get(serverId) < System.currentTimeMillis() - updateInterval){
            updateOnlineStatistics(serverId);
        }
        while (isBusy) {try {Thread.sleep(100);} catch (InterruptedException e) {throw new RuntimeException(e);}}
        return statisticsMap.get(serverId).get(2);
    }

}
