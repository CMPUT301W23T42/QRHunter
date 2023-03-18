package com.example.qrhunter.searchPlayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.qrhunter.R;
import com.example.qrhunter.generators.QrCodeImageGenerator;

import java.util.ArrayList;

/** Custom adapter made to implement a custom listview in searched player's profile with name of QR code and its score **/
public class QRCodeAdapter extends ArrayAdapter<QRCodeListItem> {

    private TextView qrCodeNameTextView;
    private TextView qrCodeScoreTextView;
    private ImageView frame, rest, square;
    QrCodeImageGenerator qrCodeImageGenerator = new QrCodeImageGenerator();

    public QRCodeAdapter(Context context, ArrayList<QRCodeListItem> qrCodeArrayList){
        super(context, 0, qrCodeArrayList);
    }
    /**
     * getView method infaltes a view passed a parameter and returns the view for each element of the listview.
     * @param position position of each item within adapters dataset
     * @param convertView the old view to reuse
     * @param parent The parent view that will eventually be attched to
     * **/
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
        frame = view.findViewById(R.id.qr_frame);
        rest = view.findViewById(R.id.qr_rest);
        square = view.findViewById(R.id.qr_square);

        qrCodeImageGenerator.setQRCodeImage(qrCodeListItem.getHash(), frame, rest, square);
        qrCodeNameTextView.setText(qrCodeListItem.getName());
        qrCodeScoreTextView.setText(String.valueOf(qrCodeListItem.getScore()));

        return view;
    }
}
