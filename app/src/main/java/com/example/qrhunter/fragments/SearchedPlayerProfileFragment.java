package com.example.qrhunter.fragments;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.qrhunter.MainActivity;
import com.example.qrhunter.QRCode;
import com.example.qrhunter.R;
import com.example.qrhunter.qrProfile.QRProfileActivity;
import com.example.qrhunter.searchPlayer.QRCodeAdapter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/** Class for fragment that shows the profile of a player when searched for and clicked in the listview of Leaderboard fragment**/
public class SearchedPlayerProfileFragment extends Fragment {

    FirebaseFirestore db;
    TextView usernameTextView;
    ListView qrList;
    ArrayAdapter<QRCode> qrAdapter;
    ArrayList<QRCode> qrDataList;

    ImageView backButton;

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
        assert bundle != null;
        String username = bundle.getString("username");

        usernameTextView = view.findViewById(R.id.username_text);
        backButton = view.findViewById(R.id.searched_player_back_button);
        usernameTextView.setText(username);

        qrList = view.findViewById(R.id.qr_codes_list_view);
        qrDataList = new ArrayList<>();
        qrAdapter = new QRCodeAdapter(this.getActivity(), qrDataList);
        qrList.setAdapter(qrAdapter);

        db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("CodeList");

        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            /**
             * This method is called when the data in the specified query snapshot changes. This includes
             * the initial data and any subsequent changes to the documents that match the query criteria.
             * Clears the arraylist of QRCodes and adds again from the document.
             *
             * @param value The query snapshot representing the updated data.
             * @param error The error, if any, that occurred while listening for changes. If the error is null,
             *              no errors occurred during the listening operation.
             */
            @Override

            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                qrDataList.clear();

                for (QueryDocumentSnapshot doc: value) {
                    String ownerName = (String) doc.getData().get("owner");
                    if (ownerName != null) {
                        if ((MainActivity.DEBUG_ROY) ? (ownerName.equals("Roy")) :
                                (ownerName.equals(username))) {
                            Log.d(TAG, "Show list of QR codes");
                            String id = doc.getId();
                            String date = (String) doc.getData().get("date");
                            String hash = (String) doc.getData().get("hash");
                            GeoPoint location = (GeoPoint) doc.getData().get("location");
                            String name = (String) doc.getData().get("name");

                            String owner = (String) doc.getData().get("owner");
                            int score = Integer.parseInt(String.valueOf(doc.getData().get("score")));

                            qrList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    Intent intent = new Intent(getActivity(), QRProfileActivity.class);
                                    intent.putExtra("DOC_ID", doc.getId());
                                    intent.putExtra("OWNER_NAME", doc.getString("owner"));
                                    startActivity(intent);
                                }
                            });


                            qrDataList.add(new QRCode(date, hash, name, location, owner, score, id));
                        }
                    }
                }
                qrAdapter.notifyDataSetChanged();
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
