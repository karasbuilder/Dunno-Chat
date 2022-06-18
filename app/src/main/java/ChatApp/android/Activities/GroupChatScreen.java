package ChatApp.android.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import ChatApp.android.Adapters.GroupChatAdapter;
import ChatApp.android.Adapters.GroupMessageAdapter;
import ChatApp.android.Model.Message;
import ChatApp.android.R;
import ChatApp.android.databinding.ActivityGroupChatScreenBinding;
import de.hdodenhof.circleimageview.CircleImageView;

public class GroupChatScreen extends AppCompatActivity {

    private ActivityGroupChatScreenBinding binding;
    private String groupID,myGroupRole;

    ImageView imageView;
    RecyclerView recyclerViewGroupMessage;
    FirebaseDatabase database;
    FirebaseAuth firebaseAuth;
    ProgressDialog dialog;

    GroupMessageAdapter adapter;
    ArrayList<Message> groupMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityGroupChatScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //init view
        recyclerViewGroupMessage=findViewById(R.id.recyclerViewGroupMessage);
        imageView=binding.imageView2;

        setSupportActionBar(binding.toolbarGroup);



        //get current firebase data platform from google services json data
        database=FirebaseDatabase.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        //getting the information group id of user
        Intent intent=getIntent();
        groupID=intent.getStringExtra("groupID");
        loadGroupInformation();
        groupMessage=new ArrayList<>();
        adapter=new GroupMessageAdapter(this,groupMessage);
        recyclerViewGroupMessage.setAdapter(adapter);
        //event for back activity
        binding.imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        //load group message
        loadGroupMessages();
        loadMyGroupRole();
        //event for sent buuton
        binding.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get message data
                String message=binding.messageBox.getText().toString();
                //check validate
                if(TextUtils.isEmpty(message)){
                    //empty message
                    Toast.makeText(GroupChatScreen.this, "Can't Send Empty Message", Toast.LENGTH_SHORT).show();

                }
                else{
                    //send message when user click to symbol send in message box
                    sendMessage(message);
                }
            }
        });

        //set event navigation
       binding.toolbarGroup.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.btnAddGroupMenu:
                        //when user click to create group application
                        Intent intent = new Intent(GroupChatScreen.this, GroupParticipantsAdd.class);
                        intent.putExtra("groupID", groupID);
                        startActivity(intent);
                        return true;
                }

                return false;
            }
        });

    }
    public void loadMyGroupRole(){
        //load group role of current user for setting up about some feature of group
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupID).child("Participants")
                .orderByChild("uid").equalTo(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                            myGroupRole=""+dataSnapshot.child("role").getValue();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    public void sendMessage(String message){
        //store message data in to firebase storage
            String timeStamp=""+System.currentTimeMillis();

        Message mess=new Message(message,firebaseAuth.getUid(),System.currentTimeMillis());
        mess.setType("text");
     DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Groups");
            reference.child(groupID).child("Messages").child(timeStamp)
                    .setValue(mess).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            binding.messageBox.setText("");

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(GroupChatScreen.this, "Failed Error send message group", Toast.LENGTH_SHORT).show();

                        }
                    });
    }
    public void loadGroupMessages(){
        //loading all information message of definitive group
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(groupID).child("Messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupMessage.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    Message message=ds.getValue(Message.class);
                    groupMessage.add(message);

                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private  void loadGroupInformation(){
        //loading all information of definition group about name , id .... image
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Groups");
        reference.orderByChild("groupID").equalTo(groupID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                            binding.groupName.setText(dataSnapshot.child("groupName").getValue().toString());
                            Glide.with(getApplicationContext()).load(binding.iconGroup)
                                    .placeholder(R.drawable.avatar)
                                    .into(binding.iconGroup);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                })
        ;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //option menu of group chat screen user , when they clickit
        getMenuInflater().inflate(R.menu.menu_group,menu);
        if(myGroupRole.equals("creator")||myGroupRole.equals("admin")){
            menu.findItem(R.id.btnAddGroupMenu).setVisible(true);
        }
        else{
            menu.findItem(R.id.btnAddGroupMenu).setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}