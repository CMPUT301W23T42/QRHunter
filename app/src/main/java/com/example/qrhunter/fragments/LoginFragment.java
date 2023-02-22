package com.example.qrhunter.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.qrhunter.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class LoginFragment extends Fragment {
    private final String TAG = "Sample";
    private EditText usernameEdit;
    private EditText nameEdit;
    private EditText emailEdit;
    private EditText phoneEdit;
    private Button signUpButton;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        usernameEdit = view.findViewById(R.id.user_name_edit);
        nameEdit = view.findViewById(R.id.name_edit);
        emailEdit = view.findViewById(R.id.email_edit);
        phoneEdit = view.findViewById(R.id.phone_edit);
        Button signUpButton = view.findViewById(R.id.sign_up_button);

        TabLayout tabLayout = getActivity().findViewById(R.id.tab_layout);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = db.collection("Users");

        signUpButton.setOnClickListener(v -> {
            final String ID = Settings.Secure.ANDROID_ID;

            final String userName = usernameEdit.getText().toString();
            final String name = nameEdit.getText().toString();
            final String email = emailEdit.getText().toString();
            final String phone = phoneEdit.getText().toString();

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
                            ViewPager2 viewPager = getActivity().
                                    findViewById(R.id.view_pager);
                            viewPager.setCurrentItem(0);
                            tabLayout.setVisibility(View.VISIBLE);
                        })
                        .addOnFailureListener(e -> Log.d(TAG, "Data could not be added!" + e));
            }
        });

        tabLayout.setVisibility(View.GONE);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        TabLayout tabLayout = getActivity().findViewById(R.id.tab_layout);
        tabLayout.setVisibility(View.VISIBLE);
    }
}