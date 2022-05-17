package ChatApp.android.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import ChatApp.android.databinding.ActivityChatUserScreenBinding;

public class ChatUserScreen extends AppCompatActivity {
    ActivityChatUserScreenBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       binding=ActivityChatUserScreenBinding.inflate(getLayoutInflater());
       setContentView(binding.getRoot());
    }
}