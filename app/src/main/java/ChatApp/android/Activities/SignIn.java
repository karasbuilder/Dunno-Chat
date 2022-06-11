package ChatApp.android.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import ChatApp.android.databinding.ActivitySignInBinding;
import ChatApp.android.R;

public class SignIn extends AppCompatActivity {
    private ActivitySignInBinding binding;

    //initialize firebase instance
    private FirebaseAuth firebaseAuth;

    TextView forgotPassword,signUpBtn,signInBtn;
    ImageView signINSMSBtn,signInGoogleBtn;

    ProgressDialog progressDialog;

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth=FirebaseAuth.getInstance();

        progressDialog=new ProgressDialog(this);


        //initialize component in UI
        forgotPassword=binding.txtRecoverPassword;
        signUpBtn=binding.textNoAccount;
        signInBtn=binding.btnSignIn;

        signINSMSBtn=binding.signInSMSPhone;
        signInGoogleBtn=binding.signInGoogleBtn;


       /* oneTapClient = Identity.getSignInClient(this);
        signInRequest = BeginSignInRequest.builder()
                .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                        .setSupported(true)
                        .build())
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(getString(R.string.default_web_client_id))
                        // Only show accounts previously used to sign in.
                        .setFilterByAuthorizedAccounts(true)
                        .build())
                // Automatically sign in when exactly one credential is retrieved.
                .setAutoSelectEnabled(true)
                .build();*/

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
       /* signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(getString(R.string.default_web_client_id))
                        // Only show accounts previously used to sign in.
                        .setFilterByAuthorizedAccounts(true)
                        .build())
                .build();*/


        gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc=GoogleSignIn.getClient(this,gso);
        //Sign In using phone number SMS verification
        signInGoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create login google proccessing
                Intent intent=gsc.getSignInIntent();
                startActivityForResult(intent,100);

            }
        });

        //recover password by email
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recoverPasswordDiaglog();
            }
        });

        //When user don't have account
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //load to sign up user screen
                Intent intent=new Intent(SignIn.this,SignUp.class);
                startActivity(intent);
                finish();
            }
        });

    }
    public void loginWithEmailPassword(String email,String password){
        progressDialog.setTitle("Signing ..........");
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

    public  void recoverPasswordDiaglog(){
        //initialize dialog


        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        LinearLayout linearLayout=new LinearLayout(this);
        builder.setTitle("Recover Password By Email");
        //initialized new edit text for getting email of user
        EditText emailText=new EditText(this);
        emailText.setHint("Email");
        emailText.setMinEms(10);

        //set editext email to layout
        linearLayout.addView(emailText);
        linearLayout.setPadding(10,10,10,10);

        builder.setView(linearLayout);
        //user click buttons recover password
        builder.setPositiveButton("Reset Password", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String email=emailText.getText().toString().trim();
                sendRecoveryPassword(email);
            }
        });
        //user click buttons cancel
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        //dialog run show
        builder.create().show();
    }

    public void sendRecoveryPassword(String email){
        //set dialog recovery password
        progressDialog.setTitle("Recovery Password ..........");
        progressDialog.show();

        //this function will send the notification email get recovery account
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()){
                    Toast.makeText(SignIn.this, "Email sent ", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(SignIn.this, "Error sent", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(SignIn.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

      /*  if(requestCode==100){
            Task<GoogleSignInAccount> task=GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                task.getResult(ApiException.class);
                Intent intent=new Intent(SignIn.this,UserHomeChat.class);
                startActivity(intent);
                finish();

            }catch (ApiException e){
                Toast.makeText(SignIn.this, "ERROR SIGN IN GOOGLE", Toast.LENGTH_SHORT).show();
            }

        }*/


    }
        //https://www.javatpoint.com/android-firebase-authentication-google-login
}