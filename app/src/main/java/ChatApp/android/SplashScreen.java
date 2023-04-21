package ChatApp.android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import ChatApp.android.Activities.PhoneNumberVerify;
import ChatApp.android.Activities.UserHomeChat;

public class SplashScreen extends AppCompatActivity {

    FirebaseAuth auth=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth=FirebaseAuth.getInstance();
       /* getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();*/
        setContentView(R.layout.activity_splash_screen);

        /**This function will delay the loading of application , and during that time check the current user have stayed in or not
         * if not user already sign in account => it will load to main activity
         *
         * */
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(auth.getCurrentUser()!=null){
                    //user have already stayed sign in => set up custom token for user => then loading to user home chat application
                    setTokenIfAlreadyLogin();
                    Intent intent = new Intent(SplashScreen.this, UserHomeChat.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    //user not have already account stayed sign in => return to main activity screen of application
                    Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        },3000);
    }

    private void setTokenIfAlreadyLogin() {
        //if user have already sign in , this function will create custom toke for user , this token will use in many function of application for checkcing some authentication setting
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                FirebaseDatabase.getInstance().getReference("users").child(uid).child("token").setValue(task.getResult());
                Log.d("SPLASH UID", uid);
                Log.d("SPLASH TOKEN", task.getResult());
            }
        });
    }
}