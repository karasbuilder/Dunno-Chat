package ChatApp.android.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;

import ChatApp.android.R;

public class VideoCallIn extends AppCompatActivity {

    String callsender_name;
    String callsender_uid;
    String callsender_room, callreceiver_room;
    ImageView acceptcallin_btn, declinecallin_btn;
    TextView txtincomingcall_sender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call_in);

        callreceiver_room = getIntent().getStringExtra("callreceiver_room");
        callsender_room = getIntent().getStringExtra("callsender_room");

        getCallSenderName();
        acceptCallInvitation();
        declineCallInvitation();
        checkIfCallSenderEnd();
    }

    private void checkIfCallSenderEnd() {
        FirebaseDatabase.getInstance().getReference("videochat").child(callsender_room).child("res").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue().toString().equals("null"))
                {
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getCallSenderName()
    {
        txtincomingcall_sender = findViewById(R.id.txtincomingCall);
        int len = callsender_room.length()/2;
        callsender_uid = callsender_room.substring(0,len);
        FirebaseDatabase.getInstance().getReference("users").child(callsender_uid).child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                callsender_name = snapshot.getValue().toString();
                txtincomingcall_sender.setText(callsender_name + " is calling...");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void acceptCallInvitation() {
        acceptcallin_btn = findViewById(R.id.btnincomingCallAccept);
        acceptcallin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinmeeting();
                FirebaseDatabase.getInstance().getReference("videochat").child(callreceiver_room).child("res").setValue("true");
                Toast.makeText(VideoCallIn.this, "Accepted video call", Toast.LENGTH_SHORT).show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        FirebaseDatabase.getInstance().getReference("videochat").child(callreceiver_room).child("res").setValue("null");
                    }
                }, 2000);
            }
        });

    }

    private void joinmeeting()
    {
        try {
            JitsiMeetConferenceOptions option = new JitsiMeetConferenceOptions.Builder()
                    .setServerURL(new URL("https://meet.jit.si"))
                    .setRoom(callsender_uid)
                    .setAudioMuted(false)
                    .setVideoMuted(false)
                    .setAudioOnly(false)
                    .setConfigOverride("requireDisplayName", true)
                    .build();
            JitsiMeetActivity.launch(VideoCallIn.this,option);
            finish();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void declineCallInvitation()
    {
        declinecallin_btn = findViewById(R.id.btnincomingCalldecline);
        declinecallin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference("videochat").child(callreceiver_room).child("res").setValue("false");
                Toast.makeText(VideoCallIn.this, "Declined video call", Toast.LENGTH_SHORT).show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        FirebaseDatabase.getInstance().getReference("videochat").child(callreceiver_room).child("res").setValue("null");
                        finish();
                    }
                }, 1000);

            }
        });
    }
}