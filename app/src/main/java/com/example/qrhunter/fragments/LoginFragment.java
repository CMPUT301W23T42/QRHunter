package com.example.qrhunter.fragments;

import android.app.AlertDialog;
import android.os.Bundle;

import android.content.Context;
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

import com.example.qrhunter.MainActivity;
import com.example.qrhunter.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Object responsible for prompting user for login info
 */
public class LoginFragment extends Fragment {
    private final String TAG = "Sample";
    private EditText usernameEdit;
    private EditText nameEdit;
    private EditText emailEdit;
    private EditText phoneEdit;
    private Button signUpButton;

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

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        usernameEdit = view.findViewById(R.id.user_name_edit);
        nameEdit = view.findViewById(R.id.name_edit);
        emailEdit = view.findViewById(R.id.email_edit);
        phoneEdit = view.findViewById(R.id.phone_edit);
        Button signUpButton = view.findViewById(R.id.sign_up_button);

        TabLayout tabLayout = getActivity().findViewById(R.id.tab_layout);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = db.collection("Users");
        Map<String, Object> users = new HashMap<>();

        Task<DocumentSnapshot> usersTask = collectionReference.
                document("Usernames").get();

        usersTask.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    users.putAll(doc.getData());
                }
            }
        });

        signUpButton.setOnClickListener(v -> {
            final String ID = Settings.Secure.getString(getContext().getContentResolver(),
                    Settings.Secure.ANDROID_ID);

            final String userName = usernameEdit.getText().toString();
            final String name = nameEdit.getText().toString();
            final String email = emailEdit.getText().toString();
            final String phone = phoneEdit.getText().toString();

            HashMap<String, String> data = new HashMap<>();
            if (users.containsKey(userName)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Username is already in use, ")
                        .setTitle("The username is taken. Try another.");
                builder.create().show();

            } else {
                if (!userName.isEmpty() && !name.isEmpty()
                        && !email.isEmpty() && phone.length() > 9) {
                    data.put("UserName", userName);
                    data.put("Name", name);
                    data.put("Email", email);
                    data.put("Phone", phone);
                    users.put(userName, "");

                    collectionReference
                            .document("Usernames")
                            .update(users)
                            .addOnSuccessListener(unused -> {
                                Log.d(TAG, "Users has been updated");
                            });

                    collectionReference
                            .document(ID)
                            .set(data)
                            .addOnSuccessListener(unused -> {
                                Log.d(TAG, "Data has been added successfully!");
                                //ViewPager2 viewPager = getActivity().
                                //findViewById(R.id.view_pager);
                                //viewPager.setCurrentItem(0);
                                tabLayout.setVisibility(View.VISIBLE);

                                ((MainActivity)getActivity()).signedUp();
                            })
                            .addOnFailureListener(e -> Log.d(TAG, "Data could not be added!" + e));
                }
            }
        });

        tabLayout.setVisibility(View.GONE);

        return view;
    }

    /**
     * actions to run on removal of view
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        TabLayout tabLayout = getActivity().findViewById(R.id.tab_layout);
        tabLayout.setVisibility(View.VISIBLE);
    }
}