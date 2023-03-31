package com.example.qrhunter.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.qrhunter.CaptureAct;
import com.example.qrhunter.MainActivity;
import com.example.qrhunter.QrCodeOnAddDialog;
import com.example.qrhunter.R;
import com.example.qrhunter.generators.QrCodeNameGenerator;
import com.example.qrhunter.generators.QrCodeScoreGenerator;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.hash.Hashing;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanIntentResult;
import com.journeyapps.barcodescanner.ScanOptions;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class handles QRCode scanning using the zxing-android-embedded library
 */
public class ScannerFragment extends Fragment{
    private final String TAG = "Scanner Fragment";
    private onCameraClose listener;
    ArrayList <String> owner_hashs = new ArrayList<>();
    FirebaseFirestore db;
    FusedLocationProviderClient client;
    SimpleDateFormat simpleDateFormat;
    String owner;
    int index = 0;
    public ScannerFragment() {
        // Required empty public constructor
    }

    public ScannerFragment(String name) {
        owner = (MainActivity.DEBUG_ROY)?"Roy":name;
    }

    public static ScannerFragment newInstance() {
        ScannerFragment fragment = new ScannerFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        db.collection("CodeList").whereEqualTo("owner",owner).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot doc: queryDocumentSnapshots) {
                    owner_hashs.add((String) doc.getData().get("hash"));
                }
            }
        });


        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                //listener.onCameraClose();
            }
        });
        scanCode();
        askPermission();

    }

    /**
     * Sets up options of QRCode camera scanner and launchs scanner
     */
    public void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Scan QRCode");
        options.setBeepEnabled(false);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }

    /**
     * Returns results of the QRCode scan.
     */
    private ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        evaluateScanResult(result);
    });


    /**
     * This method checks if app has permission for location access and ask if doesn't.
     */
    public void askPermission() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        }
    }

//    /**
//     * This method get the current location of the user.
//     * @return
//     * Return the geopoint of current location
//     */
//    @Nullable
//    public GeoPoint getLocation() {
//        //      Location location = null;
//        LocationManager locationManager = (LocationManager) getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
//        if (locationManager == null) {
//            return null;
//        }
//        Location location = null;
//
//
//        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 1000L, 0f, new LocationListener() {
//
//                @Override
//                public void onLocationChanged(@NonNull Location location) {
//                }
//            });
//        }
//        List<String> providers = locationManager.getProviders(true);
//        for (String provider : providers) {
//            @SuppressLint("MissingPermission") Location l = locationManager.getLastKnownLocation(provider);
//            if (l == null){
//                continue;
//            }
//            if (location == null||location.getAccuracy() < l.getAccuracy()){
//                location = l;
//            }
//        }
//        if (location!= null){
//            GeoPoint geoPoint = new GeoPoint(location.getLatitude(),location.getLongitude());
//            return geoPoint;
//        }else{
//            return null;
//        }
//    }

    public void setOnCameraCloseListener(onCameraClose listener) {
        this.listener = listener;
    }

    public interface onCameraClose {
        void onCameraClose();

    }

    /**
     * Goes to wallet fragment
     */
    public void goToWallet() {
        WalletFragment walletFragment = new WalletFragment(owner);
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, walletFragment);
        fragmentTransaction.commit();
    }

    /**
     * Evaluates the results of a scan
     * @param result
     */
    public void evaluateScanResult(ScanIntentResult result) {
        if (result.getContents() == null) { //User either went back or qrcode contents are empty
            goToWallet();
            return;
        }
        // Generate hash from qrcode contents
        String hash = Hashing.sha256().hashString(result.getContents(), StandardCharsets.UTF_8).toString();
        if (owner_hashs.contains(hash)) {
            evaluateQRCodeOwned();
            return;
        }
        evaluateAddQRCode(hash);
    }

    /**
     * Evaluation when qrcode hashed contents are already in the players wallet
     * Displays a dialog and goes to wallet
     */
    private void evaluateQRCodeOwned() {
        new AlertDialog.Builder(this.getActivity())
                .setTitle("QR Code already owned")
                .setNegativeButton("Continue", null)
                .show();
        goToWallet();
    }

    /**
     * Adds a qrcode to a players wallet
     * @param hash: qr code content hash
     */
    private void evaluateAddQRCode(String hash) {
        CollectionReference CodeList = db.collection("CodeList");
        owner_hashs.add(hash);

        int score = QrCodeScoreGenerator.score_algorithm(hash);

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
        QRInfo.put("score", score);
        CodeList.add(QRInfo);

        // Open dialog showing user the qrcode they just scanned
        QrCodeOnAddDialog qrAddDialog = new QrCodeOnAddDialog(hash, getActivity(), owner);
        qrAddDialog.show(getParentFragmentManager(), "QRCodeOnAddDialog");
        goToWallet();
    }







}
