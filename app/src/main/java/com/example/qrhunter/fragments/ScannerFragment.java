package com.example.qrhunter.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;

import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.qrhunter.CaptureAct;

import com.example.qrhunter.MainActivity;
import com.example.qrhunter.R;

import com.example.qrhunter.generators.QrCodeImageGenerator;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.example.qrhunter.generators.QrCodeNameGenerator;

import com.google.common.hash.Hashing;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScannerFragment extends Fragment {
    private final String TAG = "Scanner Fragment";
    private onCameraClose listener;


    FirebaseFirestore db;
    FusedLocationProviderClient client;
    SimpleDateFormat simpleDateFormat;
    String owner = "Mary Ramirez";
    int index = 0;

    public ScannerFragment() {
        // Required empty public constructor
    }

    public static ScannerFragment newInstance() {

        ScannerFragment fragment = new ScannerFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scanner, container, false);

        db = FirebaseFirestore.getInstance();
        Query query = db.collection("CodeList");
        AggregateQuery countQuery = query.count();
        countQuery.get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                if (task.isSuccessful()) {
                    AggregateQuerySnapshot snapshot = task.getResult();
                    System.out.println(snapshot.getCount());
                    index = (int) snapshot.getCount();
                    System.out.println(index);
                }
            }
        });


        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                listener.onCameraClose();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        scanCode();
        askPermission();
    }

    public void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(false);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }


    private ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {

        if (result.getContents() != null) {

            GeoPoint geoPoint = getLocation();
            System.out.println(geoPoint);
            String hash = Hashing.sha256().hashString(result.getContents(), StandardCharsets.UTF_8).toString();
            int score = score_algorithm(hash);

            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String date = simpleDateFormat.format(new Date());
            System.out.println(date);


            QrCodeNameGenerator nameGenerator = new QrCodeNameGenerator();
            String codeName = nameGenerator.createQRName(hash);
            Map<String, Object> QRInfo = new HashMap<>();
            QRInfo.put("name", codeName);
            QRInfo.put("date", date);
            QRInfo.put("hash", hash);
            QRInfo.put("owner", owner);
            QRInfo.put("location", geoPoint);
            QRInfo.put("score", score);
//            QRInfo.put("id",index+1);
            CollectionReference CodeList = db.collection("CodeList");
            CodeList.add(QRInfo);
//            CodeList.document(String.valueOf(index+1))
//                    .set(QRInfo)
//                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void unused) {
//                            Log.d(TAG,"Document successfully written.");
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Log.w(TAG,"Error writing document"+e);
//                        }
//                    });


//            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//            builder.setTitle("Result");
//            Log.d(TAG, "Alert builder instantiated");
//            String message = result.getContents().concat("\n");
//            message = message.concat(Hashing.sha256().hashString(result.getContents(), StandardCharsets.UTF_8).toString().concat("\n"));
//            message = message.concat(Integer.toString(score_algorithm(result.getContents())));
//            builder.setMessage(message);
//            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    listener.onCameraClose();
//                    dialog.dismiss();
//                }
//            }).show();
        }
    });


    /**
     * This method checks if app has permission for location access and ask if doesn't.
     */
    private void askPermission() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        }
    }

    /**
     * This method get the current location of the user.
     * @return
     * Return the geopoint of current location
     */
    @Nullable
    private GeoPoint getLocation() {
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






    public int score_algorithm(String string) {
        String SHA = Hashing.sha256().hashString("BFG5DGW54", StandardCharsets.UTF_8).toString();
        System.out.print(SHA);
        //SHA = "61606b9663e7b844c189d7b30444e76ecb46b45bad279b0bebf1a23eef236f49";
        int score = 0;
        int i = 0;
        while (i < SHA.length()-1) {
            char fixedChar = SHA.charAt(i);
            int j = 1;
            while (fixedChar == SHA.charAt(i+j) && (i+j) < SHA.length()) {
                j ++;
            }
            if (j > 1) {
                int val;
                if (fixedChar != '0') {
                    val = Character.getNumericValue(fixedChar);
                } else {
                    val = 20;
                }
                score += Math.pow(val, j-1);
            }
            i += j;
        }
        System.out.print("QR CODE SCORE:");
        System.out.print(score);
        return score;
    }

    public void setOnCameraCloseListener(onCameraClose listener) {
        this.listener = listener;
    }

    public interface onCameraClose {
        void onCameraClose();
    }
}