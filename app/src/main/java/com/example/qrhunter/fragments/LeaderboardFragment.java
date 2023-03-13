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

/** Class for the fragment that shows the Leaderboard and Search Player functionality **/
public class LeaderboardFragment extends Fragment {

    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    final CollectionReference collectionReference = db.collection("Users");
    ListView playerListView;
    ArrayList<UserListItem> usernames;
    EditText searchEditText;

    Button sortButton;
    SearchAdapter usernamesArrayAdapter;

    public LeaderboardFragment() {
        // Required empty public constructor
    }

    /**
     * Called to create the view hierarchy associated with the fragment. This method is responsible for
     * inflating the fragment's layout and returning the root View of the inflated layout. If the fragment
     * does not have a UI or does not need to display a view, you can return null from this method.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          The parent view that the fragment's UI should be attached to. This value may be null
     *                           if the fragment is not being attached to a parent view.
     * @param savedInstanceState A Bundle containing any saved state information for the fragment. This value may be null
     *                           if the fragment is being instantiated for the first time.
     * @return The View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);

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
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String username = document.getString("UserName");
                        Long scoreLong = document.getLong("score");
                        int score = (scoreLong != null) ? scoreLong.intValue() : 0; // Set score to 0 if the value is null
                        usernames.add(new UserListItem(username, score));

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
