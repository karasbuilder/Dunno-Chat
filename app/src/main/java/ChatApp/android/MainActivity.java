package ChatApp.android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import ChatApp.android.Activities.PhoneNumberVerify;
import ChatApp.android.Activities.UserHomeChat;

public class MainActivity extends AppCompatActivity {
    TextView btnSignUp,btnSignIn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();*/

        btnSignIn=findViewById(R.id.btnSignIn);
        btnSignIn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, UserHomeChat.class);
                startActivity(intent);
            }
        });
        btnSignUp=findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this, PhoneNumberVerify.class);
                startActivity(intent);

            }
        });
    }
}