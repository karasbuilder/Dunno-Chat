package ChatApp.android.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import ChatApp.android.databinding.ActivitySearchUserBinding;

public class SearchUser extends AppCompatActivity {
    private ActivitySearchUserBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySearchUserBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

    }
}