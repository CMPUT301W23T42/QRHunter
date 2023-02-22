package com.example.qrhunter.fragments;

import android.os.Bundle;

import com.example.qrhunter.UserProfile;
import com.example.qrhunter.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Map;

public class ProfileFragment extends Fragment {

    private String ID;
    private UserProfile userProfile;
    final String TAG = "Sample";
    private FirebaseFirestore db;
    DocumentReference docRef;

    public ProfileFragment(String ID) {
        this.ID = ID;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TabLayout tabLayout = getActivity().findViewById(R.id.tab_layout);
        tabLayout.setVisibility(View.VISIBLE);

        db = FirebaseFirestore.getInstance();
        docRef = db.collection("Users").document(ID);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        TextView userNameText = view.findViewById(R.id.username_text);
        TextView fullNameText = view.findViewById(R.id.name_text);
        TextView emailText = view.findViewById(R.id.email_text);
        TextView phoneText = view.findViewById(R.id.phone_text);
        ImageButton editButton = view.findViewById(R.id.edit_button);

        userProfile = new UserProfile(new UserProfile.AttributeChangeListener() {
            @Override
            public void onChange() {
                Log.d("OnChange", "Called");
                userNameText.setText(userProfile.getUserName());
                fullNameText.setText(userProfile.getFullName());
                emailText.setText(userProfile.getEmail());
                phoneText.setText(userProfile.getPhone());
            }
        });

        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value,
                                @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(TAG, "Listen Failed", error);
                    throw new RuntimeException(error);
                }

                if (value != null && value.exists()) {
                    userProfile.setUserName(value.get("User Name").toString());
                    Log.d("FB", value.get("User Name").toString());
                    userProfile.setFullName(value.get("Name").toString());
                    userProfile.setEmail(value.get("Email").toString());
                    userProfile.setPhone(value.get("Phone").toString());

                } else {
                    throw new RuntimeException("Invalid Document Snapshot");
                }
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewPager2 viewPager = (ViewPager2) getActivity().findViewById(R.id.view_pager);
                viewPager.setCurrentItem(4);
            }
        });

        return view;


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}