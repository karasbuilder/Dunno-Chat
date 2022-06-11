package ChatApp.android.Services;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;

import ChatApp.android.Activities.ChatUserScreen;
import ChatApp.android.Activities.UserHomeChat;
import ChatApp.android.Activities.VideoCallIn;
import ChatApp.android.MainActivity;
import ChatApp.android.Model.User;
import ChatApp.android.R;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "DunnoFirebaseService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getNotification() != null) {
            String body = remoteMessage.getNotification().getBody();
            String title = remoteMessage.getNotification().getTitle();
            if(title.equals("Video Call"))
            {
                callNofitication(title,body);
            }
            else
            {
                normalNofitication(title,body);
            }
        }
//        if(isAppForeground()){
//            // Handle notification silently without displaying in notification tray
//        }else {
//
//            if (remoteMessage.getNotification() != null) {
//                //Log.d(TAG, "Current useruid: " + user.getUid());
//                String body = remoteMessage.getNotification().getBody();
//                String title = remoteMessage.getNotification().getTitle();
//                //int color = Integer.parseInt(remoteMessage.getNotification().getColor());
//                sendNotification(body, title);
//            }
//        }
    }

    @Override
    public void onNewToken(String token) {
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
//        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        FirebaseDatabase.getInstance().getReference("users").child(uid).child("token").setValue(token);
//        Log.d("Firebase uid",uid);
//        Log.d(TAG, "Refreshed Firebase token: " + token);
        // TODO: Implement this method to send token to your app server.
    }

    private void sendNotification(String messageBody, String messageTitle, Intent intent, String decline, String accept) {
        //Intent intent = new Intent(this, UserHomeChat.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.project_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_background))
                        .setContentTitle(messageTitle)
                        .setColor(getResources().getColor(R.color.teal_200, getApplication().getTheme()))
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setPriority(NotificationManager.IMPORTANCE_HIGH)
                        .addAction(new NotificationCompat.Action(
                                android.R.drawable.sym_call_missed,
                                decline,
                                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)))
                        .addAction(new NotificationCompat.Action(
                                android.R.drawable.sym_call_outgoing,
                                accept,
                                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)));

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);

            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notificationBuilder.build());
    }

    private boolean isAppForeground() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    private void normalNofitication(String title, String body)
    {
        Intent intent = new Intent(this, UserHomeChat.class);
        sendNotification(body, title, intent, "Cancel", "OK");
    }

    private void callNofitication(String title, String body)
    {
        Intent intent = new Intent(getApplicationContext(), VideoCallIn.class);
        intent.putExtra("notif_callsender", body);
        sendNotification(body, title, intent, "Decline", "Accept");
    }
}
