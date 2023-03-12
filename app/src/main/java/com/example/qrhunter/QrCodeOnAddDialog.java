package com.example.qrhunter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.qrhunter.generators.QrCodeImageGenerator;
import com.example.qrhunter.generators.QrCodeNameGenerator;
import com.example.qrhunter.generators.QrCodeScoreGenerator;

public class QrCodeOnAddDialog extends DialogFragment {
    String hash;
    Activity activity;
    public QrCodeOnAddDialog(String hash, Activity activity) {
        this.hash = hash;
        this.activity = activity;
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
        Button locationButton = add_qr_dialog_fragment.findViewById(R.id.qr_add_location_button);
        Button photoButton = add_qr_dialog_fragment.findViewById(R.id.qr_add_photo_button);

        QrCodeImageGenerator imageGenerator = new QrCodeImageGenerator();
        imageGenerator.setQRCodeImage(hash, qrFrame, qrRest, qrSquare);
        QrCodeNameGenerator nameGenerator = new QrCodeNameGenerator();
        String name = nameGenerator.createQRName(hash);
        nameText.setText(name.concat(" has been added to your inventory!"));
        QrCodeScoreGenerator scoreGenerator = new QrCodeScoreGenerator();
        scoreText.setText("Score: ".concat(Integer.toString(scoreGenerator.score_algorithm(hash))));



        builder.setNegativeButton("Nice", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {}
        });

        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, UserInfo.CameraActivity.class);
                activity.startActivity(intent);
            }
        });
        return builder.create();
    }
}
