package com.example.qrhunter.fragments;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.qrhunter.searchPlayer.SearchAdapter;
import com.example.qrhunter.R;
import com.example.qrhunter.searchPlayer.UserListItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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

/**
 This is the HighScoreQRCodeFragment class which extends Fragment class.
 This class is responsible for displaying the list of users and their QR code high scores
 from the database in the high score fragment.
 */

public class HighScoreQRCodeFragment extends Fragment {

    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    final CollectionReference collectionReference = db.collection("CodeList");
    ListView playerListView;
    Context mContext;
    ArrayList<UserListItem> usernames;
    EditText searchEditText;
    ImageView backButton;
    SearchAdapter usernamesArrayAdapter;
    TextView userHighScoreTextView;
    TextView userNameTextView;
    TextView userRankTextView;

    public HighScoreQRCodeFragment() {
        // Required empty public constructor
    }

    /**
     * Called when the Fragment is attached to its context. Sets the mContext variable to the context passed in.
     *
     * @param context the context that the Fragment is being attached to
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }


    /**
     * Called to create the Fragment's view. Inflates the layout and sets up the ListView and EditText. Retrieves the
     * data from the Firestore database and sets up the adapter to display the data in the ListView.
     *
     * @param inflater           the LayoutInflater object that can be used to inflate any views in the Fragment
     * @param container          if non-null, this is the parent view that the fragment's UI should be attached to. The
     *                           fragment should not add the view itself, but this can be used to generate the
     *                           LayoutParams of the view.
     * @param savedInstanceState if non-null, this fragment is being re-constructed from a previous saved state as
     *                           given here.
     * @return the view for the Fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_high_score, container, false);

        playerListView = view.findViewById(R.id.player_list_list_view);
        searchEditText = view.findViewById(R.id.search_profile_edit_text);
        userHighScoreTextView = view.findViewById(R.id.user_high_score);
        userNameTextView = view.findViewById(R.id.user_name_text);
        userRankTextView = view.findViewById(R.id.user_rank_text);

        usernames = new ArrayList<UserListItem>();

        // Retrieve data from the Firestore database and set up the adapter to display the data in the ListView
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            Map<String, Integer> highScores;

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    highScores = new HashMap<>(); // to store the highest score for each owner
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
                            // Debug log to print out the usernames
                            Log.d("DEBUG", "Added username: " + username);
                        }
                    }

                }

                else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
                SearchAdapter searchAdapter = new SearchAdapter(mContext, usernames);
                usernamesArrayAdapter = searchAdapter;
                usernamesArrayAdapter.sortFilteredScores();

                final String ID = Settings.Secure.getString(getContext().getContentResolver(),
                        Settings.Secure.ANDROID_ID);

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference userRef = db.collection("Users").document(ID);
                userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        // Retrieve the username from the "Users" collection in the database

                        String username = documentSnapshot.getString("UserName");

                        // Retrieve the high score for this user from the hash map

                        int highScore = 0;
                        if (highScores.containsKey(username)) {
                            highScore = highScores.get(username);
                        }

                        // Retrieve the position of username from usernames list
                        int position = 0;
                        for (UserListItem usernameObj: searchAdapter.getFilteredList()){
                            Log.d("username ", usernameObj.getUsername());
                            if (usernameObj.getUsername().equalsIgnoreCase(username)){
                                Log.d("usernames size: ", String.valueOf(searchAdapter.getFilteredList().size()));
                                Log.d("index: ", String.valueOf(searchAdapter.getFilteredList().indexOf(usernameObj)));
                                position = searchAdapter.getFilteredList().indexOf(usernameObj) + 1;
                                Log.d("position: ", String.valueOf(position));
                            }
                        }

                        // Update the text view with the username and high score
                        userHighScoreTextView.setText(String.valueOf(highScore));
                        userRankTextView.setText(String.valueOf(position) + ".");
                        userNameTextView.setText(String.valueOf(username));

                    }
                });
                usernamesArrayAdapter.notifyDataSetChanged();
                playerListView.setAdapter(usernamesArrayAdapter);
            }

        });

        // Add a text change listener to the search EditText to filter the list
        searchEditText.addTextChangedListener(new TextWatcher() {
            /**
             * This method is called to notify the listener that the text is about to be changed.
             *
             * @param s the text before it is changed
             * @param start the position of the first character that will be changed
             * @param count the number of characters that will be changed
             * @param after the number of characters that will replace the changed characters
             */
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            /**
             * This method is called to notify the listener that the text has changed.
             *
             * @param s the new text
             * @param start the position of the first character that was changed
             * @param before the number of characters that were replaced
             * @param count the number of characters that were added
             */
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (usernamesArrayAdapter != null) {
                    usernamesArrayAdapter.getFilter().filter(s);
                    Log.d("Filter", usernamesArrayAdapter.getFilter().toString());
                    usernamesArrayAdapter.notifyDataSetChanged();
                }
            }

            /**
             * This method is called to notify the listener that the text has been changed.
             *
             * @param s the new text
             */
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
             * @param view The view of the item clicked on
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

        backButton = view.findViewById(R.id.searched_player_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        return view;
    }

}
