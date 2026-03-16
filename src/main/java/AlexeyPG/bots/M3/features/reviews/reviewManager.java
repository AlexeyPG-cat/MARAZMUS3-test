package AlexeyPG.bots.M3.features.reviews;

import AlexeyPG.bots.M3.Main;
import AlexeyPG.bots.M3.dataManager;
import jakarta.annotation.Nullable;

import java.util.ArrayList;

public class reviewManager {
    private static boolean busy = false;

    public static void createReview(String authorID, String reviewText, int rating, String file){
        while(busy) {
            try {Thread.sleep(100);} catch (InterruptedException ignored) {}
        }
        busy = true;
        Object reviewsObj = dataManager.loadStream(file);
        ArrayList<Review> reviewList = null;
        if(reviewsObj != null){
            reviewList = (ArrayList<Review>) reviewsObj;
            reviewList.removeIf(rev -> rev.getAuthorID().equals(authorID));
        }
        String name = authorID;
        try{
            name = Main.jda.getUserById(authorID).getGlobalName();
        } catch (Exception ignored){}
        Review review = new Review(authorID,name,(byte) rating,reviewText);
        if(reviewList!=null){
            reviewList.add(review);
        } else {
            reviewList = new ArrayList<>(){};
            reviewList.add(review);
        }
        dataManager.saveStream(reviewList,file);
        busy = false;
    }
    @Nullable
    public static ArrayList<Review> getReviews(String file, boolean revealHidden){
        while(busy) {try {Thread.sleep(100);}catch(InterruptedException ignored){}}
        busy = true;
        ArrayList<Review> reviews = null;
        try {
            reviews = (ArrayList<Review>) dataManager.loadStream(file);
            if(!revealHidden){reviews.removeIf(rev -> !rev.isVisible());}
        } catch (Exception e){ System.out.println(e.getMessage());}
        busy = false;
        return reviews;
    }
    public static Review getReview(String author, String file, boolean revealHidden){
        Review review = null;
        ArrayList<Review> reviews = getReviews(file,revealHidden);
        if(reviews!=null) for(Review rev : reviews){
            if(rev.getAuthorID().equals(author)) review = rev;
        }
        return review;
    }
    public static void deleteReview(String author, String file){
        ArrayList<Review> reviews = getReviews(file,true);
        if(reviews!=null) reviews.removeIf(rev -> rev.getAuthorID().equals(author));
        dataManager.saveStream(reviews,file);
    }
    public static boolean rateReview(String author, String user, boolean positive, String file){
        ArrayList<Review> reviews = getReviews(file,false);
        if(reviews == null) return false;
        for(Review review : reviews){
            if(review.getAuthorID().equals(author)){
                if(positive){
                    review.like(user);
                } else {
                    review.dislike(user);
                }
                dataManager.saveStream(reviews,file);
                return true;
            }
        }
        return false;
    }
}




