package com.example.qrhunter.fragments;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.qrhunter.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SearchPlayerFragment extends Fragment {

    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    final CollectionReference collectionReference = db.collection("Users");
    ListView playerListView;
    ArrayList<String> usernames;
    ArrayAdapter<String> usernamesArrayAdapter;

    public SearchPlayerFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        playerListView = view.findViewById(R.id.player_list_list_view);

        usernames = new ArrayList<String>();

        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String username = document.getString("UserName");
                        usernames.add(username);
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }

                usernamesArrayAdapter = new ArrayAdapter<String>(getContext(), R.layout.player_list_items_text_view, usernames);
                playerListView.setAdapter(usernamesArrayAdapter);
            }
        });

        return view;


    }

}
