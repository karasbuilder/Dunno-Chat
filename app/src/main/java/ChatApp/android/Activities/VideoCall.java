package ChatApp.android.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import ChatApp.android.R;

public class VideoCall extends AppCompatActivity {

    String sendcallUser;
    String receivecallUSer;
    boolean isVideo = true;
    boolean isAudio = true;
    boolean isPeerConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);
    }
}