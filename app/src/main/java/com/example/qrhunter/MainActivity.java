package com.example.qrhunter;

import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResultCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.example.qrhunter.fragments.LoginFragment;
import com.example.qrhunter.fragments.ProfileFragment;
import com.example.qrhunter.generators.QrCodeNameGenerator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.common.hash.Hashing;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
This is the main activity of the app. It is responsible for the tab layout and the fragments.
*/
public class MainActivity extends AppCompatActivity { 
    private final String TAG = "Main";
    private UserProfile profile;
    private TabManager tabManager;
    private TabLayout tabLayout;
    // Firebase
    private FirebaseFirestore db;
    private Boolean transactionSafe = true;
    DocumentReference docRef;

    /**
    This method retrieves the user profile data from the database
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final String ID = Settings.Secure.ANDROID_ID;

        db = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = db.collection("Users");
        docRef = collectionReference.document(ID);

        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setVisibility(View.GONE);
        tabManager = new TabManager(this);
        tabLayout.selectTab(tabLayout.getTabAt(0));

        tabLayout.getTabAt(0).setIcon(R.drawable.list_icon);
        tabLayout.getTabAt(1).setIcon(R.drawable.map_icon);
        tabLayout.getTabAt(2).setIcon(R.drawable.search_icon);
        tabLayout.getTabAt(3).setIcon(R.drawable.profile_icon);

        getProfile(docRef);
        /**  
        This is a listener that will update the profile data if it changes.
        */
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            /**
             Sets up a listener for tab selection events and switches the fragment according to the selected tab.
            @param tabLayout the TabLayout object that the listener will be attached to.
            */
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (!isFinishing()) {
                    int position = tab.getPosition();
                    tabManager.switchFragment(position);
                    Log.d(TAG, "Tab Selected: " + position);
                }
            }

            /**
            An empty method that is called when a tab is unselected.
            @param tab the TabLayout.Tab object that was unselected.
            */
            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            /**
            An empty method that is called when a tab is reselected.
            @param tab the TabLayout.Tab object that was reselected.
            */
            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
    }
    /**
     * This method retrieves the user profile data from Firebase.
     * @param docRef The document reference for the user's data in Firebase Firestore.
     */
    private void getProfile(DocumentReference docRef) {
        profile = new UserProfile();

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    Log.d(TAG, "Getting firebase profile");
                    Map<String, Object> data = document.getData();

                    profile.setUserName(data.get("UserName").toString());
                    profile.setFullName(data.get("Name").toString());
                    profile.setEmail(data.get("Email").toString());
                    profile.setPhone(data.get("Phone").toString());

                    tabManager.setProfile(profile);
                    Log.d(TAG, "result: " + data);

                    tabManager.switchFragment(0);
                    tabLayout.setVisibility(View.VISIBLE);
                } else {
                    Log.d(TAG, "Failed");
                    tabManager.switchFragment(5);
                }
            }
        });
        /**  
        This is a listener that will update the profile data if it changes.
        */
        docRef.addSnapshotListener(MetadataChanges.INCLUDE, (value, error) -> {
            Log.d(TAG, "Snapshot Listener running");
            if (error != null) {
                Log.w(TAG, "Listen Failed", error);
                throw new RuntimeException(error);
            }

            String source = value != null && value.getMetadata().hasPendingWrites()
                    ? "Local" : "Server";

            if (value != null && value.exists()) {
                Log.d(TAG, "Firebase return: " + value.getData());
                Map<String, Object> data = value.getData();
                profile.setUserName(data.get("UserName").toString());
                profile.setFullName(data.get("Name").toString());
                profile.setEmail(data.get("Email").toString());
                profile.setPhone(data.get("Phone").toString());

                tabManager.setProfile(profile);

            }
        });
    }
}