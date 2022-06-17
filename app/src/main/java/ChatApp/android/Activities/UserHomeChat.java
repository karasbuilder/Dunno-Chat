package ChatApp.android.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import androidx.fragment.app.FragmentTransaction;



import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import ChatApp.android.Fragments.AccountDetail;
import ChatApp.android.Fragments.ContactUser;
import ChatApp.android.Fragments.ConversationUser;
import ChatApp.android.Fragments.Notification;
import ChatApp.android.Fragments.Timelines;
import ChatApp.android.Model.User;
import ChatApp.android.R;

import ChatApp.android.databinding.ActivityUserHomeChatBinding;


public class UserHomeChat extends AppCompatActivity {
    private ActivityUserHomeChatBinding binding;

    MenuItem ButtonScanQRCode;
    User user;
    ConversationUser conversationUserFragment=new ConversationUser();
    AccountDetail accountDetailFragment=new AccountDetail();
    ContactUser contactUserFragment=new ContactUser();
    Timelines timelinesFragment=new Timelines();
    Notification notificationFragment=new Notification();
    BottomNavigationView bottomNavigationView;

    LinearLayout searchBar;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityUserHomeChatBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_user_home_chat);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,conversationUserFragment).commit();
        bottomNavigationView=findViewById(R.id.bottomNavigationView);

//        String token = FirebaseMessaging.getInstance().getToken().toString();
//        Log.d("TOKEN",token);
//        FirebaseMessaging.getInstance().deleteToken();
//            FirebaseMessaging.getInstance().getToken()
//                .addOnCompleteListener(task -> {
//                            if (task.isSuccessful() && task.getResult() != null) {
//                        NotificationModel nm = new NotificationModel(task.getResult(),"test","haha");
//                        new PushNotificationSender().execute(nm);
//                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//                        FirebaseDatabase.getInstance().getReference("users").child(uid).child("token").setValue(task.getResult());
//                                Log.d("TOKEN", task.getResult());
//                            }
//                        });

        toolbar=findViewById(R.id.customToolbar);
        setSupportActionBar(toolbar);
        toolbar.setOverflowIcon(ContextCompat.getDrawable(this,R.drawable.ic_baseline_add_24));

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.ButtonQrScanMenu:
                        Intent i = new Intent(UserHomeChat.this,ScanQrCode.class);
                        startActivity(i);
                        return true;
                    case R.id.btnCreateGroupMenu:
                        Intent intent=new Intent(UserHomeChat.this,GroupCreative.class);
                        startActivity(intent);
                        return true;



                }
                return false;
            }
        });
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(UserHomeChat.this,SearchUser.class);
                startActivity(intent);
            }

        });

      bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
          @Override
          public boolean onNavigationItemSelected(@NonNull MenuItem item) {
              switch (item.getItemId()){
                  case R.id.message:
                      toolbar.getMenu().findItem(R.id.btnAddNewContactMenu).setVisible(false);
                      getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,conversationUserFragment).commit();
                      return  true;
                  case R.id.contact:
                      toolbar.getMenu().findItem(R.id.btnAddNewContactMenu).setVisible(true);
                      getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,contactUserFragment).commit();
                      return true;
                  case R.id.notification:
                      toolbar.getMenu().findItem(R.id.btnAddNewContactMenu).setVisible(false);
                      getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,notificationFragment).commit();
                      return true;

                  case R.id.timeline:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,timelinesFragment).commit();
                      return true;
                  case R.id.account:
                      getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,accountDetailFragment).commit();
                      return  true;
              }
              return false;
          }
      });
      //onScanQRCode();
      onSearchUsers();
}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_home,menu);
        menu.findItem(R.id.btnAddNewContactMenu).setVisible(false);
        return super.onCreateOptionsMenu(menu);

    }
    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, fragment);

        transaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.ButtonQrScanMenu:
                Intent i = new Intent(UserHomeChat.this,ScanQrCode.class);
                startActivity(i);
                return true;
            case R.id.btnCreateGroupMenu:



        }
        return super.onOptionsItemSelected(item);
    }

   /* private void onScanQRCode()
    {
        ButtonScanQRCode = findViewById(R.id.ButtonQrScanMenu);
        ButtonScanQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserHomeChat.this,ScanQrCode.class);
                startActivity(i);
            }
        });
    }*/

    //search event
    private void onSearchUsers(){

    }

    //when user use application => status of user will online
    @Override
    protected void onStart() {
        super.onStart();
    }
    //when user stop use application => status offline
    @Override
    protected void onStop() {
        super.onStop();
    }

    //Lose edittext focus when click outside
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    Log.d("focus", "touchevent");
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

}