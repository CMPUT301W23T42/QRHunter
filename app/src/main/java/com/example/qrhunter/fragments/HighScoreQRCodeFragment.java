package com.example.qrhunter.fragments;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class HighScoreQRCodeFragment extends Fragment {

    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    final CollectionReference collectionReference = db.collection("CodeList");
    ListView playerListView;
    ArrayList<UserListItem> usernames;
    EditText searchEditText;

    ImageView backButton;

    SearchAdapter usernamesArrayAdapter;

    public HighScoreQRCodeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_high_score, container, false);

        playerListView = view.findViewById(R.id.player_list_list_view);
        searchEditText = view.findViewById(R.id.search_profile_edit_text);

        usernames = new ArrayList<UserListItem>();

        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

            /**
             * Called when the query is able to execute, and get data from the database
             *
             * @param task Has a task object that has all the documents required
             * @return None
             */
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Map<String, Integer> highScores = new HashMap<>(); // to store the highest score for each owner
                    Map<String, String> highestScoringCodes = new HashMap<>(); // to store the highest scoring code for each owner
                    Map<String, List<String>> ownersByScore = new TreeMap<>(Collections.reverseOrder()); // to store the owners sorted by score
                    Map<String, List<String>> ownersByAlpha = new TreeMap<>(); // to store the owners sorted alphabetically

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String owner = document.getString("owner");
                        Long scoreLong = document.getLong("score");
                        int score = (scoreLong != null) ? scoreLong.intValue() : 0; // Set score to 0 if the value is null
                        String code = document.getId();

                        if (highScores.containsKey(owner)) {
                            if (score > highScores.get(owner)) {
                                highScores.put(owner, score);
                                highestScoringCodes.put(owner, code);
                            } else if (score == highScores.get(owner)) {
                                String currentCode = highestScoringCodes.get(owner);
                                if (code.compareTo(currentCode) < 0) {
                                    highestScoringCodes.put(owner, code);
                                }
                            }
                        } else {
                            highScores.put(owner, score);
                            highestScoringCodes.put(owner, code);
                        }
                    }

                    // group owners by score
                    for (Map.Entry<String, Integer> entry : highScores.entrySet()) {
                        String owner = entry.getKey();
                        int score = entry.getValue();
                        String code = highestScoringCodes.get(owner);

                        if (!ownersByScore.containsKey(String.valueOf(score))) {
                            ownersByScore.put(String.valueOf(score), new ArrayList<String>());
                        }
                        ownersByScore.get(String.valueOf(score)).add(owner + "#" + code);
                    }

                    // sort owners with same score alphabetically
                    for (Map.Entry<String, List<String>> entry : ownersByScore.entrySet()) {
                        String score = entry.getKey();
                        List<String> owners = entry.getValue();

                        Collections.sort(owners, new Comparator<String>() {
                            @Override
                            public int compare(String o1, String o2) {
                                String[] parts1 = o1.split("#");
                                String[] parts2 = o2.split("#");
                                String owner1 = parts1[0];
                                String code1 = parts1[1];
                                String owner2 = parts2[0];
                                String code2 = parts2[1];

                                if (code1.equals(code2)) {
                                    return owner1.compareToIgnoreCase(owner2);
                                } else {
                                    return code1.compareTo(code2);
                                }
                            }
                        });

                        ownersByAlpha.put(score, owners);
                    }

                    // add owners to the list adapter
                    for (Map.Entry<String, List<String>> entry : ownersByAlpha.entrySet()) {
                        List<String> owners = entry.getValue();

                        for (String owner : owners) {
                            String[] parts = owner.split("#");
                            String username = parts[0];
                            int score = Integer.parseInt(entry.getKey());
                            usernames.add(new UserListItem(username, score));
                        }
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }

                usernamesArrayAdapter = new SearchAdapter(getContext(), usernames);
                usernamesArrayAdapter.sortFilteredScores();
                usernamesArrayAdapter.notifyDataSetChanged();
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
                    Log.d("Filter", usernamesArrayAdapter.getFilter().toString());
                    usernamesArrayAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                usernamesArrayAdapter.sortOriginalScores();
                usernamesArrayAdapter.notifyDataSetChanged();
            }
        });

        //open new profile fragment on clicking list item
        playerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * Called when an item in the listview is clicked on
             *
             * @param adapterView Has a task object that has all the documents required
             * @param view The veiw of the item clciked on
             * @param i Position of the item clicked
             * @param l ID of the item clicked
             * @return None
             */
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                UserListItem usernameObj = (UserListItem) usernamesArrayAdapter.getFilteredList().get(i);
                String username = usernameObj.getUsername();
                assert username != "";
                Log.d("ans", username);

                Bundle bundle = new Bundle();
                bundle.putString("username", username);

                SearchedPlayerProfileFragment searchedPlayerProfileFragment = new SearchedPlayerProfileFragment();
                searchedPlayerProfileFragment.setArguments(bundle);

                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.activity_main, searchedPlayerProfileFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });


        // sort the adapter automatically when view is created

        return view;
    }

}
