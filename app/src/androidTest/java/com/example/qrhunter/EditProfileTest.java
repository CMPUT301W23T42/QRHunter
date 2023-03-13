package com.example.qrhunter;

import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.app.Fragment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.google.firebase.firestore.CollectionReference;

import com.google.firebase.firestore.FirebaseFirestore;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
/**

 Fragment class for displaying and editing user profile information.
 This fragment allows the user to edit their profile picture, name, email and password.
 */
public class EditProfileTest {
    private Solo solo;

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
     * Tests if profile edit button opens edit profile fragment
     */
    @Test
    public void checkEditSwitch() {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        solo.clickOnText("Profile");
        View profileFrag = solo.getView(R.id.Profile_Fragment);
        assertTrue("Wrong fragment", profileFrag.getVisibility() == View.VISIBLE);

        solo.clickOnView(solo.getView(R.id.edit_button));
        View editFrag = solo.getView(R.id.Profile_Edit_Fragment);
        assertTrue("Wrong fragment", editFrag.getVisibility() == View.VISIBLE);

    }
}
