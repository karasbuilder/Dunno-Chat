package ChatApp.android.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import ChatApp.android.databinding.ActivitySignInBinding;

public class SignIn extends AppCompatActivity {
    ActivitySignInBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}