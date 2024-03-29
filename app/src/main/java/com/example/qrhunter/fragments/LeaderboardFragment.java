package com.example.qrhunter.fragments;

import static android.content.ContentValues.TAG;

import android.content.Context;
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
import android.widget.Toast;

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
    Context mContext;
    Button sortButton;
    SearchAdapter usernamesArrayAdapter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

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

                usernamesArrayAdapter = new SearchAdapter(mContext, usernames);
                usernamesArrayAdapter.sortFilteredScores();
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
             * @param view The veiw of the item clciked on
             * @param i Position of the item clicked
             * @param l ID of the item clicked
             * @return None
             */
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String username = "null";
                try{
                    UserListItem usernameObj = (UserListItem) usernamesArrayAdapter.getFilteredList().get(i);
                    username = usernameObj.getUsername();
                }catch (Exception e){
                    Log.d("exception", String.valueOf(e));
                    Toast.makeText(mContext, "Nothing to select.", Toast.LENGTH_SHORT).show();
                    return ;
                }

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

        sortButton = view.findViewById(R.id.sort_button);
        sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HighScoreQRCodeFragment highScoreQRCodeFragment = new HighScoreQRCodeFragment();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.activity_main, highScoreQRCodeFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });


        return view;
    }

}
