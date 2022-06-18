package ChatApp.android.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Tag;

import java.util.concurrent.TimeUnit;

import ChatApp.android.MainActivity;
import ChatApp.android.Model.User;
import ChatApp.android.databinding.ActivityConfirmPhoneOtpBinding;
import in.aabhasjindal.otptextview.OTPListener;
import in.aabhasjindal.otptextview.OtpTextView;

public class ConfirmPhoneOTP extends AppCompatActivity {

    private ActivityConfirmPhoneOtpBinding binding;
    FirebaseAuth auth;
    FirebaseUser user;

    String phoneNumber;
    String verificationID;
    ProgressDialog dialog;
    DatabaseReference reference;




    private OtpTextView otpTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //intialize ui by getting binding view feature
        binding = ActivityConfirmPhoneOtpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //setting dialog when user sending otp vetification code sms
        dialog = new ProgressDialog(this);
        dialog.setMessage("Sending OTP...");
        dialog.setCancelable(false);
        dialog.show();
        //getting extra information about phone number of user in previous page
        phoneNumber = getIntent().getStringExtra("phoneNumber");
        /// binding setting phone number to showing in screen
        binding.txtPhoneNumber.setText(phoneNumber);
        //setting get current user uid because we already create account by email and password
        auth = FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();
        //phone way setting send sms verification code => documentation about way how to do it of firebase
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(ConfirmPhoneOTP.this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        //if user type wrong verification code => dismiss current dialog
                        Toast.makeText(ConfirmPhoneOTP.this, "Failed Verification Code", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }

                    @Override
                    public void onCodeSent(@NonNull String verifyID, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(verifyID, forceResendingToken);
                        dialog.dismiss();
                        verificationID = verifyID;
                        //get the verification current ID code => check it with input method manager
                        //if toggle soft input
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                        binding.otpView.requestFocus();
                    }
                }).build();
        PhoneAuthProvider.verifyPhoneNumber(options);
        otpTextView = binding.otpView;

        otpTextView.setOtpListener(new OTPListener() {
            @Override
            public void onInteractionListener() {
                // fired when user types something in the Otpbox
            }

            @Override
            public void onOTPComplete(String otp) {



               //link multiple authentication
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID, otp);
                linkCredential(credential);
                //when the otp finish ( method of library check when we complete all field of number code)



            }
        });


    }
    public void linkCredential(AuthCredential credential) {
        //we have already email passwrod register in previous screen , now we want to create the same user id => need to link multiple method
        //here is the way setting link with already credential
        auth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            dialog.setMessage("Verified");
                            dialog.setCancelable(false);
                            dialog.show();

                            FirebaseUser user=task.getResult().getUser();
                            Toast.makeText(ConfirmPhoneOTP.this, user.getEmail()+"|||"+user.getPhoneNumber(), Toast.LENGTH_SHORT).show();

                        //when complete all field => load to set up page
                            String password=getIntent().getStringExtra("passwordUser");
                            Intent intent=new Intent(ConfirmPhoneOTP.this,SetUpAccountSignUp.class);
                            intent.putExtra("passwordUser",password);
                            startActivity(intent);
                            finish();

                        } else {
                            //notification ì user data merge failed to database



                            Toast.makeText(ConfirmPhoneOTP.this, "Failed to merge" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }


}