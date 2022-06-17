package ChatApp.android.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

import ChatApp.android.Adapters.GroupChatAdapter;
import ChatApp.android.Model.Group;
import ChatApp.android.R;
import ChatApp.android.databinding.FragmentGroupsContactBinding;


public class GroupsContact extends Fragment {

   FragmentGroupsContactBinding binding;
   private FirebaseAuth firebaseAuth;
   private ArrayList<Group> listGroups;
   private GroupChatAdapter groupChatAdapter;
   private RecyclerView recyclerViewGroup;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       binding=FragmentGroupsContactBinding.inflate(inflater,container,false);
       View view=binding.getRoot();


    firebaseAuth=FirebaseAuth.getInstance();

    recyclerViewGroup=view.findViewById(R.id.recyclerViewGroupList);



     loadListGroup();

        return view;
    }
    public void loadListGroup(){
        listGroups=new ArrayList<>();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Groups");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listGroups.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    if(ds.child("Participants").child(firebaseAuth.getCurrentUser().getUid()).exists()){
                        Group group=ds.getValue(Group.class);
                        listGroups.add(group);

                    }

                }
                groupChatAdapter=new GroupChatAdapter(getActivity(),listGroups);
                recyclerViewGroup.setAdapter(groupChatAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void searchGroupChatList(String query){
        listGroups=new ArrayList<>();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Groups");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listGroups.clear();
                for(DataSnapshot ds:snapshot.getChildren()){

                    //search group by title
                    if(!ds.child("Participants").child(firebaseAuth.getUid()).exists()){
                        if(ds.child("groupName").toString().toLowerCase().contains(query.toLowerCase(Locale.ROOT))){
                            Group group=ds.getValue(Group.class);
                            listGroups.add(group);
                        }


                    }
                    groupChatAdapter=new GroupChatAdapter(getActivity(),listGroups);
                    recyclerViewGroup.setAdapter(groupChatAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}