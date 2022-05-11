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

import ChatApp.android.databinding.ActivityPhoneNumberVerifyBinding;

public class PhoneNumberVerify extends AppCompatActivity {
    private ActivityPhoneNumberVerifyBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        binding=ActivityPhoneNumberVerifyBinding.inflate(getLayoutInflater());
        binding.inputPhoneNumber.requestFocus();
        setContentView(binding.getRoot());
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