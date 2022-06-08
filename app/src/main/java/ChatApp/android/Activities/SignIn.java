package ChatApp.android.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ChatApp.android.databinding.ActivitySignInBinding;

public class SignIn extends AppCompatActivity {
    private ActivitySignInBinding binding;

    //initialize firebase instance
    private FirebaseAuth firebaseAuth;

    TextView forgotPassword,signUpBtn,signInBtn;
    ImageView signINSMSBtn,signInGoogleBtn;

    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth=FirebaseAuth.getInstance();

        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Loading..........");

        //initialize component in UI
        forgotPassword=binding.txtRecoverPassword;
        signUpBtn=binding.signUpOpen;
        signInBtn=binding.btnSignIn;

        signINSMSBtn=binding.signInSMSPhone;
        signInGoogleBtn=binding.signInGoogleBtn;


        //back to home by back button
        binding.btnBackFromSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //login event
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //login by email and password
                String email=binding.emailUserSignIn.getText().toString();
                String password=binding.passwordUserSignIn.getText().toString().trim();
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    //when email type invalidate
                    binding.emailUserSignIn.setError("Your email Invalid");
                    binding.emailUserSignIn.setFocusable(true);
                }
                else{

                    loginWithEmailPassword(email,password);
                }


            }
        });
        //Sign In using phone number SMS verification

        //recover password by email




    }
    public void loginWithEmailPassword(String email,String password){
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            progressDialog.dismiss();
                            //after login sucess => load to user page
                            Intent intent=new Intent(SignIn.this,UserHomeChat.class);
                            startActivity(intent);
                            finish();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignIn.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                });

    }

}