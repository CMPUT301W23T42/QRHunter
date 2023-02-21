package com.example.qrhunter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class SameQRCodeAdapter extends ArrayAdapter<QRCode> {

    public SameQRCodeAdapter(Context context, ArrayList<QRCode> qrCodes) {
        super(context, 0, qrCodes);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView ==null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.same_code_scaned_player_card,parent,false);
        } else{
            view = convertView;
        }

        QRCode qrCode = getItem(position);
        TextView sameQRName = view.findViewById(R.id.same_QR_code_name);
        TextView sameQRDate = view.findViewById(R.id.same_QR_code_date);
        TextView sameQROwner = view.findViewById(R.id.same_QR_code_owner);

        sameQRName.setText(qrCode.getName());
        sameQRDate.setText(qrCode.getDate().toString());
        sameQROwner.setText(qrCode.getOwner());

        return view;
    }
}
