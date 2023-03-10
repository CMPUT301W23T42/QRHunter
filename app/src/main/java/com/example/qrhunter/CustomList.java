package com.example.qrhunter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.qrhunter.fragments.WalletFragment;

import java.util.ArrayList;

public class CustomList extends ArrayAdapter<QRCode> {
    private ArrayList<QRCode> codes;
    private Context context;

    public CustomList(Context context, ArrayList<QRCode> codes){
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
            view = LayoutInflater.from(context).inflate(R.layout.content, parent,false);
        }

        QRCode code = codes.get(position);

        TextView qrName = view.findViewById(R.id.tv_qr_name);
        TextView qrPoints = view.findViewById(R.id.tv_wallet_points);

//        qrName.setText(code.getName());
//        qrPoints.setText(code.getScore());
        // image stuff

        return view;
    }
}
