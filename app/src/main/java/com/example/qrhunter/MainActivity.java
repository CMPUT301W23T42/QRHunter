package com.example.qrhunter;

import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.qrhunter.fragments.LoginFragment;
import com.example.qrhunter.fragments.ProfileFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;

import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "Main";
    private UserProfile profile;
    private ViewPager2 viewPager;
    private TabManager tabManager;

    private FirebaseFirestore db;
    DocumentReference docRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        profile = new UserProfile();
        final String ID = Settings.Secure.ANDROID_ID;

        db = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = db.collection("Users");

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        viewPager.setCurrentItem(5);
        tabManager = new TabManager(this);
        viewPager.setAdapter(tabManager);

        docRef = collectionReference.document(ID);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    Log.i(TAG, "Getting firebase profile");
                    Map<String, Object> data = document.getData();

                    profile.setUserName(data.get("UserName").toString());
                    profile.setFullName(data.get("Name").toString());
                    profile.setEmail(data.get("Email").toString());
                    profile.setPhone(data.get("Phone").toString());

                    tabManager.setProfile(profile);

                    viewPager.setCurrentItem(0);
                } else {
                    Log.i(TAG, "Failed");
                    viewPager.setCurrentItem(5);
                }
            }
        });
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

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                Log.d("TabManager", "Tab Position " + position);
                viewPager.setCurrentItem(position);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }
            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });

    }

}