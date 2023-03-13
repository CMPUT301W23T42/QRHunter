package com.example.qrhunter.qrProfile;
import android.content.Intent;
import android.location.Location;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.example.qrhunter.MainActivity;
import com.example.qrhunter.QRCode;
import com.example.qrhunter.R;
import com.example.qrhunter.generators.QrCodeImageGenerator;
import com.example.qrhunter.generators.QrCodeNameGenerator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * The mainAcitvity of the QR profile.
 */
public class QRProfileActivity extends AppCompatActivity implements AddCommentFragment.AddCommentDialogListener {

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
    ImageView qrFrame;
    ImageView qrRest;
    ImageView qrSquare;
    Button qrLocation;

    ImageView returnButton;

    FirebaseFirestore db;
    SimpleDateFormat simpleDateFormat;


    private String QR_id;


    private String TAG = "QRProfile";
    private String user_name;


    /**
     * Add comment to the database
     * @param comment
     * comment the user input.
     */
    @Override
    public void addComment(String comment) {
        db = FirebaseFirestore.getInstance();
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        DocumentReference QRCodeCommentReference = db.collection("Comments").document(String.valueOf(QR_id));
        Map<String,String> data = new HashMap<>();
        data.put("comment",comment);
        data.put("date",simpleDateFormat.format(new Date()));
        data.put("user_name",user_name);
        int size = qrCommentDataList.size();
        String commentName = "c" + String.valueOf(size+1);
        Map<String,Map> commentInfo = new HashMap<>();
        commentInfo.put(commentName,data);
        QRCodeCommentReference
                .set(commentInfo, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG,"Data added successfully!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                 @Override
                 public void onFailure(@NonNull Exception e) {
                     Log.d(TAG,"Data not added!"+e);
                 }
                 });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_profile);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        QR_id = bundle.getString("DOC_ID");
        user_name = bundle.getString("OWNER_NAME");
        System.out.println(QR_id);
        System.out.println(user_name);

        db = FirebaseFirestore.getInstance();

        returnButton = findViewById(R.id.returnButtonImage);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(QRProfileActivity.this, MainActivity.class);
                startActivity(intent1);
            }
        });

        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        // add the name and information of the chosen QR code
        qrName = findViewById(R.id.QR_profile_name);
        qrOwner = findViewById(R.id.QR_profile_owner);
        qrScore = findViewById(R.id.QR_profile_score);
        qrDate = findViewById(R.id.QR_profile_date);
        qrFrame = findViewById(R.id.qr_profile_frame);
        qrRest = findViewById(R.id.qr_profile_rest);
        qrSquare = findViewById(R.id.qr_profile_square);
        DocumentReference QRReference = db.collection("CodeList").document(QR_id);
        QRReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    Map<String, Object> QRData = document.getData();
                    String hash = QRData.get("hash").toString();
                    QrCodeNameGenerator nameGenerator = new QrCodeNameGenerator();
                    QrCodeImageGenerator imageGenerator = new QrCodeImageGenerator();
                    imageGenerator.setQRCodeImage(hash, qrFrame, qrRest, qrSquare);
                    qrName.setText(QRData.get("name").toString());
                    qrOwner.setText("Owner:"+QRData.get("owner").toString());
                    qrScore.setText("Score:"+QRData.get("score").toString());
                    qrDate.setText("Date:"+QRData.get("date").toString());

                }
            }
        });





//        DocumentReference QRCodeCommentReference = db.collection("Comments").document(String.valueOf(QR_id));
//        QRCodeCommentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if(task.isSuccessful()){
//                    DocumentSnapshot document = task.getResult();
//                    Collection<Object> commentsData = document.getData().values();
//                    int num = commentsData.size();
//                    System.out.println(commentsData);
//
//                    for(Object commentData:commentsData){
//                        Map<String,Object> comment = (Map<String,Object>) commentData;
//                        qrCommentDataList.add(new QRComment(comment.get("comment").toString(),comment.get("date").toString(),
//                                comment.get("user_name").toString()));
//                    }
//                    qrCommentAdapter.notifyDataSetChanged();
//                }
//            }
//        });









        // deal with the same QR code scanned by other player.
        QRCode qrCode = new QRCode("2022-12-12","2ca0a77816f6dce72e5c147cc2225cf1392362abaff9d70c7d40de1298de9006","hunter",null,"Roy", 60, "kkAR0oZisLLwigCNibMV");
        qrDataList = new ArrayList<>();
        qrDataList.add(qrCode);
        sameQRList = findViewById(R.id.same_QR_code_listview);
        sameQRAdapter = new SameQRCodeAdapter(this,qrDataList);
        sameQRList.setAdapter(sameQRAdapter);


        // show qr comments and deal with add comment fragment.
        qrCommentDataList = new ArrayList<>();
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


        // update the list when new comment is added.
        db.collection("Comments").document(QR_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value.getData() != null){
                    qrCommentDataList.clear();
                    Collection<Object> docAll = value.getData().values();

                    System.out.println(docAll);
                    for(Object commentData: docAll){
                        Map<String,String> comment = (Map<String, String>) commentData;
                        qrCommentDataList.add(new QRComment(comment.get("comment"),comment.get("date"),comment.get("user_name")));
                    }


                    Collections.sort(qrCommentDataList, new Comparator<QRComment>() {
                        @Override
                        public int compare(QRComment o1, QRComment o2) {
                            return -o1.getDate().compareTo(o2.getDate());
                        }
                    });

                    qrCommentAdapter.notifyDataSetChanged();
                }

            }
        });


    }


}
