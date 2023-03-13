package com.example.qrhunter;

import static android.content.ContentValues.TAG;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.qrhunter.qrProfile.QRProfileActivity;
import com.example.qrhunter.searchPlayer.SearchAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.robotium.solo.Solo;

import junit.framework.TestCase;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeaderboardTest {
    private Solo solo;
    private final String id = Settings.Secure.ANDROID_ID;

    FirebaseFirestore db;
    CollectionReference collectionReference;
    int lengthOfListView;

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
        collectionReference = db.collection("Users");

        Map<String, Object> QRInfo = new HashMap<>();
        QRInfo.put("Name", "test 1");
        QRInfo.put("Email", "test@email.com");
        QRInfo.put("Phone", "1234567");
        QRInfo.put("UserName", "TestUsername1");
        QRInfo.put("score", 10);
        collectionReference
                .document("test doc")
                .set(QRInfo);

        Map<String, Object> QRInfo1 = new HashMap<>();
        QRInfo.put("Name", "test 2");
        QRInfo.put("Email", "test2@email.com");
        QRInfo.put("Phone", "123456789");
        QRInfo.put("UserName", "TestUsername2");
        QRInfo.put("score", 20);
        collectionReference
                .document("test doc2")
                .set(QRInfo1);

        Map<String, Object> QRInfo2 = new HashMap<>();
        QRInfo.put("Name", "test 3");
        QRInfo.put("Email", "test3@email.com");
        QRInfo.put("Phone", "1234567910");
        QRInfo.put("UserName", "TestUsername3");
        QRInfo.put("score", 0);
        collectionReference
                .document("test doc3")
                .set(QRInfo2);
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

        solo.clickOnText("Leaderboard");
        View frag = solo.getView(R.id.fragment_leaderboard);

        assertTrue("Wrong fragment", frag.getVisibility() == View.VISIBLE);
    }

    /**
     * Check if listview is being displayed with items from database
     */
    @Test
    public void checkListViewItems(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        TestCase.assertTrue(solo.waitForText("test 1", 1, 20000));
    }

    /**
     * Checks if Leaderbaord list is sorted based on score
     */
    @Test
    public void checkAscendingSort() {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        collectionReference.count().get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Count fetched successfully
                    AggregateQuerySnapshot snapshot = task.getResult();
                    lengthOfListView = Integer.parseInt(String.valueOf(snapshot.getCount()));
                    solo.clickInList(lengthOfListView);
                } else {
                    Log.d(TAG, "Count failed: ", task.getException());
                }
            }
        });

        assertTrue(solo.waitForText("test 3"));
    }

    /**
     * Checks if clicking on listView goes to a new activity of the profile of the listView item.
     */
    @Test
    public void checkClickListViewItem() {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        View frag = solo.getView(R.id.fragment_leaderboard);
        EditText editText = frag.findViewById(R.id.search_profile_edit_text);
        editText.setText("test 1");
        assertTrue(solo.waitForText("test 1", 1, 10000));
        solo.clickInList(0);
        solo.waitForText("test 1", 1, 10000);
    }

    /**
     * Closes the activity after each test
     * Deletes the test QRCodes that were added.
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception{
        db.collection("Users").document("test doc")
                .delete();
        db.collection("Users").document("test doc1")
                .delete();
        db.collection("Users").document("test doc2")
                .delete();
        solo.finishOpenedActivities();
    }

}
