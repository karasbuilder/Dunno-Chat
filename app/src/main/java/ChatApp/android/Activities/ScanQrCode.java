package ChatApp.android.Activities;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.VIBRATE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import ChatApp.android.Fragments.QrCodeScanResult;
import ChatApp.android.R;
import eu.livotov.labs.android.camview.ScannerLiveView;
import eu.livotov.labs.android.camview.scanner.decoder.zxing.ZXDecoder;

public class ScanQrCode extends AppCompatActivity {

    ScannerLiveView scannerLiveView;
    String current_uid;
    ProfileContactUser pcu = new ProfileContactUser();
    ZXDecoder decoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr_code);

        scannerLiveView = findViewById(R.id.camView);

        if(checkPermission()){
            Toast.makeText(this, "Permission Granted..",Toast.LENGTH_SHORT).show();
        }else{
            requestPermission();
        }

        //set behaviour for qr scan: start, stop, error, success
        scannerLiveView.setScannerViewEventListener(new ScannerLiveView.ScannerViewEventListener() {
            @Override
            public void onScannerStarted(ScannerLiveView scanner) {
                Toast.makeText(ScanQrCode.this, "Scanner Started...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onScannerStopped(ScannerLiveView scanner) {
                Toast.makeText(ScanQrCode.this, "Scanner Stopped...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onScannerError(Throwable err) {
                Toast.makeText(ScanQrCode.this, "Scanner Error Occurred Please Start Again...", Toast.LENGTH_SHORT).show();
            }

            //On success scan, direct to profile contact user activity
            @Override
            public void onCodeScanned(String scanned_data) {

                current_uid = scanned_data;
                Intent i = new Intent(getApplicationContext(), ProfileContactUser.class);
                i.putExtra("visitID", current_uid);
                startActivity(i);

            }
        });
    }

    //func for checking permission
    private boolean checkPermission(){
        int cameraPermission = ContextCompat.checkSelfPermission(getApplicationContext(),CAMERA);
        int vibratePermission = ContextCompat.checkSelfPermission(getApplicationContext(),VIBRATE);
        return cameraPermission == PackageManager.PERMISSION_GRANTED && vibratePermission == PackageManager.PERMISSION_GRANTED;
    }

    //func for requesting permission
    private void requestPermission(){
        int PERMISSION_CODE = 200;
        ActivityCompat.requestPermissions(this,new String[]{CAMERA,VIBRATE},PERMISSION_CODE);
    }

    @Override
    protected void onPause() {
        scannerLiveView.stopScanner();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        decoder = new ZXDecoder();
        decoder.setScanAreaPercent(0.8);
        scannerLiveView.setDecoder(decoder);
        scannerLiveView.startScanner();
    }

    //Dialog when user give or deny the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0){
            boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            boolean vibrationAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
            if(cameraAccepted && vibrationAccepted){
                Toast.makeText(this, "Permission Granted...", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Permission Denied \n You can't use the app without permissions", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        scannerLiveView.setScannerViewEventListener(null);
        scannerLiveView = null;
        current_uid = null;
        pcu  = null;
        decoder = null;
        Runtime.getRuntime().gc();
    }
}