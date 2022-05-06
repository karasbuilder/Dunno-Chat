package ChatApp.android.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.WindowManager;

import ChatApp.android.databinding.ActivityConfirmPhoneOtpBinding;

public class ConfirmPhoneOTP extends AppCompatActivity {

    private ActivityConfirmPhoneOtpBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        binding=ActivityConfirmPhoneOtpBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
    }

}