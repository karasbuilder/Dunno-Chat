package ChatApp.android.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ChatApp.android.Activities.ProfileContactUser;
import ChatApp.android.Model.User;
import ChatApp.android.R;
import ChatApp.android.databinding.FragmentQrCodeScanResultBinding;


public class QrCodeScanResult extends Fragment {

    String scanned_uid;
    User user;
    Button cancelbtn;
    FragmentQrCodeScanResultBinding binding;
    DatabaseReference reference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentQrCodeScanResultBinding.inflate(inflater, container, false);
        View v = binding.getRoot();
        onQrCodeScanResult();
        onButtonCancel();
        return v;
    }

    public void onQrCodeScanResult()
    {
        scanned_uid = getArguments().getString("scanned_UID");
        reference= FirebaseDatabase.getInstance().getReference().child("users").child(scanned_uid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    user=snapshot.getValue(User.class);
                    binding.QrAccountName.setText(user.getName());
                    Glide.with(getActivity()).load(user.getProfileImage()).centerCrop().placeholder(R.drawable.avatar).into(binding.QrProfile);
                    Intent i = new Intent(getActivity(), ProfileContactUser.class);
                    i.putExtra("visitID", scanned_uid);
                    startActivity(i);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void onButtonCancel()
    {
        cancelbtn = binding.ButtonCancel;
        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(QrCodeScanResult.this).commit();
                getActivity().finish();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        scanned_uid = null;
        user = null;
        cancelbtn = null;
        binding = null;
        reference = null;
        cancelbtn.setOnClickListener(null);
        Runtime.getRuntime().gc();
    }
}