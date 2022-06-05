package ChatApp.android.Activities;

import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;

import ChatApp.android.databinding.ActivityGroupChatScreenBinding;

public class GroupChatScreen extends AppCompatActivity {

    private ActivityGroupChatScreenBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityGroupChatScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}