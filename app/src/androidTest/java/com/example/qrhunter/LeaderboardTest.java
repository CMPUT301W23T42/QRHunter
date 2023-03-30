package com.example.qrhunter;

import static android.content.ContentValues.TAG;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.qrhunter.qrProfile.QRProfileActivity;
import com.example.qrhunter.searchPlayer.SearchAdapter;
import com.example.qrhunter.searchPlayer.UserListItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
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
        LayoutInflater inflater = rule.getActivity().getLayoutInflater();
        View fragmentLeaderboard = inflater.inflate(R.layout.fragment_leaderboard, null);

        db = FirebaseFirestore.getInstance();
        collectionReference = db.collection("Users");

        Map<String, Object> ProfileInfo1 = new HashMap<>();
        ProfileInfo1.put("Name", "test 1");
        ProfileInfo1.put("Email", "test@email.com");
        ProfileInfo1.put("Phone", "1234567");
        ProfileInfo1.put("UserName", "TestUsername1");
        ProfileInfo1.put("score", 10);
        collectionReference
                .document("test doc")
                .set(ProfileInfo1);

        Map<String, Object> ProfileInfo2 = new HashMap<>();
        ProfileInfo2.put("Name", "test 2");
        ProfileInfo2.put("Email", "test2@email.com");
        ProfileInfo2.put("Phone", "123456789");
        ProfileInfo2.put("UserName", "TestUsername2");
        ProfileInfo2.put("score", 20);
        collectionReference
                .document("test doc2")
                .set(ProfileInfo2);

        Map<String, Object> ProfileInfo3 = new HashMap<>();
        ProfileInfo3.put("Name", "test 3");
        ProfileInfo3.put("Email", "test3@email.com");
        ProfileInfo3.put("Phone", "1234567910");
        ProfileInfo3.put("UserName", "TestUsername3");
        ProfileInfo3.put("score", 0);
        collectionReference
                .document("test doc3")
                .set(ProfileInfo3);
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
     * Tests if search tab opens search fragment
     */
    @Test
    public void checkProfileSwitch() {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        TabLayout tabs = (TabLayout) solo.getView(R.id.tab_layout);
        solo.clickOnView(tabs.getTabAt(2).view);

        View frag = solo.getView(R.id.fragment_leaderboard);

        assertTrue("Wrong fragment", frag.getVisibility() == View.VISIBLE);
    }

    /**
     * Check if listview is being displayed with items from database
     */
    @Test
    public void checkListViewItems(){
        TabLayout tabs = (TabLayout) solo.getView(R.id.tab_layout);
        solo.clickOnView(tabs.getTabAt(2).view);

        View frag = solo.getView(R.id.fragment_leaderboard);

        assertTrue("Wrong fragment", frag.getVisibility() == View.VISIBLE);

        TestCase.assertTrue(solo.waitForText("TestUsername1", 1, 20000));
    }

    /**
     * Checks if Leaderboard list is sorted based on score
     */
    @Test
    public void checkAscendingSort() {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        TabLayout tabs = (TabLayout) solo.getView(R.id.tab_layout);
        solo.clickOnView(tabs.getTabAt(2).view);

        View frag = solo.getView(R.id.fragment_leaderboard);

        assertTrue("Wrong fragment", frag.getVisibility() == View.VISIBLE);

        collectionReference.count().get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Count fetched successfully
                    AggregateQuerySnapshot snapshot = task.getResult();
                    lengthOfListView = Integer.parseInt(String.valueOf(snapshot.getCount()));
                    System.out.println(lengthOfListView);
                    ListView list = (ListView)solo.getView(R.id.player_list_list_view);
                    UserListItem item = (UserListItem) list.getAdapter().getItem(lengthOfListView-1);
                    String lastUser = item.getUsername();
                    System.out.println(lastUser);
                    assertTrue("Incorrect order", lastUser.equals("TestUsername3"));
                } else {
                    Log.d(TAG, "Count failed: ", task.getException());
                }
            }
        });

        assertTrue(solo.waitForText("TestUsername3"));
    }

    /**
     * Checks if clicking on listView goes to a new activity of the profile of the listView item.
     */
    @Test
    public void checkClickListViewItem() {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        TabLayout tabs = (TabLayout) solo.getView(R.id.tab_layout);
        solo.clickOnView(tabs.getTabAt(2).view);

        View frag = solo.getView(R.id.fragment_leaderboard);

        assertTrue("Wrong fragment", frag.getVisibility() == View.VISIBLE);

        EditText editText = frag.findViewById(R.id.search_profile_edit_text);
        solo.typeText(editText, "TestUsername1");
        solo.waitForText("TestUsername1", 2, 10000);
        solo.clickInList(0);
        assertTrue(solo.waitForText("TestUsername1", 1, 10000));
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
