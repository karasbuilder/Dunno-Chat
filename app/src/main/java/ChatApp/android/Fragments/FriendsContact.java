package ChatApp.android.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ChatApp.android.Adapters.RequestAdapter;
import ChatApp.android.Adapters.UserAdapter;
import ChatApp.android.Model.Contact;
import ChatApp.android.Model.Request;
import ChatApp.android.Model.User;
import ChatApp.android.R;
import ChatApp.android.databinding.FragmentFriendsContactBinding;


public class FriendsContact extends Fragment {


   FragmentFriendsContactBinding binding;
   private RecyclerView recyclerViewRequestList;

   private String currentUserId;
    FirebaseDatabase database;


    ArrayList<User> users;
    ArrayList<Request> listUserRequest;
    RequestAdapter requestAdapter;
    String currentUserID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
      binding=FragmentFriendsContactBinding.inflate(inflater,container,false);
      View view=binding.getRoot();
      recyclerViewRequestList=binding.recyclerRequest;
      onCreateRequest();
      return view;
    }
    public void onCreateRequest(){
        //get userid sent => load to users => get data

        listUserRequest=new ArrayList<>();
        users=new ArrayList<>();
        database=FirebaseDatabase.getInstance();
        currentUserID=FirebaseAuth.getInstance().getCurrentUser().getUid();

        //
      // requestAdapter=new RequestAdapter(getActivity(),users);
        requestAdapter=new RequestAdapter(getActivity(),listUserRequest);
        binding.recyclerRequest.setLayoutManager(new LinearLayoutManager(getActivity()));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        binding.recyclerRequest.setAdapter(requestAdapter);

        //
        database.getReference("Friend Received").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listUserRequest.clear();
                if(snapshot.exists()){
                    for (DataSnapshot snapshot1:snapshot.getChildren()){
                        Request request=snapshot1.getValue(Request.class);

                        listUserRequest.add(request);
                    }
                    requestAdapter.notifyDataSetChanged();


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


       /* Query query=reference.orderByChild("senderID").equalTo(currentUserID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot snapshot1:snapshot.getChildren()){
                        User user=snapshot1.getValue(User.class);
                        users.add(user);
                    }
                }
                else{
                    Toast.makeText(getActivity(), "Error load null", Toast.LENGTH_SHORT).show();
                }
                requestAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/

    }

}