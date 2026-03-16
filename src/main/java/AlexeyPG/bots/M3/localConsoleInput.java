package AlexeyPG.bots.M3;

import AlexeyPG.bots.M3.features.autoReact;
import AlexeyPG.bots.M3.features.channelLink;
import AlexeyPG.bots.M3.features.reviews.Review;
import AlexeyPG.bots.M3.features.voiceNotifier;
import AlexeyPG.bots.M3.interactions.interactionHandler;
import net.dv8tion.jda.api.JDA;
import AlexeyPG.bots.M3.features.reviews.reviewManager;

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
                if(input.startsWith("createReview")){
                    String[] in = input.split(" ");
                    reviewManager.createReview(in[1],in[3],Integer.parseInt(in[2]),"data/bot/reviews/site");
                    //createReview ID RATING review
                    System.out.println("done");
                }
                if(input.startsWith("deleteReview")){
                    String[] in = input.split(" ");
                    reviewManager.deleteReview(in[1],"data/bot/reviews/site");
                    System.out.println("done");
                }
                if(input.startsWith("getReview")){
                    String[] in = input.split(" ");
                    Review review = reviewManager.getReview(in[1],"data/bot/reviews/site",true);
                    if(review!=null){
                        System.out.println(review.getAuthorID());
                        System.out.println(review.getAuthorName());
                        System.out.println(review.getRating());
                        System.out.println(review.getReview());
                        System.out.println(review.getLikes().size());
                        System.out.println(review.getDislikes().size());
                    } else System.out.println("Review not found");
                }
                if(input.startsWith("rateReview")){
                    String[] in = input.split(" ");
                    reviewManager.rateReview(in[1],in[2],Boolean.parseBoolean(in[3]),"data/bot/reviews/site");
                    System.out.println("done");
                }
            }
        }catch(Exception ex){
            System.out.println("Console scanner failure \n" + ex);
            run();
        }
    }
}
