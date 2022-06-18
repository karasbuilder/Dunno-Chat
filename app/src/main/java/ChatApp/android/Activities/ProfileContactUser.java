package ChatApp.android.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ChatApp.android.Model.Request;
import ChatApp.android.Model.User;
import ChatApp.android.R;
import ChatApp.android.databinding.ActivityProfileContactUserBinding;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileContactUser extends AppCompatActivity {
    ActivityProfileContactUserBinding binding;

    private String receiverUserId, senderUserId,messageRequest;
    private DatabaseReference userRef, chatRequestRef, chatSentRef;
    private FirebaseAuth mAuth;

    private CircleImageView imageContactProfile;
    private TextView userContactName;
    private Button buttonAddFriend;
    private User receivedUser;
    private String currentuid;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityProfileContactUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //setting what database we use for storage data
        userRef = FirebaseDatabase.getInstance().getReference().child("users");
        chatRequestRef = FirebaseDatabase.getInstance().getReference().child("Friend Requests");
        chatSentRef = FirebaseDatabase.getInstance().getReference().child("Friend Received");
        //set id of ui
        imageContactProfile=binding.profileImageUserContact;
        userContactName=binding.userContactName;
        buttonAddFriend=binding.ButtonAddFriend;

        //set initialize firebase
        mAuth = FirebaseAuth.getInstance();
        senderUserId = mAuth.getCurrentUser().getUid();

        receiverUserId = getIntent().getExtras().get("visitID").toString();

        currentuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //retrived data from firebase === sender ID
        getInformationUser();

        buttonAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequestFriend();
            }
        });


    }
    public void getInformationUser(){
        userRef.child(receiverUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(receiverUserId.equals(currentuid)) {
                        buttonAddFriend.setVisibility(View.GONE);
                        binding.editTextMessageRequest.setVisibility(View.GONE);
                    }
                    else
                    {
                        buttonAddFriend.setVisibility(View.VISIBLE);
                        binding.editTextMessageRequest.setVisibility(View.VISIBLE);
                    }
                    receivedUser = snapshot.getValue(User.class);
                    userContactName.setText(receivedUser.getName());
                    Glide.with(getApplicationContext()).load(receivedUser.getProfileImage()).centerCrop().placeholder(R.drawable.avatar).into(binding.profileImageUserContact);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public  void sendRequestFriend(){
        if(buttonAddFriend.getText().equals("Cancel Invited")){
            chatRequestRef.child(senderUserId).child(receiverUserId)
                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                chatSentRef.child(receiverUserId).child(senderUserId)
                                        .removeValue();
                            }
                        }
                    });
           buttonAddFriend.setText("Add Friend");
            return;
        }
        /**
         * We will add senderUser and receiverUser in the same table
         * Also seperate two table => chat received =>chat request
         * senderID => store received ID
         * receivedID => store senderID
         * */

        //

        long nowTime=System.currentTimeMillis();
        Request request=new Request(senderUserId,receiverUserId,messageRequest,nowTime);

        chatRequestRef.child(senderUserId).child(receiverUserId)
                .setValue(request)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                           chatSentRef.child(receiverUserId).child(senderUserId)
                                   .setValue(request)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                                buttonAddFriend.setText("Cancel Invited");
                                            }
                                        }
                                    });
                        }
                    }
                });


    }
}