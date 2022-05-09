package ChatApp.android.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import ChatApp.android.databinding.ActivityConfirmPhoneOtpBinding;
import in.aabhasjindal.otptextview.OTPListener;
import in.aabhasjindal.otptextview.OtpTextView;

public class ConfirmPhoneOTP extends AppCompatActivity {

    private ActivityConfirmPhoneOtpBinding binding;
    FirebaseAuth auth;
    String verificationID;
    private OtpTextView otpTextView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        binding=ActivityConfirmPhoneOtpBinding.inflate(getLayoutInflater());




        setContentView(binding.getRoot());
        String phoneNumber=getIntent().getStringExtra("phoneNumber");
        binding.txtPhoneNumber.setText(phoneNumber);
        auth=FirebaseAuth.getInstance();
        PhoneAuthOptions options=PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(ConfirmPhoneOTP.this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {

                    }

                    @Override
                    public void onCodeSent(@NonNull String verifyID, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(verifyID, forceResendingToken);
                        verificationID=verifyID;
                    }
                }).build();
        PhoneAuthProvider.verifyPhoneNumber(options);
       otpTextView=binding.otpView;
       otpTextView.setOtpListener(new OTPListener(){
           @Override
           public void onInteractionListener() {
               // fired when user types something in the Otpbox
           }
           @Override
           public void onOTPComplete(String otp) {
               // fired when user has entered the OTP fully.
               PhoneAuthCredential credential=PhoneAuthProvider.getCredential(verificationID,otp);
               auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                   @Override
                   public void onComplete(@NonNull Task<AuthResult> task) {
                       if(task.isSuccessful()){
                           Toast.makeText(ConfirmPhoneOTP.this, "Verified", Toast.LENGTH_SHORT).show();
                       }
                       else{
                           Toast.makeText(ConfirmPhoneOTP.this, "Failed Verification Code", Toast.LENGTH_SHORT).show();
                       }
                   }
               });

           }
       });
    }

}