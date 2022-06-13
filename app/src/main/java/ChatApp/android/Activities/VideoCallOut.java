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
    int count = 0;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call_out);

        outcomingcallDecline_btn = findViewById(R.id.btnoutcomingDecline);
        outcomingcallName_txt= findViewById(R.id.txtoutcomingName);

        callsender_uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference("users").child(callsender_uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                callsender_token = snapshot.child("token").getValue().toString();
                callsender_name = snapshot.child("name").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        callsender_room = getIntent().getStringExtra("callsender_room");
        callreceiver_uid = getIntent().getStringExtra("callreceiver_uid");
        callreceiver_name = getIntent().getStringExtra("callreceiver_name");
        callreceiver_token = getIntent().getStringExtra("callreceiver_token");
        callreceiver_room = getIntent().getStringExtra("callreceiver_room");

        outcomingcallName_txt.setText("Calling " + callreceiver_name);

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
                if(callreceiver_res .equals("null") && count < 1)
                {
                    Toast.makeText(VideoCallOut.this, "Waiting for response", Toast.LENGTH_LONG).show();
                    count++;
                }
                else if(callreceiver_res.equals("true"))
                {
                    Toast.makeText(VideoCallOut.this, "Receiver accepted the call", Toast.LENGTH_SHORT).show();
                    joinmeeting(callsender_uid);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            FirebaseDatabase.getInstance().getReference("videochat").child(callsender_room).child("res").setValue("null");
                        }
                    }, 2000);
                }
                else if(callreceiver_res.equals("false"))
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
                        NotificationModel call_noti = new NotificationModel(callreceiver_token,"Notification","Miss call from " +callsender_name);
                        new PushNotificationSender().execute(call_noti);
                        ///Set call sender response to null when cancel the call
                        reference = FirebaseDatabase.getInstance().getReference("videochat").child(callsender_room).child("res");
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
                NotificationModel call_noti = new NotificationModel(callreceiver_token,"Video Call", callsender_name + " calling...");
                        new PushNotificationSender().execute(call_noti);
            }
        }, 2000);
    }

    private void joinmeeting(String key)
    {
        try {
            JitsiMeetConferenceOptions option = new JitsiMeetConferenceOptions.Builder()
                    .setServerURL(new URL("https://meet.jit.si"))
                    .setRoom(key)
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