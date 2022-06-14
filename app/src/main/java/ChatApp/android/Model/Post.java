package ChatApp.android.Model;

import java.util.ArrayList;

public class Post {

    private String postid;
    private String uid;
    private String name;
    private String profileimage;
    private ArrayList<String> likes = new ArrayList<>();
    private long timestamp;
    private String content;
    private boolean liked;

    public Post() { }

    public Post(String postid, String uid, String name, String profileimage, ArrayList<String> likes, long timestamp, String content, boolean liked) {
        this.postid = postid;
        this.uid = uid;
        this.name = name;
        this.profileimage = profileimage;
        this.likes = likes;
        this.timestamp = timestamp;
        this.content = content;
        this.liked = liked;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public ArrayList<String> getLikes() {
        return likes;
    }

    public void setLikes(ArrayList<String> likes) {
        this.likes = likes;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public int getLikesAmount() {
        return likes.size();
    }
}
