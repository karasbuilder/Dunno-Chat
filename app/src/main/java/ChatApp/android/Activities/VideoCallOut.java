package ChatApp.android.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ChatApp.android.R;

public class VideoCallOut extends AppCompatActivity {

    ImageView outcomingcallDecline_btn;
    TextView outcomingcallName_txt;
    String callsender_uid, callreceiver_uid, callreceiver_name, callsender_name;
    String callsender_token, callreceiver_token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call_out);

        outcomingcallDecline_btn = findViewById(R.id.btnoutcomingDecline);
        outcomingcallName_txt= findViewById(R.id.txtoutcomingName);

        callreceiver_uid = getIntent().getStringExtra("callreceiver");
        callsender_uid = getIntent().getStringExtra("callsender");
        callsender_name = getIntent().getStringExtra("callreceiver_name");

        String callsender_token_check = getIntent().getStringExtra("callsender_token");
        if(callsender_token_check != null)
        {
            callsender_token = callsender_token_check;
        }
        else
        {
            FirebaseDatabase.getInstance().getReference().child("users").child(callsender_uid).child("token")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            callsender_token = snapshot.getValue().toString();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }

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
        CallDeclineButton();
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
                    }
                }, 2000);
                finish();
            }
        });
    }

    private void sendCallInvitation() {
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
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                NotificationModel call_noti = new NotificationModel(callreceiver_token,"Video Call",callsender_name + " is calling...");
                        new PushNotificationSender().execute(call_noti);
            }
        }, 2000);
    }

}