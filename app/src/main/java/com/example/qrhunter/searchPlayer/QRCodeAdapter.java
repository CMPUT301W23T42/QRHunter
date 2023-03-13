package com.example.qrhunter.searchPlayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.qrhunter.R;

import java.util.ArrayList;

public class QRCodeAdapter extends ArrayAdapter<QRCodeListItem> {

    private TextView qrCodeNameTextView;
    private TextView qrCodeScoreTextView;

    public QRCodeAdapter(Context context, ArrayList<QRCodeListItem> qrCodeArrayList){
        super(context, 0, qrCodeArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.qr_code_list_items, parent, false);
        } else {
            view = convertView;
        }

        QRCodeListItem qrCodeListItem = getItem(position);

        qrCodeNameTextView = view.findViewById(R.id.qr_code_name_text_view);
        qrCodeScoreTextView = view.findViewById(R.id.qr_code_score_text_view);

        qrCodeNameTextView.setText(qrCodeListItem.getName());
        qrCodeScoreTextView.setText(String.valueOf(qrCodeListItem.getScore()));

        return view;
    }
}
