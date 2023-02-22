package com.example.qrhunter.fragments;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.qrhunter.R;
import com.example.qrhunter.UserInfo;
import com.example.qrhunter.UserProfile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class EditProfileFragment extends Fragment implements UserInfo {
    private UserProfile profile;
    private final String ID = Settings.Secure.ANDROID_ID;
    private final String TAG = "Edit Profile Fragment";
    private View view = null;
    private EditText editUserName;
    private EditText editFullName;
    private EditText editEmail;
    private EditText editPhone;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    public EditProfileFragment(UserProfile profile) {
        this.profile = profile;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewPager = getActivity().findViewById(R.id.view_pager);
        tabLayout = getActivity().findViewById(R.id.tab_layout);
        view = inflater.inflate(R.layout.fragment_profile_edit, container, false);

        editUserName = view.findViewById(R.id.user_name_edit);
        editFullName = view.findViewById(R.id.full_name_edit);
        editEmail = view.findViewById(R.id.email_edit);
        editPhone = view.findViewById(R.id.phone_edit);

        onChange();

        Button saveProfileEdit = view.findViewById(R.id.save_button);
        Button cancelProfileEdit = view.findViewById(R.id.cancel_button);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = db.collection("Users");

        saveProfileEdit.setOnClickListener(v -> {
            String userName = editUserName.getText().toString();
            String name = editFullName.getText().toString();
            String email = editEmail.getText().toString();
            String phone = editPhone.getText().toString();

            HashMap<String, String> data = new HashMap<>();

            if (!userName.isEmpty() && !name.isEmpty()
            && !email.isEmpty() && phone.length()>9) {
                data.put("UserName", userName);
                data.put("Name", name);
                data.put("Email", email);
                data.put("Phone", phone);

                collectionReference
                        .document(ID)
                        .set(data)
                        .addOnSuccessListener(unused -> {
                            Log.d(TAG, "Data has been added successfully!");

                            tabLayout.setVisibility(View.VISIBLE);
                            viewPager.setCurrentItem(3);
                        })
                        .addOnFailureListener(e -> {
                            Log.d(TAG, "Data could not be added!" + e);
                            tabLayout.setVisibility(View.VISIBLE);
                        });
            }
        });

        cancelProfileEdit.setOnClickListener(v -> viewPager.setCurrentItem(3));
        return view;
    }

    @Override
    public void setProfile(UserProfile profile) {
        this.profile = profile;
        onChange();
    }

    @Override
    public void onChange() {
        if (view != null) {
            editUserName.setText(profile.getUserName());
            editFullName.setText(profile.getFullName());
            editEmail.setText(profile.getEmail());
            editPhone.setText(profile.getPhone());
        }
    }
}

