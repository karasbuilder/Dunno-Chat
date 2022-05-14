package ChatApp.android.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import ChatApp.android.MainActivity;
import ChatApp.android.databinding.ActivityPhoneNumberVerifyBinding;

public class PhoneNumberVerify extends AppCompatActivity {
    private ActivityPhoneNumberVerifyBinding binding;
    FirebaseAuth auth;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        binding=ActivityPhoneNumberVerifyBinding.inflate(getLayoutInflater());
        binding.inputPhoneNumber.requestFocus();
        setContentView(binding.getRoot());

        //check if the auth has already
        auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser() != null) {
            Intent intent = new Intent(PhoneNumberVerify.this, UserHomeChat.class);
            startActivity(intent);
            finish();
        }


        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent itIntent=new Intent(PhoneNumberVerify.this,ConfirmPhoneOTP.class);
                itIntent.putExtra("phoneNumber","+84"+binding.inputPhoneNumber.getText().toString());
                startActivity(itIntent);

            }
        });
    }
}