package com.example.qrhunter;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class QRProfileActivity extends AppCompatActivity {

    ListView sameQRList;
    SameQRCodeAdapter sameQRAdapter;
    ArrayList<QRCode> dataList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_profile);

        QRCode qrCode = new QRCode("hunter","2022-12-12","bob",1,null);
        dataList = new ArrayList<>();
        dataList.add(qrCode);
        sameQRList = findViewById(R.id.same_QR_code_listview);
        sameQRAdapter = new SameQRCodeAdapter(this,dataList);
        sameQRList.setAdapter(sameQRAdapter);


    }
}
