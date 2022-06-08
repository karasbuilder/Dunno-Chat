package ChatApp.android.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import ChatApp.android.databinding.ActivitySignInBinding;

public class SignIn extends AppCompatActivity {
    private ActivitySignInBinding binding;

    TextView signinBtn;
    EditText email, password;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //back to home by back button
        binding.btnBackFromSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //signInButton();
    }


//    private void signInButton()
//    {
//        signinBtn = binding.btnSignIn;
//        email = binding.emailUserSignIn;
//        password = binding.passwordUserSignIn;
//        signinBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String s_email = email.getText().toString();
//                String s_pass = password.getText().toString();
//                auth = FirebaseAuth.getInstance();
//                auth.signInWithEmailAndPassword(s_email,s_pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        Toast.makeText(SignIn.this, "Login success", Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(SignIn.this, UserHomeChat.class);
//                        startActivity(intent);
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(SignIn.this, "Check the information again", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        });
//    }
}