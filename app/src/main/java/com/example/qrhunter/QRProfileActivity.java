package com.example.qrhunter;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class QRProfileActivity extends AppCompatActivity implements AddCommentFragment.AddCommentDialogListener{

    ListView sameQRList;
    SameQRCodeAdapter sameQRAdapter;
    ArrayList<QRCode> qrDataList;

    ListView qrCommentList;
    QRCommentAdapter qrCommentAdapter;
    ArrayList<QRComment> qrCommentDataList;
    Button addCommentButton;

    @Override
    public void addComment(String comment) {
        qrCommentDataList.add(new QRComment(comment,"2022-12-12","Bob"));
        qrCommentAdapter.notifyDataSetChanged();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_profile);

        QRCode qrCode = new QRCode("hunter","2022-12-12","bob",1,null);
        qrDataList = new ArrayList<>();
        qrDataList.add(qrCode);
        sameQRList = findViewById(R.id.same_QR_code_listview);
        sameQRAdapter = new SameQRCodeAdapter(this,qrDataList);
        sameQRList.setAdapter(sameQRAdapter);



        QRComment qrComment = new QRComment("Nice QR code","2022-12-12","Bob");
        qrCommentDataList = new ArrayList<>();
        qrCommentDataList.add(qrComment);
        qrCommentList = findViewById(R.id.comment_listview);
        qrCommentAdapter = new QRCommentAdapter(this,qrCommentDataList);
        qrCommentList.setAdapter(qrCommentAdapter);

        addCommentButton = findViewById(R.id.add_comment_button);
        addCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AddCommentFragment().show(getSupportFragmentManager(), "Add Comment");
            }
        });

    }


}
