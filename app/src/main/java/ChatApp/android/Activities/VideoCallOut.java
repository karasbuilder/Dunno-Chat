package ChatApp.android.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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
    String callreceiver_uid, callreceiver_name;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call_out);

        outcomingcallDecline_btn = findViewById(R.id.btnoutcomingDecline);
        outcomingcallName_txt= findViewById(R.id.txtoutcomingName);

        reference = database.getReference("users").child(callreceiver_uid);

        reference.addValueEventListener(new ValueEventListener() {
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
    }

    private void sendCallInvitation() {

    }
}