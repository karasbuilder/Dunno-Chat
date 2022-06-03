package ChatApp.android.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ChatApp.android.Adapters.UserAdapter;
import ChatApp.android.FloatingWidgetService;
import ChatApp.android.Fragments.AccountDetail;
import ChatApp.android.Fragments.ContactUser;
import ChatApp.android.Fragments.ConversationUser;
import ChatApp.android.GlobalStuff;
import ChatApp.android.Model.User;
import ChatApp.android.R;

import ChatApp.android.databinding.ActivityUserHomeChatBinding;
import ChatApp.android.databinding.FragmentConversationUserBinding;


public class UserHomeChat extends AppCompatActivity {
    private ActivityUserHomeChatBinding binding;

    ImageButton ButtonScanQRCode;
    User user;
    ConversationUser conversationUserFragment=new ConversationUser();
    AccountDetail accountDetailFragment=new AccountDetail();
    ContactUser contactUserFragment=new ContactUser();
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityUserHomeChatBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_user_home_chat);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,conversationUserFragment).commit();
        bottomNavigationView=findViewById(R.id.bottomNavigationView);

      bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
          @Override
          public boolean onNavigationItemSelected(@NonNull MenuItem item) {
              switch (item.getItemId()){
                  case R.id.message:
                      getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,conversationUserFragment).commit();
                      return  true;
                  case R.id.contact:
                      Toast.makeText(UserHomeChat.this, "Click to Contact", Toast.LENGTH_LONG).show();
                      getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,contactUserFragment).commit();
                      return true;
                  case R.id.account:
                      getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,accountDetailFragment).commit();
                      return  true;
              }
              return false;
          }
      });
      onScanQRCode();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu,menu);
        return super.onCreateOptionsMenu(menu);

    }
    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, fragment);

        transaction.commit();
    }

        @Override
        protected void onResume()
        {
            super.onResume();
        }

        @Override
        public void onStop() {
            super.onStop();
        }

        @Override
        protected void onDestroy() {
        super.onDestroy();
        }

    private void onScanQRCode()
    {
        ButtonScanQRCode = findViewById(R.id.ImgButtonScanQRCode);
        ButtonScanQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserHomeChat.this,ScanQrCode.class);
                startActivity(i);
            }
        });
    }
}

