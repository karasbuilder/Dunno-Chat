package ChatApp.android.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ChatApp.android.R;

public class VideoCallIn extends AppCompatActivity {

    String callreceiver_token;
    String callsender_name;
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
    }

    private void getCallSenderName()
    {
        txtincomingcall_sender = findViewById(R.id.txtincomingCall);
        int len = callreceiver_room.length()/2;
        String callsender_uid = callreceiver_room.substring(0,len);
        FirebaseDatabase.getInstance().getReference("users").child(callsender_uid).child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                callsender_name = snapshot.getValue().toString();
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

                FirebaseDatabase.getInstance().getReference("videochat").child(callreceiver_room).child("res").setValue("true");
                Toast.makeText(VideoCallIn.this, "Accepted video call", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void declineCallInvitation()
    {
        declinecallin_btn = findViewById(R.id.btnincomingCalldecline);
        declinecallin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference("videochat").child(callreceiver_room).child("res").setValue("false");
                Toast.makeText(VideoCallIn.this, "Declined video call", Toast.LENGTH_SHORT).show();
            }
        });
    }
}