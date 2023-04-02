package com.example.qrhunter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.example.qrhunter.MainActivity;
import com.example.qrhunter.R;

import java.io.ByteArrayOutputStream;

/**
 * This class allows users to take a photo of their QRCode after scanning it
 */
public class CameraActivity extends Activity {
    private ImageView image;
    private static final int CAMERA_REQUEST = 999;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_fragment);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
        image = findViewById(R.id.camera_image);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            Matrix matrix = new Matrix();
            matrix.postRotate(90); // base bitmap will be 90 degrees offset
            Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
            image.setImageBitmap(rotatedBitmap);
            Intent intent = new Intent();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            rotatedBitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            intent.putExtra("image",byteArray);
            setResult(111,intent);
        }
        finish();
    }
}