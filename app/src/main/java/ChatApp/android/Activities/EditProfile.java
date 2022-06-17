package ChatApp.android.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import ChatApp.android.Model.User;
import ChatApp.android.databinding.ActivityEditProfileBinding;
import ChatApp.android.R;

public class EditProfile extends AppCompatActivity {


    /* Edit Profile will include
    *  --> Dunno name user
    * --> Profile Picture
    * ---> Cover Picture
    * */

    //

    /**
     * The first thing we will get information about user ( by user ID in realtime database)
     * and setting information to edit field
     * when user click to update button
     * Update all of thing: 1. for normal fied , just easy update by updateuser .
     * 2. for email , first get id and update by function of firebase update email
     * */
    private  ActivityEditProfileBinding binding;
    FirebaseAuth auth;
    FirebaseUser currentUser;
    DatabaseReference databaseReference;
   StorageReference storageReference;
    String storagePath="Uses_imageProfile_imageCover/";
    //user in database
    User user;// user interface load in application


    private ProgressDialog progressDialog;
    //setting permission constant
    private static final int CAMERA_REQUEST_CODE=100;
    private static final int STORAGE_REQUEST_CODE=200;
    private static final int IMAGE_PICK_GALLERY_CODE=300;
    private static final int IMAGE_PICK_CAMERA_CODE=400;

    //array permision
    String cameraPermission[];
    String storagePermission[];
    private Button btnBack,btnUpdate;

    ImageView coverImage,profileImage;
    EditText editNameUser,editEmailUser,editAddressUser;
    RadioGroup radioGroup;
    RadioButton genderRadio;
    String gender;


    Uri imageProfileUri,imageCoverUri;
    Uri imageUri;
    String modeGetImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        cameraPermission=new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        //initialize id of ui
        btnBack=binding.btnBack;
        btnUpdate=binding.btbUpdateProfile;
        //load item from UI
        coverImage=binding.coverImageUser;
        profileImage=binding.profileImageUser;
        editNameUser=binding.editTextNameUser;
        editEmailUser=binding.editTextEmailUser;
        editAddressUser=binding.editTextAddressUser;

        radioGroup=binding.radioGroup;

        //initialize firebase
        auth=FirebaseAuth.getInstance();
        currentUser= auth.getCurrentUser();
        databaseReference=FirebaseDatabase.getInstance().getReference("users");
        storageReference= FirebaseStorage.getInstance().getReference();


        getCurrentUser();
        coverImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modeGetImage="cover";
                showEditProfileImage();
                binding.coverImageUser.setImageURI(imageCoverUri);
            }
        });
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modeGetImage="profile";
                showEditProfileImage();
                binding.profileImageUser.setImageURI(imageProfileUri);
            }
        });

        //radio group event
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int radioID=group.getCheckedRadioButtonId();
                genderRadio=findViewById(radioID);
                gender=genderRadio.getText().toString();
            }
        });

        //get current user to load in edit profile page

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadNewInformation();
            }
        });

        exitEditProfileScreen();
    }


    public void exitEditProfileScreen(){
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // Storage Permission
    private boolean checkStoragePermission(){
        // check if storage permission is enabled or not
        //return false if permission is not enable
        //return true if enabled
        boolean result= ContextCompat.checkSelfPermission(EditProfile.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return result;

    }
    public void requestStoragePermission(){
        ActivityCompat.requestPermissions(EditProfile.this,storagePermission,STORAGE_REQUEST_CODE);

    }
    //Camera Permission
    private boolean checkCameraPermission(){
        // check if camera permission is enabled or not
        //return false if permission is not enable
        //return true if enabled
        boolean resultCamera= ContextCompat.checkSelfPermission(EditProfile.this,Manifest.permission.CAMERA)==(PackageManager.PERMISSION_GRANTED);
        boolean result= ContextCompat.checkSelfPermission(EditProfile.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return result&&resultCamera;

    }
    public void requestCameraPermission(){
        ActivityCompat.requestPermissions(EditProfile.this,cameraPermission,CAMERA_REQUEST_CODE);


    }



    public void showEditProfileImage(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        //setting options
        String options[]={"View Cover","Take photo","Choose from gallery","Cancel"};
        builder.setTitle("Cover");
        builder.setItems(options,((dialog, which) -> {
            if (which==0){
                Toast.makeText(this, "This function during updated", Toast.LENGTH_SHORT).show();

            }
            else  if(which==1){
                //camera clicked
                if(!checkCameraPermission()){
                    requestCameraPermission();
                }
                else {
                    pickFromCamera();
                }
            }
            else if(which==2){
                if(!checkStoragePermission()){
                    requestStoragePermission();

                }
                else{
                    pickFromGallery();
                }



            }
            else if(which==3){
                return;
            }
        }));
        builder.create().show();
    }


    public void getCurrentUser(){


        DatabaseReference reference=databaseReference.child(currentUser.getUid());

       reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    user=snapshot.getValue(User.class);

                    editNameUser.setText(user.getName());
                    editEmailUser.setText(user.getEmail());
                    editAddressUser.setText(user.getAddressUser());
                    String getGender=user.getGender();

                    editEmailUser.setEnabled(false);
                    if(!getGender.isEmpty()) {

                        if (getGender.equals("Female")) {
                            binding.radioFemale.setChecked(true);
                        } else {
                            binding.radioMale.setChecked(true);
                        }
                    }
                    Glide.with(EditProfile.this).load(user.getProfileImage()).centerCrop().placeholder(R.drawable.avatar).into(binding.profileImageUser);
                    Glide.with(EditProfile.this).load(user.getCoverImage()).placeholder(R.drawable.wall).into(binding.coverImageUser);

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    private void pickFromCamera(){

        ContentValues values=new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Temp Description");

        imageUri=EditProfile.this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        Intent cameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        startActivityForResult(cameraIntent,IMAGE_PICK_CAMERA_CODE);

    }
    private  void pickFromGallery(){
        //pick from gallery
        Intent galleryIntent=new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,IMAGE_PICK_GALLERY_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean cameraAccepted =grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted=grantResults[1]==PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted&&writeStorageAccepted){
                        pickFromCamera();
                    }
                    else{
                       Toast.makeText(EditProfile.this,"Please  enable Camera and Storage Permission",Toast.LENGTH_SHORT).show();
                    }
                }
            }
            case STORAGE_REQUEST_CODE:{
                if(grantResults.length>0){

                    boolean writeStorageAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted){
                        pickFromGallery();
                    }
                    else{
                        Toast.makeText(EditProfile.this,"Please  enable Storage Permission",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // this method called when user press allow or deny from request permission diaglog
        //It will handle permission case (Allow & denied )


            switch (requestCode){
                case IMAGE_PICK_CAMERA_CODE:{
                    if(modeGetImage.equals("cover")){
                        imageCoverUri=data.getData();
                        Toast.makeText(this, "Modata ak"+data.getData(), Toast.LENGTH_SHORT).show();
                    }
                    else{
                        imageProfileUri=data.getData();
                    }
                }
                case IMAGE_PICK_GALLERY_CODE:{
                    if(modeGetImage.equals("cover")){
                        imageCoverUri=data.getData();

                    }
                    else{
                        imageProfileUri=data.getData();
                    }
                }
            }


        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadProfileAvatar(Uri uri,String modeChooseImage) {
        /**
         *The rule of database is only 1 image for profile image user and 1 image for cover image user
         * */
        if(uri!=null) {
            String filePath = storagePath + "" + modeChooseImage + "_" + user.getUid();
            StorageReference storageReference1 = storageReference.child(filePath);
            storageReference1.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //storage image to storage database and then storage to user
                            //whe
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful()) {
                                Uri downloadUri = uriTask.getResult();
                                if (uriTask.isSuccessful()) {
                                    HashMap<String, Object> result = new HashMap<>();
                                    result.put(modeChooseImage, downloadUri.toString());
                                    databaseReference.child(user.getUid()).child(modeChooseImage).updateChildren(result)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(EditProfile.this, "Image updated Failed", Toast.LENGTH_SHORT).show();

                                                }
                                            });

                                } else {

                                }
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }


    }



    private void uploadNewInformation(){
        //First check the valid of field


        if(!user.getEmail().equals(editEmailUser.getText().toString())){
            /**
             * We need to authentication from old email
             * update email to firebase
             * then reauthentication with email new and password
             * */

        }
        /// normal field
        int checkedId=radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton=radioGroup.findViewById(checkedId);




        user.setAddressUser(editAddressUser.getText().toString());
        user.setGender(radioButton.getText().toString());
        user.setName(editNameUser.getText().toString());
        Map<String,Object> result =user.toMap();
        //upload new image for avatar and cover
        uploadProfileAvatar(imageProfileUri,"profileImage");
        uploadProfileAvatar(imageCoverUri,"coverImage");
        //set uri

        databaseReference.child(user.getUid()).updateChildren(result)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        finish();
                    }
                });
        //email field password
    }
}