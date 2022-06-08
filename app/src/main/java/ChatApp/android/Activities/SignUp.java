package ChatApp.android.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import ChatApp.android.databinding.ActivitySignInBinding;
import ChatApp.android.databinding.ActivitySignUpBinding;

public class SignUp extends AppCompatActivity {
    private ActivitySignUpBinding binding;

    //Initialize firebase auth instance
    FirebaseAuth auth;
    Button nextBtn;
    EditText emailUser,passwordUser,confirmPasswordUser;
    ProgressDialog progressDialog;
    DatabaseReference reference;

    /*The idea following this
    * 1. We will get the information from fields of UI
    * 2. Check the validate and return the notification
    * 3. With next button we will register authentication with the following email , next to the next link multiple authentication with the same ID User.
    * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //set ID , initialize
        emailUser=binding.emailUser;
        passwordUser=binding.passwordUser;
        confirmPasswordUser=binding.confirmPasswordUser;
        auth=FirebaseAuth.getInstance();


        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Registering Account ......");

        //set event back button
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //set onclick for forward buttom =? after register by email => it will authentication and load the next
        // next activity is verify phone number
        //Link multiple authentication with the same UID =? Creditial Firebase
        binding.btnNextVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get the email , password
                String email=emailUser.getText().toString().trim();
                String password=passwordUser.getText().toString().trim();
                String confirmPassword=confirmPasswordUser.getText().toString().trim();

                //check the validate of input fields
                if(email.isEmpty()){
                   emailUser.requestFocus();
                   binding.emailUser.setError("This field not be Empty");
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){

                    binding.emailUser.setError("Invalid Email");
                    emailUser.setFocusable(true);
                    return;
                }

                if(password.isEmpty()){
                  passwordUser.requestFocus();
                  binding.passwordUser.setError("This field not be Empty ");
                  return;
                }
                if(password.length()<8){
                    passwordUser.setFocusable(true);
                   binding.passwordUser.setError("Your password must be length > 8 character");
                    return;
                }
                if(confirmPassword.isEmpty()){
                    confirmPasswordUser.requestFocus();
                    binding.confirmPasswordUser.setError("This field not be Empty");
                    return;
                }
                //check compare to confirm password
                if(!password.equals(confirmPassword)){
                    confirmPasswordUser.setError("Your Confirm Password not didn't match");
                    return;
                }

                else
                {
                    //Check if email is existed
                    auth.fetchSignInMethodsForEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                @Override
                                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                                    boolean isNewUser = task.getResult().getSignInMethods().isEmpty();

                                    if (isNewUser) {
                                        Log.e("TAG", "Is New User!");
                                        //
                                        String finalEmail = emailUser.getText().toString();
                                        String finalpassword=passwordUser.getText().toString();
                                        //
                                        registerAccountEmailPassword(finalEmail,finalpassword);

                                    } else {
                                        Log.e("TAG", "Is Old User!");
                                        Toast.makeText(getApplicationContext(), "Already used email", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                }
            }
        });
    }

    public void createUserDataObject(String uid)
    {
        FirebaseDatabase.getInstance().getReference().child("users").push();
        reference = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
        reference.child("addressUSer").setValue("");
        reference.child("coverImage").setValue("");
        reference.child("email").setValue("");
        reference.child("gender").setValue("");
        reference.child("name").setValue("");
        reference.child("passwordUser").setValue("");
        reference.child("phoneNumber").setValue("");
        reference.child("profileImage").setValue("");
        reference.child("token").setValue("");
        reference.child("uid").setValue(uid);
    }

    public void registerAccountEmailPassword(String email,String password){
        progressDialog.show();
        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            progressDialog.dismiss();
                            String current_uid = auth.getCurrentUser().getUid();
                            createUserDataObject(current_uid);
                            //next authentication intent
                            Intent intent=new Intent(SignUp.this,PhoneNumberVerify.class);
                            startActivity(intent);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }


}