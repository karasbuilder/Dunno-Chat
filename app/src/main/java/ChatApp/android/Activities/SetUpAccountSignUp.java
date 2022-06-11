package ChatApp.android.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;
import java.util.HashMap;

import ChatApp.android.Model.User;
import ChatApp.android.databinding.ActivitySetUpAccountSignUpBinding;

import ChatApp.android.R;

public class SetUpAccountSignUp extends AppCompatActivity {
    private ActivitySetUpAccountSignUpBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;

    Uri coverImage;
    Uri selectedImage;
    ProgressDialog dialog;

    String gender;
    RadioGroup radioGroup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySetUpAccountSignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //set id initialize
        radioGroup=binding.genderSelect;


        //initialized
        dialog = new ProgressDialog(this);
        dialog.setMessage("Updating profile...");
        dialog.setCancelable(false);
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();

        //set event radio group
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton=radioGroup.findViewById(checkedId);
                gender=radioButton.getText().toString();
            }
        });

        //set cover image event
        binding.coverImageUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 45);
            }
        });

        //get image profile event
        binding.imgViewSetUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 45);
            }
        });
        //User(String uid, String name, String phoneNumber,String email,String passwordUser ,String profileImage,String coverImage)

        binding.btnSaveSetting.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //initialize attribute of user model
                String name=binding.editUserNameSetting.getText().toString();
                String address=binding.editTextAddress.getText().toString();

                String phone=auth.getCurrentUser().getPhoneNumber();
                String email=auth.getCurrentUser().getEmail();
                String uid = auth.getCurrentUser().getUid();
                String password=getIntent().getStringExtra("passwordUser");

                if(name.isEmpty()){
                    binding.editUserNameSetting.setError("Please Enter your name");
                    return;
                }
                dialog.show();

                if(selectedImage != null) {

                    //store database to storage database
                    StorageReference reference = storage.getReference().child("Profiles").child(FirebaseAuth.getInstance().getUid());
                    reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()) {
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String imageUrl = uri.toString();
                                        String coverURL="NORUl";



                                        User user = new User(uid, name, phone,email,password,imageUrl,coverURL,address,gender);
                                        Log.d("logg",user.getPasswordUser());

                                        database.getReference()
                                                .child("users")
                                                .child(uid)
                                                .setValue(user)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        dialog.dismiss();
                                                        Intent intent = new Intent(SetUpAccountSignUp.this, UserHomeChat.class);
                                                        Toast.makeText(SetUpAccountSignUp.this, "User Created Successfully", Toast.LENGTH_SHORT).show();
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                });
                                    }
                                });
                            }
                        }
                    });
                } else {



                    User user = new User(uid, name, phone,email,password,"imageUrl","coverURL",address,gender);

                    database.getReference()
                            .child("users")
                            .child(uid)
                            .setValue(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(SetUpAccountSignUp.this, UserHomeChat.class);
                                    Toast.makeText(SetUpAccountSignUp.this, "User Created Successfully", Toast.LENGTH_SHORT).show();
                                    startActivity(intent);
                                    finish();
                                }
                            });
                }

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null) {
            if(data.getData() != null) {
                Uri uri = data.getData(); // filepath
                FirebaseStorage storage = FirebaseStorage.getInstance();
                long time = new Date().getTime();
                StorageReference reference = storage.getReference().child("Profiles").child(time+"");
                reference.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()) {
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String filePath = uri.toString();
                                    HashMap<String, Object> obj = new HashMap<>();
                                    obj.put("image", filePath);
                                    database.getReference().child("users")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .updateChildren(obj).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                        }
                                    });
                                }
                            });
                        }
                    }
                });


                binding.imgViewSetUp.setImageURI(data.getData());
                selectedImage = data.getData();
            }
        }
    }

}