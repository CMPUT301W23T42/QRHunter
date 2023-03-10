package com.example.qrhunter.fragments;

import android.content.Intent;
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

import android.widget.CompoundButton;
import android.widget.ImageView;

import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.qrhunter.WalletCustomList;
import com.example.qrhunter.QRCode;
import com.example.qrhunter.QRProfileActivity;
import com.example.qrhunter.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;


public class WalletFragment extends Fragment {
    RadioGroup radioGroup;
    RadioButton descendingSort, ascendingSort;
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

        descendingSort = view.findViewById(R.id.rb_descending);
        ascendingSort = view.findViewById(R.id.rb_ascending);
        radioGroup = view.findViewById(R.id.rg_sort);

        totalScanned = view.findViewById(R.id.tv_total_scanned);
        totalPoints = view.findViewById(R.id.tv_total_points);

        qrList = view.findViewById(R.id.qr_list);
        qrDataList = new ArrayList<>();
        qrAdapter = new WalletCustomList(this.getActivity(), qrDataList);
        qrList.setAdapter(qrAdapter);

        db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("CodeList");

        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                qrDataList.clear();

                assert value != null;
                for (QueryDocumentSnapshot doc: value) {
                    String ownerName = (String) doc.getData().get("owner");
                    assert ownerName != null;
                    if (ownerName.equals("Roy")) {
                        Log.d(TAG, "Show list of QR codes");
                        String id = doc.getId();
                        String date = (String) doc.getData().get("date");
                        String hash = (String) doc.getData().get("hash");
                        GeoPoint location = (GeoPoint) doc.getData().get("location");
                        String name = (String) doc.getData().get("name");
                        String owner = (String) doc.getData().get("owner");
                        Long score = (Long) doc.getData().get("score");

                        qrDataList.add(new QRCode(date, hash, name, location, owner, Math.toIntExact(score), id));
                    }
                }
                qrAdapter.notifyDataSetChanged();
                totalPoints.setText(Integer.toString(countPoints()));
                totalScanned.setText(Integer.toString(qrDataList.size()));
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.rb_ascending) {
                    Collections.sort(qrDataList, QRCode.ascendingOrder);
                    qrAdapter.notifyDataSetChanged();
                } else if (i == R.id.rb_descending) {
                    Collections.sort(qrDataList, QRCode.descendingOrder);
                    qrAdapter.notifyDataSetChanged();
                }
            }
        });

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
                Intent intent = new Intent(getActivity(), QRProfileActivity.class);
                intent.putExtra("DOC_ID", qrDataList.get(i).getId());
                intent.putExtra("OWNER_NAME", qrDataList.get(i).getOwner());
                startActivity(intent);
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