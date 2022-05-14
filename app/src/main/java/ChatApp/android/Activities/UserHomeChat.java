package ChatApp.android.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import ChatApp.android.databinding.ActivityConfirmPhoneOtpBinding;
import ChatApp.android.databinding.ActivityUserHomeChatBinding;

public class UserHomeChat extends AppCompatActivity {
    private ActivityUserHomeChatBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityUserHomeChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }
}