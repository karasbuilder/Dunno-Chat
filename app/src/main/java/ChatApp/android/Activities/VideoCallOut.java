package ChatApp.android.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;

import ChatApp.android.R;

public class VideoCallOut extends AppCompatActivity  {

    ImageView outcomingcallDecline_btn;
    TextView outcomingcallName_txt;
    String callsender_uid, callreceiver_uid;
    String callreceiver_name, callsender_name;
    String callsender_token, callreceiver_token;
    String callsender_res, callreceiver_res;
    String callsender_room, callreceiver_room;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call_out);

        outcomingcallDecline_btn = findViewById(R.id.btnoutcomingDecline);
        outcomingcallName_txt= findViewById(R.id.txtoutcomingName);

        callreceiver_uid = getIntent().getStringExtra("callreceiver");
        callsender_uid = getIntent().getStringExtra("callsender");
        callreceiver_name = getIntent().getStringExtra("callreceiver_name");
        callsender_token = getIntent().getStringExtra("callsender_token");
        callreceiver_room = getIntent().getStringExtra("callreceiver_room");
        callsender_room = getIntent().getStringExtra("callsender_room");

            //Get call receiver's token
        FirebaseDatabase.getInstance().getReference().child("users").child(callreceiver_uid).child("token")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        callreceiver_token = snapshot.getValue().toString();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        FirebaseDatabase.getInstance().getReference().child("users").child(callreceiver_uid)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    callreceiver_name = snapshot.child("name").getValue().toString();
                    outcomingcallName_txt.setText(callreceiver_name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sendCallInvitation();
        checkResponse();
        CallDeclineButton();
    }

    private void checkResponse()
    {
        reference = FirebaseDatabase.getInstance().getReference("videochat").child(callreceiver_room).child("res");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                callreceiver_res = snapshot.getValue().toString();
                if(callreceiver_res == "null")
                {
                    Toast.makeText(VideoCallOut.this, "Waiting for response", Toast.LENGTH_LONG).show();
                }
                else if(callreceiver_res == "true")
                {
                    Toast.makeText(VideoCallOut.this, "Receiver accepted the call", Toast.LENGTH_SHORT).show();
                    String key = null;
                    joinmeeting(key);
//                    Intent intent = new Intent(VideoCallOut.this, VideoCall.class);
//                    startActivity(intent);
                }
                else if(callreceiver_res == "false")
                {
                    Toast.makeText(VideoCallOut.this, "Receiver declined the call", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void CallDeclineButton()
    {
        outcomingcallDecline_btn = findViewById(R.id.btnoutcomingDecline);
        outcomingcallDecline_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        NotificationModel call_noti = new NotificationModel(callreceiver_token,"Video Call","Miss call from " +callsender_name);
                        new PushNotificationSender().execute(call_noti);
                        ///Set call sender response to null when cancel the call
                        reference = FirebaseDatabase.getInstance().getReference("videochat").child(callsender_uid).child("res");
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                reference.setValue("null");
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }, 2000);
                finish();
            }
        });
    }

    private void sendCallInvitation() {
        //Set call send response = true when click video call
        FirebaseDatabase.getInstance().getReference("videochat").child(callsender_room).child("res").setValue("true");
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                NotificationModel call_noti = new NotificationModel(callreceiver_token,"Video Call",callsender_name);
                        new PushNotificationSender().execute(call_noti);
            }
        }, 2000);
    }

    private void joinmeeting(String key)
    {
        try {
            JitsiMeetConferenceOptions option = new JitsiMeetConferenceOptions.Builder()
                    .setServerURL(new URL("https://meet.jit.si"))
                    .setRoom(callsender_name)
                    .setAudioMuted(false)
                    .setVideoMuted(false)
                    .setAudioOnly(false)
                    .setConfigOverride("requireDisplayName", true)
                    .build();
            JitsiMeetActivity.launch(VideoCallOut.this,option);
            finish();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}