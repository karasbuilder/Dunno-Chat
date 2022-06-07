package ChatApp.android.Activities;

public class NotificationModel {
    String receiver_token;
    String title;
    String body;

    public NotificationModel(String receiver_token, String title, String body) {
        this.receiver_token = receiver_token;
        this.title = title;
        this.body = body;
    }
}
