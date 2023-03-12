package com.example.qrhunter.fragments;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.qrhunter.searchPlayer.SearchAdapter;
import com.example.qrhunter.R;
import com.example.qrhunter.searchPlayer.UserListItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SearchPlayerFragment extends Fragment {

    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    final CollectionReference collectionReference = db.collection("Users");
    ListView playerListView;
    ArrayList<UserListItem> usernames;
    EditText searchEditText;
    SearchAdapter usernamesArrayAdapter;

    public SearchPlayerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        playerListView = view.findViewById(R.id.player_list_list_view);
        searchEditText = view.findViewById(R.id.search_profile_edit_text);


        usernames = new ArrayList<UserListItem>();

        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String username = document.getString("UserName");
                        usernames.add(new UserListItem(username, 0));
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }

                usernamesArrayAdapter = new SearchAdapter(getContext(), usernames);
                playerListView.setAdapter(usernamesArrayAdapter);
            }
        });

        // Add a text change listener to the search EditText to filter the list
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (usernamesArrayAdapter != null) {
                    usernamesArrayAdapter.getFilter().filter(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return view;
    }

}




