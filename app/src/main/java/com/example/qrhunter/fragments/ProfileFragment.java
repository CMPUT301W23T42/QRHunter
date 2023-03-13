package com.example.qrhunter.fragments;

import android.os.Bundle;

import com.example.qrhunter.UserInfo;
import com.example.qrhunter.UserProfile;
import com.example.qrhunter.R;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Fragment responsible for display profile to users
 */
public class ProfileFragment extends Fragment implements UserInfo {
    private onEditProfileListener listener;
    private final String TAG = "Profile Fragment";
    private UserProfile profile;

    private View view = null;
    private TextView userNameText;
    private TextView fullNameText;
    private TextView emailText;
    private TextView phoneText;

    /**
     * Profile fragment initializer
     * @param profile UserProfile object representing a user profile
     */
    public ProfileFragment(UserProfile profile) {
        this.profile = profile;
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        userNameText = view.findViewById(R.id.username_text);
        fullNameText = view.findViewById(R.id.name_text);
        emailText = view.findViewById(R.id.email_text);
        phoneText = view.findViewById(R.id.phone_text);
        ImageButton editButton = view.findViewById(R.id.edit_button);

        onChange();

        editButton.setOnClickListener(v -> listener.onEditProfile());

        return view;
    }

    /**
     * Set the profile attribute of object
     * @param   profile UserProfile object representing the user profile
     */
    @Override
    public void setProfile(UserProfile profile) {
        Log.d(TAG, "Running setProfile");
        this.profile = profile;
        onChange();
    }

    /**
     * Updates view elements on any changes to object
     */
    @Override
    public void onChange() {
        Log.d(TAG, "Running onChange");
        if (view != null) {
            userNameText.setText(profile.getUserName());
            fullNameText.setText(profile.getFullName());
            emailText.setText(profile.getEmail());
            phoneText.setText(profile.getPhone());
            Log.d(TAG, "onChange complete");
        }
    }

    /**
     * Sets listener attribute of profile fragment object
     * @param   listener    represents an onEditProfileListener
     */
    public void setOnEditProfileListener(onEditProfileListener listener) {
        this.listener = listener;
    }

    public interface onEditProfileListener {
        void onEditProfile();
    }
}