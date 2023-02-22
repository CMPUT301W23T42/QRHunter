package com.example.qrhunter.fragments;

import android.media.Image;
import android.os.Bundle;

import com.example.qrhunter.UserInfo;
import com.example.qrhunter.UserProfile;
import com.example.qrhunter.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.auth.User;

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

public class ProfileFragment extends Fragment implements UserInfo {
    private final String TAG = "Profile Fragment";
    private String ID;
    private UserProfile profile;

    private View view = null;
    private TextView userNameText;
    private TextView fullNameText;
    private TextView emailText;
    private TextView phoneText;
    private ViewPager2 viewPager;


    public ProfileFragment(UserProfile profile) {
        this.profile = profile;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TabLayout tabLayout = getActivity().findViewById(R.id.tab_layout);
        tabLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        userNameText = view.findViewById(R.id.username_text);
        fullNameText = view.findViewById(R.id.name_text);
        emailText = view.findViewById(R.id.email_text);
        phoneText = view.findViewById(R.id.phone_text);
        ImageButton editButton = view.findViewById(R.id.edit_button);

        onChange();

        editButton.setOnClickListener(v -> {
            viewPager = getActivity().findViewById(R.id.view_pager);
            viewPager.setCurrentItem(4);
        });

        return view;
    }

    @Override
    public void setProfile(UserProfile profile) {
        Log.i(TAG, "Running setProfile");
        this.profile = profile;
        onChange();
    }

    @Override
    public void onChange() {
        Log.i(TAG, "Running onChange");
        if (view != null) {
            userNameText.setText(profile.getUserName());
            fullNameText.setText(profile.getFullName());
            emailText.setText(profile.getEmail());
            phoneText.setText(profile.getPhone());
            Log.i(TAG, "onChange complete");
        }
    }
}