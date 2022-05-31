package ChatApp.android.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ChatApp.android.Activities.PhoneNumberVerify;
import ChatApp.android.MainActivity;
import ChatApp.android.Model.User;
import ChatApp.android.R;
import ChatApp.android.databinding.FragmentAccountDetailBinding;
import ChatApp.android.databinding.FragmentConversationUserBinding;


public class AccountDetail extends Fragment {


    FirebaseAuth auth;
    Button btnLogOut;
    FragmentAccountDetailBinding binding;
    User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding= FragmentAccountDetailBinding.inflate(inflater,container,false);
        View view=binding.getRoot();
        auth=FirebaseAuth.getInstance();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("users").child(auth.getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    user=snapshot.getValue(User.class);
                    binding.txtNameAccountAuth.setText(user.getName());
                    Glide.with(getActivity()).load(user.getProfileImage()).centerCrop().placeholder(R.drawable.avatar).into(binding.profile);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        System.out.println(auth.getCurrentUser().getUid());
        onSignOutAccount();
        return view;
    }
    public void onSignOutAccount(){



        btnLogOut=binding.btnLogOut;
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getActivity(), "Sign Out from App", Toast.LENGTH_SHORT).show();
                auth.signOut();
                Intent intent=new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

    }

}