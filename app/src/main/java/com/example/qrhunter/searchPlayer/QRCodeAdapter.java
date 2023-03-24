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

import com.example.qrhunter.QRCode;
import com.example.qrhunter.R;
import com.example.qrhunter.generators.QrCodeImageGenerator;

import java.util.ArrayList;

/**
 * WalletCustomList is a class that extends ArrayAdapter to create a custom list view for displaying a list of QRCodes.
 */
public class QRCodeAdapter extends ArrayAdapter<QRCode> {
    private ArrayList<QRCode> codes;
    private Context context;

    /**
     * Constructs a new CustomList object.
     * @param context The context where the CustomList will be used.
     * @param codes The QRCodes to represent in the ListView.
     */
    public QRCodeAdapter(Context context, ArrayList<QRCode> codes){
        super(context,0, codes);
        this.codes = codes;
        this.context = context;
    }

    /**
     * Returns the view to be displayed for the specified position in the list view.
     * @param position The position of the item within the adapter's data set of the item whose view we want.
     * @param convertView The old view to reuse, if possible.
     * @param parent The parent that this view will eventually be attached to.
     * @return The view corresponding to the data at the specified position.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        return super.getView(position, convertView, parent);
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.searched_player_content, parent,false);
        }

        QRCode code = codes.get(position);

        TextView qrName = view.findViewById(R.id.tv_qr_name_searched_player);
        TextView qrPoints = view.findViewById(R.id.tv_searched_player_points);
        ImageView qrFrame = view.findViewById(R.id.qr_seached_player_frame);
        ImageView qrRest = view.findViewById(R.id.qr_searched_player_rest);
        ImageView qrSquare = view.findViewById(R.id.qr_searched_player_square);
        qrName.setText(code.getName());
        qrPoints.setText(Integer.toString(code.getScore()));

        QrCodeImageGenerator imageGenerator = new QrCodeImageGenerator();
        imageGenerator.setQRCodeImage(code.getHash(), qrFrame, qrRest, qrSquare);
        // image stuff

        return view;
    }
}
