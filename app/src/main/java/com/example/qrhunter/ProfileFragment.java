package com.example.qrhunter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    public ProfileFragment() {

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_profile, container, false);;

        EditText editUserName = view.findViewById(R.id.user_name_edit);
        EditText editFullName = view.findViewById(R.id.full_name_edit);
        EditText editEmail = view.findViewById(R.id.email_edit);
        EditText editPhone = view.findViewById(R.id.phone_edit);

        Button saveProfileEdit = view.findViewById(R.id.save_button);
        Button cancelProfileEdit = view.findViewById(R.id.cancel_button);

        saveProfileEdit.setOnClickListener(v -> {
            String userName = editUserName.getText().toString();
            String fullName = editFullName.getText().toString();
            String email = editEmail.getText().toString();
            String phone = editPhone.getText().toString();
        });

        cancelProfileEdit.setOnClickListener(v -> {

        });
        return view;
    }
}

