package ChatApp.android.Model;

public class NotificationModel {
    public String receiver_token;
    public String title;
    public String body;

    public NotificationModel(String receiver_token, String title, String body) {
        this.receiver_token = receiver_token;
        this.title = title;
        this.body = body;
    }
}
