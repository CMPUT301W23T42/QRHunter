package com.example.qrhunter.fragments;

import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.qrhunter.CustomList;
import com.example.qrhunter.QRCode;
import com.example.qrhunter.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class WalletFragment extends Fragment {

    ListView qrList;
    ArrayAdapter<QRCode> qrAdapter;
    ArrayList<QRCode> qrDataList;
    TextView totalPoints;
    TextView totalScanned;
    final String TAG = "Sample";
    FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wallet, container, false);

        totalScanned = view.findViewById(R.id.tv_total_scanned);
        totalPoints = view.findViewById(R.id.tv_total_points);

        qrList = view.findViewById(R.id.qr_list);
        qrDataList = new ArrayList<>();
        qrAdapter = new CustomList(this.getActivity(), qrDataList);

        CollectionReference collectionReference = db.collection("CodeList");

        // Add Floating Action Button work
        // Image Stuff

        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                qrDataList.clear();

                for (QueryDocumentSnapshot doc: value) {
                    String ownerName = (String) doc.getData().get("owner");
                    if (ownerName.equals("Roy")) {
                        Log.d(TAG, "Show list of QR codes");
                        String id = doc.getId();
                        String date = (String) doc.getData().get("date");
                        String hash = (String) doc.getData().get("hash");
                        Location location = (Location) doc.getData().get("location");
                        String name = (String) doc.getData().get("name");
                        String owner = (String) doc.getData().get("owner");
                        int score = (int) doc.getData().get("score");

                        qrDataList.add(new QRCode(date, hash, name, location, owner, score, id));
                    }
                }
                qrAdapter.notifyDataSetChanged();
            }
        });

        totalPoints.setText(countPoints());
        totalScanned.setText(qrDataList.size());

        qrList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                String id = qrAdapter.getItem(i).getId();
                deleteData(id);
                return true;
            }
        });

        qrList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Open QR profile fragment
            }
        });

        return view;
    }

    private void deleteData(String id){
        db.collection("CodeList").document(id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }

    private int countPoints() {
        int points = 0;
        for (int i = 0; i < qrDataList.size(); i++) {
            points += qrDataList.get(i).getScore();
        }
        return points;
    }
}