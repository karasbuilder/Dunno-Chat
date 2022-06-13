package ChatApp.android.Model;

public class Post {


    private String postID,postTitle,postDescription,uid,uEmail,postImage;
    private long postUpdateAt,postCreateAt;
    private int numReact;
    public Post(){

    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostDescription() {
        return postDescription;
    }

    public void setPostDescription(String postDescription) {
        this.postDescription = postDescription;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getuEmail() {
        return uEmail;
    }

    public void setuEmail(String uEmail) {
        this.uEmail = uEmail;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public long getPostUpdateAt() {
        return postUpdateAt;
    }

    public void setPostUpdateAt(long postUpdateAt) {
        this.postUpdateAt = postUpdateAt;
    }

    public long getPostCreateAt() {
        return postCreateAt;
    }

    public void setPostCreateAt(long postCreateAt) {
        this.postCreateAt = postCreateAt;
    }

    public int getNumReact() {
        return numReact;
    }

    public void setNumReact(int numReact) {
        this.numReact = numReact;
    }



}
