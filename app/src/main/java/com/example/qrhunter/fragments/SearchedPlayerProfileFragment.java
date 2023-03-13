package com.example.qrhunter.fragments;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.qrhunter.R;
import com.example.qrhunter.searchPlayer.QRCodeAdapter;
import com.example.qrhunter.searchPlayer.QRCodeListItem;
import com.example.qrhunter.searchPlayer.SearchAdapter;
import com.example.qrhunter.searchPlayer.UserListItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SearchedPlayerProfileFragment extends Fragment {

    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    final CollectionReference collectionReference = db.collection("CodeList");
    TextView usernameTextView;
    ArrayList<QRCodeListItem> qrCodes;
    ArrayAdapter qrCodeAdapter;
    ListView qrCodeListView;

    public SearchedPlayerProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_searched_player_profile, container, false);

        Bundle bundle = getArguments();
        String username = bundle.getString("username");

        usernameTextView = view.findViewById(R.id.username_text);
        qrCodeListView = view.findViewById(R.id.qr_codes_list_view);

        usernameTextView.setText(username);

        qrCodes = new ArrayList<QRCodeListItem>();

        collectionReference.whereArrayContains("owner", username).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String name = document.getString("name");
                        int score = Integer.parseInt(String.valueOf(document.getLong("score")));

                        qrCodes.add(new QRCodeListItem(name, score));
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }

                qrCodeAdapter = new QRCodeAdapter(getContext(), qrCodes);
                qrCodeListView.setAdapter(qrCodeAdapter);
            }
        });


        return view;
    }
}
