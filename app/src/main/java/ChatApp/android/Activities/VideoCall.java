package ChatApp.android.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Bundle;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import ChatApp.android.R;

public class VideoCall extends AppCompatActivity {

    String sendcallUser;
    String receivecallUSer;
    boolean isVideo = true;
    boolean isAudio = true;
    boolean isPeerConnected = false;
    ImageView acceptCallBtn;
    ImageView endCallBtn;
    ImageView toggleAudioBtn;
    ImageView toggleVideoBtn;
    private WebView webView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);

//        acceptCallBtn = findViewById(R.id.btnacceptCall);
//        toggleAudioBtn = findViewById(R.id.btntoggleMic);
//        toggleVideoBtn= findViewById(R.id.btntoggleVideo);
//        webView = findViewById(R.id.webView);
//        sendcallUser = getIntent().getStringExtra("");

    }
}