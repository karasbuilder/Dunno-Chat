package ChatApp.android.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;

import ChatApp.android.R;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu,menu);
        return super.onCreateOptionsMenu(menu);

    }
}