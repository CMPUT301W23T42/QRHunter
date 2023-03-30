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

import com.example.qrhunter.R;
import com.example.qrhunter.UserInfo;
import com.example.qrhunter.UserProfile;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

/**
 * Fragment responsible for displaying the edit profile view and collecting input
 */
public class EditProfileFragment extends Fragment implements UserInfo {
    private onCompleteListener listener;
    private UserProfile profile;
    private final String ID = Settings.Secure.ANDROID_ID;
    private final String TAG = "Edit Profile Fragment";
    private View view = null;
    private EditText editUserName;
    private EditText editFullName;
    private EditText editEmail;
    private EditText editPhone;

    /**
     * EditProfileFragment initializer
     * @param profile UserProfile object representing a user profile
     */
    public EditProfileFragment(UserProfile profile) {
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
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                            onChange();
                        })
                        .addOnFailureListener(e -> {
                            Log.d(TAG, "Data could not be added!" + e);
                        });
            }
        });

        cancelProfileEdit.setOnClickListener(v -> listener.onComplete());
        return view;
    }

    /**
     * Set the profile attribute of object
     * @param   profile UserProfile object representing the user profile
     */
    @Override
    public void setProfile(UserProfile profile) {
        this.profile = profile;
        onChange();
    }

    /**
     * Get profile attribute of object
     * @return profile UserProfile object attribute
     */
    public UserProfile getProfile() {
        return profile;
    }

    /**
     * Updates view elements on any changes to object
     */
    @Override
    public void onChange() {
        if (view != null) {
            editUserName.setText(profile.getUserName());
            editFullName.setText(profile.getFullName());
            editEmail.setText(profile.getEmail());
            editPhone.setText(profile.getPhone());
        }
    }

    /**
     * Sets listener attribute of profile fragment object
     * @param   listener    represents an onEditProfileListener
     */
    public void setOnCompleteListener(onCompleteListener listener) {
        this.listener = listener;
    }

    public interface onCompleteListener {
        void onComplete();
    }
}

