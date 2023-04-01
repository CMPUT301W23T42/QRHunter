package com.example.qrhunter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qrhunter.generators.QrCodeImageGenerator;
import com.example.qrhunter.generators.QrCodeNameGenerator;
import com.example.qrhunter.generators.QrCodeScoreGenerator;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.hash.Hashing;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.acl.Owner;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Creates a dialog fragment that shows the user the
 * name, score, and picture of the QRCode that they just scanned
 * Also has checkbox to save location of QRCode and to take a picture of the QRCode
 */
public class QrCodeOnAddDialog extends DialogFragment {
    String hash;
    Activity activity;
    String owner;
    SimpleDateFormat simpleDateFormat;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage fbs = FirebaseStorage.getInstance();
    String TAG = "Add QR Dialog";
    int totalPhotoNumber = 0;
    TextView photoNumberText;
    ImageDisplayAdapter imageDisplayAdapter;

    ArrayList<byte[]> byteArrayList = new ArrayList<>();

    public QrCodeOnAddDialog(String hash, Activity activity,String owner) {
        this.hash = hash;
        this.activity = activity;
        this.owner = owner;
    }

    /**
     * This method get the current location of the user.
     * @return
     * Return the geopoint of current location
     */
    @Nullable
    public GeoPoint getLocation() {
        //      Location location = null;
        LocationManager locationManager = (LocationManager) getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) {
            return null;
        }
        Location location = null;


        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 1000L, 0f, new LocationListener() {

                @Override
                public void onLocationChanged(@NonNull Location location) {
                }
            });
        }
        List<String> providers = locationManager.getProviders(true);
        for (String provider : providers) {
            @SuppressLint("MissingPermission") Location l = locationManager.getLastKnownLocation(provider);
            if (l == null){
                continue;
            }
            if (location == null||location.getAccuracy() < l.getAccuracy()){
                location = l;
            }
        }
        if (location!= null){
            GeoPoint geoPoint = new GeoPoint(location.getLatitude(),location.getLongitude());
            return geoPoint;
        }else{
            return null;
        }
    }





    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View add_qr_dialog_fragment = inflater.inflate(R.layout.add_qr_dialog, null);
        builder.setView(add_qr_dialog_fragment);

        ImageView qrFrame = add_qr_dialog_fragment.findViewById(R.id.qr_add_dialog_frame);
        ImageView qrRest = add_qr_dialog_fragment.findViewById(R.id.qr_add_dialog_rest);
        ImageView qrSquare = add_qr_dialog_fragment.findViewById(R.id.qr_add_dialog_square);
        TextView nameText = add_qr_dialog_fragment.findViewById(R.id.qr_add_dialog_nameandtext);
        TextView scoreText = add_qr_dialog_fragment.findViewById(R.id.qr_add_dialog_score);
        CheckBox locationCheckBox = add_qr_dialog_fragment.findViewById(R.id.qr_add_dialog_storelocation_checkbox);
        Button photoButton = add_qr_dialog_fragment.findViewById(R.id.qr_add_photo_button);
        photoNumberText = add_qr_dialog_fragment.findViewById(R.id.qr_add_dialog_totalimage);

        RecyclerView recyclerView = add_qr_dialog_fragment.findViewById(R.id.qr_add_dialog_image_display_recylerview);
        imageDisplayAdapter = new ImageDisplayAdapter(byteArrayList);
        recyclerView.setAdapter(imageDisplayAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(),LinearLayoutManager.HORIZONTAL,false));
        imageDisplayAdapter.notifyDataSetChanged();

        QrCodeImageGenerator imageGenerator = new QrCodeImageGenerator();
        imageGenerator.setQRCodeImage(hash, qrFrame, qrRest, qrSquare);
        QrCodeNameGenerator nameGenerator = new QrCodeNameGenerator();
        String name = nameGenerator.createQRName(hash);
        nameText.setText(name);
        QrCodeScoreGenerator scoreGenerator = new QrCodeScoreGenerator();
        scoreText.setText("Score: ".concat(Integer.toString(scoreGenerator.score_algorithm(hash))));



        builder
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        GeoPoint geoPoint = null;
                        if (locationCheckBox.isChecked()){
                            geoPoint =  getLocation();
                        }
                   //     System.out.println(geoPoint);
                        // Generate hash from qrcode contents
                        QrCodeScoreGenerator scoreGenerator = new QrCodeScoreGenerator();
                        int score = scoreGenerator.score_algorithm(hash);

                        QrCodeNameGenerator nameGenerator = new QrCodeNameGenerator();
                        String codeName = nameGenerator.createQRName(hash);
                        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        String date = simpleDateFormat.format(new Date());
                        System.out.println(date);

                        // Put QRCode into database
                        Map<String, Object> QRInfo = new HashMap<>();
                        QRInfo.put("name", codeName);
                        QRInfo.put("date", date);
                        QRInfo.put("hash", hash);
                        QRInfo.put("owner", owner);
                        QRInfo.put("location", geoPoint);
                        QRInfo.put("score", score);
                        CollectionReference CodeList = db.collection("CodeList");
                        String id = CodeList.document().getId();
                        db.collection("CodeList").document(id).set(QRInfo)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d(TAG,"Add qrinfo success");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG,"Add qrinfo fail"+e);
                                    }
                                });

                        for (int i = 0;i<byteArrayList.size();i++){
                            firebaseUploadBitmap(byteArrayList.get(i),id,i);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArrayList.get(i),0,byteArrayList.get(i).length);
                            saveImage(bitmap,id);
                        }

                        Context context = getActivity().getApplicationContext();
                        db.collection("Users").document(Settings.Secure.getString(
                                        context.getContentResolver(),
                                        Settings.Secure.ANDROID_ID)).get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        Map<String, Object> userData = documentSnapshot.getData();
                                        if (userData != null) {
                                            userData.put("score",
                                                    (userData.containsKey("score")) ?
                                                            (Math.toIntExact(
                                                                    (long)userData.get("score")
                                                            ) + score) : score);
                                            db.collection("Users").document(
                                                    Settings.Secure.getString(
                                                            context.getContentResolver(),
                                                            Settings.Secure.ANDROID_ID)).set(userData);
                                        } else {
                                            Log.d(TAG, "Error retrieving user data");
                                        }
                                    }
                                });
                        Toast toast = new Toast(context);
                        toast.setText("QR Code Added successfully!");
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.show();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {}
        });


        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, CameraActivity.class);
                activityResultLauncher.launch(intent);

            }
        });
        return builder.create();
    }

    // Receive the image result from camera activity.
    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == 111){
                        Intent intent = result.getData();
                        byte[] byteArray = intent.getByteArrayExtra("image");
                        byteArrayList.add(byteArray);
                        totalPhotoNumber++;
                        photoNumberText.setText(String.valueOf(totalPhotoNumber)+" photos in total");
                        imageDisplayAdapter.notifyDataSetChanged();
                   //     Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);

                    }
                }
            });

    /**
     * Upload image into firebaseStorage
     * @param data
     * The data of image in byte[]
     * @param id
     * The id of the QR Code.
     * @param index
     * The current index of image in the arraylist.
     */
    private void firebaseUploadBitmap(byte[] data,String id,int index) {
        StorageReference imageStorage = fbs.getReference();
        StorageReference imageRef = imageStorage.child("images/" + id + "/image"+ String.valueOf(index));


        UploadTask uploadTask = imageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"Image storage failed"+e);
            }
        });
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            }
        });
    }



    // reference: https://stackoverflow.com/a/64262046

    /**
     * Save a image locally into storage.
     * @param bitmap
     * The image to store.
     * @param docName
     * The QR code id to store.
     */
    private void saveImage(Bitmap bitmap, String docName) {
        Context context = getActivity().getApplicationContext();
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            ContentValues values = contentValues();
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + getString(R.string.app_name) + '/' + docName);
            values.put(MediaStore.Images.Media.IS_PENDING, true);

            Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (uri != null) {
                try {
                    saveImageToStream(bitmap, context.getContentResolver().openOutputStream(uri));
                    values.put(MediaStore.Images.Media.IS_PENDING, false);
                    context.getContentResolver().update(uri, values, null, null);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        } else {
            String url = Environment.getExternalStorageDirectory().toString() + '/' + getString(R.string.app_name) + '/' + docName;
            File directory = new File(url);

            if (!directory.exists()) {
                directory.mkdirs();
            }
            String fileName = System.currentTimeMillis() + ".png";
            File file = new File(directory, fileName);
            try {
                saveImageToStream(bitmap, new FileOutputStream(file));
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
                context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * Put information of iamge into values.
     * @return
     * Return contentValues with image info.
     */
    private ContentValues contentValues() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        }
        return values;
    }

    /**
     * Save iamge to the output stream.
     * @param bitmap
     * The bitmap for the image
     * @param outputStream
     * The stream to output
     */
    private void saveImageToStream(Bitmap bitmap, OutputStream outputStream) {
        if (outputStream != null) {
            try {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
