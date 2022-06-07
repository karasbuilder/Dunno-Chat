package ChatApp.android.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

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

    FirebaseAuth auth;
    User user;

    private ProgressDialog progressDialog;
    //setting permission constant
    private static final int CAMERA_REQUEST_CODE=100;
    private static final int STORAGE_REQUEST_CODE=200;
    private static final int IMAGE_PICK_GALLERY_CODE=300;
    private static final int IMAGE_PICK_CAMERA_CODE=400;

    //array permision
    String cameraPermission[];
    String storagePermission[];




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        cameraPermission=new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    public void showEditProfileImage(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        //setting options
        String options[]={"View Cover","Take photo","Choose from gallery"};
        builder.setTitle("Cover");
        builder.setItems(options,((dialog, which) -> {
            if (which==0){
                Toast.makeText(this, "This function during updated", Toast.LENGTH_SHORT).show();

            }
            else  if(which==1){
                //take photo now from camera
            }
            else if(which==2){
                //load from gallery

            }
        }));
        builder.create().show();
    }

    //setting function get image from camera
    public void getImageFromCamera(){

    }
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_male:
                if (checked)
                    // male gender
                    break;
            case R.id.radio_female:
                if (checked)
                    // female gender
                    break;
        }
    }
}