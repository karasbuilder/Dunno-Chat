package ChatApp.android.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;

import ChatApp.android.Activities.GetQrCode;
import ChatApp.android.Activities.PhoneNumberVerify;
import ChatApp.android.MainActivity;
import ChatApp.android.Model.User;
import ChatApp.android.R;
import ChatApp.android.databinding.FragmentAccountDetailBinding;
import ChatApp.android.databinding.FragmentConversationUserBinding;


public class AccountDetail extends Fragment {


    FirebaseAuth auth;
    Button btnLogOut;
    TextView txtGetQrCode;
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
        onGetQrCode();
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

    public void onGetQrCode()
    {
        txtGetQrCode = binding.textViewQrcode;
        txtGetQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = user.getName();
                    // Initialize multi format writer
                    MultiFormatWriter writer = new MultiFormatWriter();
                    // Initialize bit matrix
                    try {
                        BitMatrix matrix = writer.encode(data, BarcodeFormat.QR_CODE, 200, 200);
                        // Initialize barcode encoder
                        BarcodeEncoder encoder = new BarcodeEncoder();
                        // Initialize Bitmap
                        Bitmap bitmap = encoder.createBitmap(matrix);
                        //set bitmap on image view
                        ByteArrayOutputStream bs = new ByteArrayOutputStream();
                        Intent i = new Intent(getActivity(),GetQrCode.class);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 90, bs);
                        i.putExtra("QRCodebyteArray", bs.toByteArray());
                        i.putExtra("QRCodeInfo", data);
                        startActivity(i);

                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
            }
        });
    }

}