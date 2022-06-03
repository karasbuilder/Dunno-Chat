package ChatApp.android.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import ChatApp.android.R;

public class GetQrCode extends AppCompatActivity {

    Button ConfirmBtn;
    ImageView QrCodeImage;
    TextView QrCodeInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_qr_code);
        RetrieveQrCodeImage();
        onConfirmButton();
    }

    private void onConfirmButton()
    {
        ConfirmBtn = findViewById(R.id.ButtonConfirm);
        ConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void RetrieveQrCodeImage()
    {
        QrCodeImage = findViewById(R.id.QRCodeImg);
        QrCodeInfo = findViewById(R.id.TextViewQrCodeInfo);
        Intent intent=getIntent();
        byte[] value = intent.getByteArrayExtra("QRCodebyteArray");
        String info = intent.getStringExtra("QRCodeInfo");
        Bitmap bitmap = BitmapFactory.decodeByteArray(value, 0, value.length);
        QrCodeImage.setImageBitmap(bitmap);
        QrCodeInfo.setText("Dunno Chat: " + info);
    }
}