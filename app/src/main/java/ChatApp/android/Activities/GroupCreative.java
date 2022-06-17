package ChatApp.android.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import ChatApp.android.Model.Group;
import ChatApp.android.databinding.ActivityGroupCreativeBinding;

public class GroupCreative extends AppCompatActivity {

    //permission constants camera and storage request
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    //image pick constants
    private static final int IMAGE_PICK_CAMERA_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;

    //array permision
    private String cameraPermission[];
    private String storagePermission[];
    private Uri imageUri;

    private ActivityGroupCreativeBinding binding;
    private ImageView groupIconIV;
    private EditText groupTitle, groupDescription;
    private TextView btnCreateGroup;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase database;

    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupCreativeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        database=FirebaseDatabase.getInstance();

        //Init UI views in activity group creative
        groupIconIV = binding.imgViewGroup;
        groupTitle = binding.editTextGroupTitle;
        groupDescription = binding.editTextGroupDescription;
        btnCreateGroup = binding.textView6;
        //init array permission
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //setting progress diaglog
      progressDialog=new ProgressDialog(this);

        //pick image event
        groupIconIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickDialog();

            }
        });

        //create group event
        btnCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGroupLoad();

            }
        });
    }


    public void showImagePickDialog() {
        //options to pick image from
        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Icon Group");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    //camera clicked
                    if (!checkCameraPermission()) {
                        requestCameraPermission();
                    } else {
                        pickFromCamera();

                    }

                } else {
                    //when user click gallery
                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    } else {
                        pickFromGallery();

                    }
                }

            }
        }).show();
    }

    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickFromCamera() {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Group Image title");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Group Image Description");

        imageUri = GroupCreative.this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);

    }

    private boolean checkStoragePermission() {
        // check if storage permission is enabled or not
        //return false if permission is not enable
        //return true if enabled
        boolean result = ContextCompat.checkSelfPermission(GroupCreative.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;

    }

    public void requestStoragePermission() {
        ActivityCompat.requestPermissions(GroupCreative.this, storagePermission, STORAGE_REQUEST_CODE);

    }

    private boolean checkCameraPermission() {
        // check if camera permission is enabled or not
        //return false if permission is not enable
        //return true if enabled
        boolean resultCamera = ContextCompat.checkSelfPermission(GroupCreative.this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result = ContextCompat.checkSelfPermission(GroupCreative.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && resultCamera;

    }

    public void requestCameraPermission() {
        ActivityCompat.requestPermissions(GroupCreative.this, cameraPermission, CAMERA_REQUEST_CODE);


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAccepted) {
                        pickFromCamera();
                    } else {
                        Toast.makeText(GroupCreative.this, "Please  enable Camera and Storage Permission", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            case STORAGE_REQUEST_CODE: {
                if (grantResults.length > 0) {

                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(GroupCreative.this, "Please  enable Storage Permission", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                imageUri = data.getData();
                groupIconIV.setImageURI(imageUri);
            } else if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                imageUri = data.getData();
                groupIconIV.setImageURI(imageUri);
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    public void createGroupLoad(){

        progressDialog.setMessage("Creating Group Chat");
        String groupName=groupTitle.getText().toString();
        String groupDes=groupDescription.getText().toString();
        String key=database.getReference().push().getKey();


        if(TextUtils.isEmpty(groupName)){
            binding.editTextGroupTitle.setError("Please Fill the name of group");
            binding.editTextGroupTitle.requestFocus();
            //if the text of title null => show error
            return;
        }

        //show processing
       progressDialog.show();
        if(imageUri==null){

             Group group=new Group(key,groupName,groupDes,firebaseAuth.getCurrentUser().getUid(),System.currentTimeMillis(),"No ICON");
             addToFireBaseGroup(group,key);


        }
        else{
            String fileNamePathFirebase="Group_Images/"+"image"+key;
            StorageReference storageReference= FirebaseStorage.getInstance().getReference(fileNamePathFirebase);
            storageReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                            while(!!uriTask.isSuccessful());
                                Uri downloadURi=uriTask.getResult();
                                if(uriTask.isSuccessful()){
                                    Group group=new Group(key,groupName,groupDes,firebaseAuth.getCurrentUser().getUid(),System.currentTimeMillis(),downloadURi.toString());
                                    addToFireBaseGroup(group,key);

                                }






                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //failed and return notification

                            progressDialog.dismiss();
                            Toast.makeText(GroupCreative.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();


                        }
                    })
            ;
        ;
        }

    }
    public void addToFireBaseGroup(Group group,String key){
        database.getReference("Groups").child(key).setValue(group).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        String timeStamp=String.valueOf(System.currentTimeMillis());
                            //create participant user add to group
                        HashMap<String,String> hashMap=new HashMap<>();
                        hashMap.put("uid",firebaseAuth.getUid());
                        hashMap.put("role","creator");
                        hashMap.put("timeStamp",timeStamp);
                      DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Groups");
                              ref.child(group.getGroupID())
                                .child("Participants").child(firebaseAuth.getCurrentUser().getUid()).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                          @Override
                                          public void onSuccess(Void unused) {
                                              Toast.makeText(GroupCreative.this, "Create Group Success", Toast.LENGTH_SHORT).show();
                                              progressDialog.dismiss();
                                          }
                                      }).addOnFailureListener(new OnFailureListener() {
                                          @Override
                                          public void onFailure(@NonNull Exception e) {
                                              Toast.makeText(GroupCreative.this, "Error when create group", Toast.LENGTH_SHORT).show();
                                              progressDialog.dismiss();
                                          }
                                      });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

}