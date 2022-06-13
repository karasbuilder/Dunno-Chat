package ChatApp.android.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.hbb20.CountryCodePicker;

import ChatApp.android.MainActivity;
import ChatApp.android.databinding.ActivityPhoneNumberVerifyBinding;

public class PhoneNumberVerify extends AppCompatActivity {
    private ActivityPhoneNumberVerifyBinding binding;
    CountryCodePicker mcountrycodepicker;
    String countryCode;

    FirebaseAuth auth;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding=ActivityPhoneNumberVerifyBinding.inflate(getLayoutInflater());
        mcountrycodepicker=binding.countrycodepicker;
        setContentView(binding.getRoot());

        //check if the auth has already
        auth = FirebaseAuth.getInstance();

       /* if(auth.getCurrentUser() != null) {
            Intent intent = new Intent(PhoneNumberVerify.this, UserHomeChat.class);
            startActivity(intent);
            finish();
        }*/

        countryCode=mcountrycodepicker.getSelectedCountryCodeWithPlus();

        mcountrycodepicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                countryCode=mcountrycodepicker.getSelectedCountryCodeWithPlus();
            }
        });

        binding.inputPhoneNumber.requestFocus();
        String password=getIntent().getStringExtra("passwordUser");
        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent itIntent=new Intent(PhoneNumberVerify.this,ConfirmPhoneOTP.class);
                itIntent.putExtra("phoneNumber",countryCode+binding.inputPhoneNumber.getText().toString());
                itIntent.putExtra("passwordUser",password);
                startActivity(itIntent);

            }
        });
    }
}