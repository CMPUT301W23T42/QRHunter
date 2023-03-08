package com.example.qrhunter.fragments;

import android.content.DialogInterface;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.qrhunter.CaptureAct;

import com.example.qrhunter.R;
import com.example.qrhunter.generators.QrCodeNameGenerator;
import com.google.common.hash.Hashing;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.nio.charset.StandardCharsets;

public class ScannerFragment extends Fragment {
    private final String TAG = "Scanner Fragment";
    private onCameraClose listener;

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
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Result");
            Log.d(TAG, "Alert builder instantiated");
            String message = result.getContents().concat("\n");
            QrCodeNameGenerator nameGenerator = new QrCodeNameGenerator();
            String hash = Hashing.sha256().hashString(result.getContents(), StandardCharsets.UTF_8).toString();
            message = message.concat(Hashing.sha256().hashString(result.getContents(), StandardCharsets.UTF_8).toString().concat("\n"));
            message = message.concat(Integer.toString(score_algorithm(result.getContents()))).concat("\n");
            String name = nameGenerator.createQRName(hash);
            message = message.concat(name);
            builder.setMessage(message);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    listener.onCameraClose();
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    });

    public int score_algorithm(String string) {
        String SHA = Hashing.sha256().hashString(string, StandardCharsets.UTF_8).toString();
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
        return score;
    }

    public void setOnCameraCloseListener(onCameraClose listener) {
        this.listener = listener;
    }

    public interface onCameraClose {
        void onCameraClose();
    }
}