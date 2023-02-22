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

public class QRCommentAdapter extends ArrayAdapter<QRComment> {
    public QRCommentAdapter(Context context, ArrayList<QRComment> qrComments) {
        super(context, 0, qrComments);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView ==null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.qr_code_comment_card,parent,false);
        } else{
            view = convertView;
        }

        QRComment qrComment = getItem(position);
        TextView qrCommentUser = view.findViewById(R.id.comment_user_name);
        TextView qrCommentText = view.findViewById(R.id.comment_user_text);
        TextView qrCommentDate = view.findViewById(R.id.comment_date);

        qrCommentUser.setText(qrComment.getUser()+":");
        qrCommentText.setText(qrComment.getComment());
        qrCommentDate.setText(qrComment.getDate());

        return view;
    }
}
