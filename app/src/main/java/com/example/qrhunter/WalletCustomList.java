package com.example.qrhunter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.qrhunter.generators.QrCodeImageGenerator;
import com.example.qrhunter.generators.QrCodeNameGenerator;

import java.util.ArrayList;

public class WalletCustomList extends ArrayAdapter<QRCode> {
    private ArrayList<QRCode> codes;
    private Context context;

    public WalletCustomList(Context context, ArrayList<QRCode> codes){
        super(context,0, codes);
        this.codes = codes;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        return super.getView(position, convertView, parent);
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.wallet_content, parent,false);
        }

        QRCode code = codes.get(position);

        TextView qrName = view.findViewById(R.id.tv_qr_name);
        TextView qrPoints = view.findViewById(R.id.tv_wallet_points);
        ImageView qrFrame = view.findViewById(R.id.qr_wallet_frame);
        ImageView qrRest = view.findViewById(R.id.qr_wallet_rest);
        ImageView qrSquare = view.findViewById(R.id.qr_wallet_square);
        qrName.setText(code.getName());
        qrPoints.setText(Integer.toString(code.getScore()));

        QrCodeImageGenerator imageGenerator = new QrCodeImageGenerator();
        imageGenerator.setQRCodeImage(code.getHash(), qrFrame, qrRest, qrSquare);
        // image stuff

        return view;
    }
}
