package ChatApp.android.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Menu;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import ChatApp.android.Adapters.UserAdapter;
import ChatApp.android.Model.User;
import ChatApp.android.R;
import ChatApp.android.databinding.ActivityConfirmPhoneOtpBinding;
import ChatApp.android.databinding.ActivityUserHomeChatBinding;

public class UserHomeChat extends AppCompatActivity {
    private ActivityUserHomeChatBinding binding;
    FirebaseDatabase database;
    ArrayList<User> user;
    UserAdapter userAdapter;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityUserHomeChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //load to firebase get all the current user

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu,menu);
        return super.onCreateOptionsMenu(menu);

    }
}