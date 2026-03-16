package AlexeyPG.bots.M3.features.reviews;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class Review implements Serializable
{
    @Serial
    private static final long serialVersionUID = 1L;
    private final String authorID;
    private String authorName;
    private Long timestamp;
    private byte rating;
    private String reviewText;
    private ArrayList<String> likes;
    private ArrayList<String> dislikes;
    private boolean visible;

    public String getAuthorID(){return authorID;}
    public String getAuthorName(){return authorName;}
    public String getReview(){return reviewText;}
    public Long getTimestamp(){return timestamp;}
    public byte getRating(){return rating;}
    public ArrayList<String> getLikes(){return likes;}
    public ArrayList<String> getDislikes(){return dislikes;}
    public boolean isVisible(){return visible;}

    public Review(String authorID_,String authorName_,byte rating_,String reviewText_){
        authorID = authorID_;
        rating = (byte)Math.max(Math.min(rating_,5),1);
        reviewText = reviewText_;
        authorName = authorName_;
        timestamp = new java.util.Date().getTime();
        likes = new ArrayList<>();
        dislikes = new ArrayList<>();
        visible = true;
    }
    public Review(String authorID_,String authorName_,byte rating_,String reviewText_,Boolean hidden){
        authorID = authorID_;
        rating = (byte)Math.max(Math.min(rating_,5),1);
        reviewText = reviewText_;
        authorName = authorName_;
        timestamp = new java.util.Date().getTime();
        likes = new ArrayList<>();
        dislikes = new ArrayList<>();
        visible = !hidden;
    }

    public void editAuthorName(String newName){
        authorName = newName;
    }
    public void editReview(String review_, byte rating_){
        reviewText = review_;
        rating = rating_;
        timestamp = new java.util.Date().getTime();
    }
    public void like(String user){
        System.out.println("linking");
        dislikes.remove(user);
        if(!likes.contains(user)){
            likes.add(user);
        }
    }
    public void dislike(String user){
        System.out.println("Disliking");
        likes.remove(user);
        if(!dislikes.contains(user)){
           dislikes.add(user);
        }
    }
    public void hide(boolean hide){
        visible = !hide;
    }
    public PublicReview getPublic(String userId){
        return new PublicReview(authorID,authorName,reviewText,timestamp,rating,likes,dislikes,userId);
    }
}
