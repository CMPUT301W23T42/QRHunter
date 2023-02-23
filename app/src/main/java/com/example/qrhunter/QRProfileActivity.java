package com.example.qrhunter;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EventListener;
import java.util.Map;

public class QRProfileActivity extends AppCompatActivity implements AddCommentFragment.AddCommentDialogListener{

    ListView sameQRList;
    SameQRCodeAdapter sameQRAdapter;
    ArrayList<QRCode> qrDataList;

    ListView qrCommentList;
    QRCommentAdapter qrCommentAdapter;
    ArrayList<QRComment> qrCommentDataList;
    Button addCommentButton;

    TextView qrName;
    TextView qrScore;
    TextView qrOwner;
    TextView qrDate;
    Button qrLocation;

    FirebaseFirestore db;
    SimpleDateFormat simpleDateFormat;

    private int QR_id;

    @Override
    public void addComment(String comment) {
        qrCommentDataList.add(new QRComment(comment,"2022-12-12","Bob"));
        qrCommentAdapter.notifyDataSetChanged();
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_profile);

        QR_id = 1;

        qrName = findViewById(R.id.QR_profile_name);
        qrOwner = findViewById(R.id.QR_profile_owner);
        qrScore = findViewById(R.id.QR_profile_score);
        qrDate = findViewById(R.id.QR_profile_date);
        qrName.setText("Name:"+"Hello");
        qrOwner.setText("Owner:"+"Bob");
        qrScore.setText("Score:"+"12");
        qrDate.setText("Date:"+"2022-12-12");

        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        db = FirebaseFirestore.getInstance();
        DocumentReference QRCodeCommentReference = db.collection("Comments").document(String.valueOf(QR_id));
        QRCodeCommentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    Collection<Object> commentsData = document.getData().values();
                    int num = commentsData.size();
                    System.out.println(commentsData);

                    for(Object commentData:commentsData){
                        Map<String,Object> comment = (Map<String,Object>) commentData;
                        qrCommentDataList.add(new QRComment(comment.get("comment").toString(),comment.get("date").toString(),
                                comment.get("user_name").toString()));
                    }
                    qrCommentAdapter.notifyDataSetChanged();
                }
            }
        });





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
