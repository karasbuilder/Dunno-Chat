package ChatApp.android.Activities;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Arrays;

public class PushNotificationSender extends AsyncTask<NotificationModel, Void, Void> {
    public final static String AUTH_KEY_FCM = "AAAAV2nuKLE:APA91bEYryE8KuV7I2Hs7MR4-8vnTSShBYaKz95_cbiBK-_bIwcv-aerF5-KpaCi3nmQkFAtQkQzR_PJjDbonHJXeWWeAboSJBbeaC-VDBsNArZbvHxNGTXdD5tLb6g0UThTv20CWaui";
    public final static String API_URL_FCM = "https://fcm.googleapis.com/fcm/send";


    //Send push notification with header, body to specific user ( with token )
    @Override
    protected Void doInBackground(NotificationModel... nmodel) {
        String rawtoken = nmodel[0].receiver_token;
        String title = nmodel[0].title;
        String body = nmodel[0].body;

        URL url = null;
        try {
            url = new URL(API_URL_FCM);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        conn.setUseCaches(false);
        conn.setDoInput(true);
        conn.setDoOutput(true);

        try {
            conn.setRequestMethod("POST");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        conn.setRequestProperty("Authorization", "key=" + AUTH_KEY_FCM);
        conn.setRequestProperty("Content-Type", "application/json");

        JSONObject json = new JSONObject();

        try {
            json.put("to", rawtoken);
            json.put("color", "#FF33CC");
            JSONObject info = new JSONObject();
            info.put("title", title); // Notification title
            info.put("body", body); // Notification
            // body
            json.put("notification", info);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("JSON", json.toString());
        try {
            OutputStreamWriter wr = new OutputStreamWriter(
                    conn.getOutputStream());
            wr.write(json.toString());
            wr.flush();

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ///Cleanup after operation
        nmodel[0].receiver_token = null;
        nmodel[0].title = null;
        nmodel[0].body = null;
        ///
        return null;
    }

}