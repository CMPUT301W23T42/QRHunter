package com.example.qrhunter.fragments;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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

/** Class for fragment that shows the profile of a player when searched for and clicked in the listview of Leaderboard fragment**/
public class SearchedPlayerProfileFragment extends Fragment {

    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    final CollectionReference collectionReference = db.collection("CodeList");
    TextView usernameTextView;
    ArrayList<QRCodeListItem> qrCodes;
    ArrayAdapter qrCodeAdapter;
    ListView qrCodeListView;

    ImageButton backButton;

    public SearchedPlayerProfileFragment() {
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
        View view = inflater.inflate(R.layout.fragment_searched_player_profile, container, false);

        Bundle bundle = getArguments();
        String username = bundle.getString("username");

        usernameTextView = view.findViewById(R.id.username_text);
        qrCodeListView = view.findViewById(R.id.qr_codes_list_view);
        backButton = view.findViewById(R.id.searched_player_back_button);

        usernameTextView.setText(username);

        qrCodes = new ArrayList<QRCodeListItem>();

        /**
         * Called when the query is able to execute, and get data from the database. Has a condition attached, to return only return reults with specific username
         *
         * @param task Has a task object that has all the documents required
         * @return None
         */
        collectionReference.whereEqualTo("owner", username).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });


        return view;

    }
}
