package ChatApp.android.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ChatApp.android.Adapters.ParticipantsAddAdapter;
import ChatApp.android.Model.User;
import ChatApp.android.R;

public class GroupParticipantsAdd extends AppCompatActivity {

    //init iview
    private RecyclerView userAdd;
    private ActionBar actionBar;
    private FirebaseAuth firebaseAuth;
    String groupID,myGroupRole;
    Toolbar toolbar;

    private ArrayList<User> userArrayList;
    private ParticipantsAddAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_participants_add);

        userAdd=findViewById(R.id.recyclerViewAddParticipant);
        firebaseAuth=FirebaseAuth.getInstance();
        toolbar=findViewById(R.id.customToolbarParticipant);

        groupID=getIntent().getStringExtra("groupID");

        loadGroupInfo();
        getAllUsers();

    }
    private  void loadGroupInfo(){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Groups");
        reference.orderByChild("groupID").equalTo(groupID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    final String groupName=""+dataSnapshot.child("groupName");
                    String groupDescription=""+ dataSnapshot.child("groupDescription").getValue();
                    toolbar.setTitle("Add Participants");

                    reference.child(groupID).child("Participants").child(firebaseAuth.getCurrentUser().getUid())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()){
                                        myGroupRole=""+snapshot.child("role").getValue();


                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getAllUsers(){
        userArrayList=new ArrayList<>();
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userArrayList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    User user=dataSnapshot.getValue(User.class);
                    if(!firebaseAuth.getUid().equals(user.getUid())){
                        userArrayList.add(user);
                    }
                }
                adapter=new ParticipantsAddAdapter(GroupParticipantsAdd.this,userArrayList,groupID,myGroupRole);
                userAdd.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}