package com.example.qrhunter.fragments;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.qrhunter.MainActivity;
import com.example.qrhunter.QRCode;
import com.example.qrhunter.R;
import com.example.qrhunter.searchPlayer.QRCodeAdapter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class QrCodeMapProfileFragment extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference collectionReference;


    public QrCodeMapProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map_qr_profile, container, false);

        Bundle bundle = getArguments();
        assert bundle != null;



        return view;

    }
}
