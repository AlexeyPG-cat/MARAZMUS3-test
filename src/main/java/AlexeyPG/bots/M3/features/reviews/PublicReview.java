package AlexeyPG.bots.M3.features.reviews;

import java.io.Serializable;
import java.util.ArrayList;

public class PublicReview {
    private String authorID;
    private String authorName;
    private Long timestamp;
    private byte rating;
    private String reviewText;
    private int likes;
    private int dislikes;
    private String myReview;

    public PublicReview(String authorID_, String authorName_, String reviewText_, long timestamp_, byte rating_, ArrayList<String> likes_, ArrayList<String> dislikes_, String userID){
        authorID=authorID_;
        authorName=authorName_;
        reviewText=reviewText_;
        timestamp=timestamp_;
        rating=rating_;
        likes=likes_.size();
        dislikes=dislikes_.size();
        if(userID!=null){
            if(likes_.contains(userID)) myReview = "like";
            else if(dislikes_.contains(userID)) myReview = "dislike";
            else myReview = "none";
        }
    }
}
