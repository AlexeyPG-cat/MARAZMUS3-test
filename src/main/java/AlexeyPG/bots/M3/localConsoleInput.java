package AlexeyPG.bots.M3;

import AlexeyPG.bots.M3.features.autoReact;
import AlexeyPG.bots.M3.features.channelLink;
import AlexeyPG.bots.M3.features.voiceNotifier;
import AlexeyPG.bots.M3.interactions.interactionHandler;
import net.dv8tion.jda.api.JDA;

import java.util.Scanner;

public class localConsoleInput implements Runnable{
    @SuppressWarnings("InfiniteLoopStatement")
    public void run(){
        try{
            while (true) {
                Scanner scr = new Scanner(System.in);
                String input = scr.nextLine();
                if(input.equals("stop")){
                    Main.jda.shutdown();
                    System.out.println("Instance shutting down");
                }
                if(input.equals("start")){
                    if(Main.jda.getStatus() == JDA.Status.SHUTDOWN){
                        System.out.println("Instance starting instance");
                        Main.start(config.get("Token"));
                    } else {
                        System.out.println("Active instance detected");
                        System.out.println("New instance start aborted");
                    }
                }
                if(input.equals("status")){
                    System.out.println(Main.jda.getStatus());
                }
                if(input.startsWith("addRole")){
                    publicAccess.roles.addRole(input.split(" ")[1],input.replace("addRole " + input.split(" ")[1],""));
                    System.out.println("Complete");
                }
                if(input.startsWith("remRole")){
                    publicAccess.roles.removeRole(input.split(" ")[1]);
                    System.out.println("Complete");
                }
                if(input.startsWith("refreshAll")){
                    System.out.println("Refreshing");
                    voiceNotifier.loadWatchers();
                    autoReact.loadChannels();
                    autoReact.loadUsers();
                    interactionHandler.updateCommands();
                    channelLink.loadGroups();
                    System.out.println(publicAccess.roles.load());

                }
            }
        }catch(Exception ex){
            System.out.println("Console scanner failure \n" + ex);
            run();
        }
    }
}
