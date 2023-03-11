package com.example.qrhunter.qrProfile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.qrhunter.QRCode;
import com.example.qrhunter.R;

import java.util.ArrayList;

/**
 * This calss defines an adapter for showing the same QR code in QRProfile.
 */
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

        sameQRName.setText("QRName:"+qrCode.getName());
        sameQRDate.setText("Date:"+qrCode.getDate().toString());
        sameQROwner.setText("Taken by:"+qrCode.getOwner());

        return view;
    }
}
