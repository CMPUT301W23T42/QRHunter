package com.example.qrhunter;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robotium.solo.Solo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Map;

public class ProfileTest {
    private Solo solo;
    private final String id = Settings.Secure.ANDROID_ID;

    FirebaseFirestore db;
    CollectionReference collection;

    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class, true, true);

    /**
     * Runs before all tests.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());

        db = FirebaseFirestore.getInstance();
        collection = db.collection("Users");
    }

    /**
     * Gets the Activity
     * @throws Exception
     */
    @Test
    public void start() throws Exception {
        Activity activity = rule.getActivity();
    }

    /**
     * Tests if profile tab opens profile fragment
     */
    @Test
    public void checkProfileSwitch() {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        solo.clickOnText("Profile");

        View frag = solo.getView(R.id.Profile_Fragment);

        assertTrue("Wrong fragment", frag.getVisibility() == View.VISIBLE);
    }

    /**
     * Tests if profile view data matches that of database
     */
    @Test
    public void checkProfileInfo() {
        solo.assertCurrentActivity("Wrong activity", MainActivity.class);

        solo.clickOnText("Profile");

        View frag = solo.getView(R.id.Profile_Fragment);
        assertTrue("Wrong fragment", frag.getVisibility() == View.VISIBLE);

        TextView viewUserName = (TextView) frag.findViewById(R.id.username_text);
        TextView viewName = (TextView) frag.findViewById(R.id.name_text);
        TextView viewEmail = (TextView) frag.findViewById(R.id.email_text);
        TextView viewPhone = (TextView) frag.findViewById(R.id.phone_text);

        DocumentReference docSnap = collection.document(id);
        docSnap.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot docSnap = task.getResult();
                    if (docSnap.exists() && docSnap != null) {
                        Map<String, Object> data = docSnap.getData();
                        String dbUserName = data.get("UserName").toString();
                        String dbName = data.get("Name").toString();
                        String dbEmail = data.get("Email").toString();
                        String dbPhone = data.get("Phone").toString();

                        assertTrue("User name displayed is incorrect", dbUserName == viewUserName.getText());
                        assertTrue("Name displayed is incorrect", dbName == viewName.getText());
                        assertTrue("Email displayed is incorrect", dbEmail == viewEmail.getText());
                        assertTrue("Phone displayed is incorrect", dbPhone == viewPhone.getText());
                    } else {
                        assertTrue("Document empty", false);
                    }
                } else {
                    assertTrue("Task failed", false);
                }
            }
        });
    }
}
