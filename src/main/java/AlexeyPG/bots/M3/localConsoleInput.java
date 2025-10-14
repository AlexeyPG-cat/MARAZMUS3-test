package AlexeyPG.bots.M3;

import net.dv8tion.jda.api.JDA;

import java.util.Scanner;

public class localConsoleInput implements Runnable{
    @SuppressWarnings("InfiniteLoopStatement")
    public void run(){
        try{
            while (true) {
                Scanner scr = new Scanner(System.in);
                String input = scr.next();
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
            }
        }catch(Exception ex){
            System.out.println("Console scanner failure");
        }
    }
}
