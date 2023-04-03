package com.example.qrhunter;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import android.util.Log;
import android.widget.EditText;
import android.widget.ListView;

import com.example.qrhunter.fragments.ProfileFragment;
import com.example.qrhunter.fragments.WalletFragment;
import com.example.qrhunter.qrProfile.QRProfileActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robotium.solo.Solo;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.HashMap;
import java.util.Map;

/**
 * Class for intent testing the WalletFragment.
 */
public class WalletTest {

    private Solo solo;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference collectionReference = db.collection("CodeList");

    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class, true, true);

    /**
     * Runs before all tests and creates solo instance.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());
        // add a few test QR codes to the database
        Map<String, Object> QRInfo = new HashMap<>();
        QRInfo.put("name", "test 1");
        QRInfo.put("date", "test date");
        QRInfo.put("hash", "test hash");
        QRInfo.put("owner", "Roy");
        QRInfo.put("location", null);
        QRInfo.put("score", 2);
        collectionReference
                .document("test doc")
                .set(QRInfo);

        Map<String, Object> QRInfo1 = new HashMap<>();
        QRInfo1.put("name", "Absolutely Solid Ready To Go TurboDraconic Tesseract");
        QRInfo1.put("date", "2023-03-12 11:24");
        QRInfo1.put("hash", "6fb8be06ad18006337bc6dbfd760eec088a69ba9aa021420213cf8bd1166f2c7");
        QRInfo1.put("owner", "Roy");
        QRInfo1.put("location", null);
        QRInfo1.put("score", 1);
        collectionReference
                .document("test doc1")
                .set(QRInfo1);

        Map<String, Object> QRInfo2 = new HashMap<>();
        QRInfo2.put("name", "test 3");
        QRInfo2.put("date", "test date");
        QRInfo2.put("hash", "test hash");
        QRInfo2.put("owner", "Roy");
        QRInfo2.put("location", null);
        QRInfo2.put("score", 1000000);
        collectionReference
                .document("test doc2")
                .set(QRInfo2);
    }

    /**
     * Gets the Activity
     * @throws Exception
     */
    @Test
    public void start() throws Exception{
        Activity activity = rule.getActivity();
    }

    /**
     * Checks if clicking on listView goes to a new activity of the profile of the listView item.
     */
    @Test
    public void checkClick() {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getView(R.id.rb_descending));
        solo.clickInList(2);
        solo.assertCurrentActivity("QRProfileActivity not opened", QRProfileActivity.class);
    }

    /**
     * Checks if clicking on radio button for descending sort works properly.
     */
    @Test
    public void checkDescendingSort() {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getView(R.id.rb_descending));
        solo.clickInList(0);
        assertTrue(solo.waitForText("test 3"));
    }

    /**
     * Closes the activity after each test
     * Deletes the test QRCodes that were added.
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception{
        db.collection("CodeList").document("test doc")
                .delete();
        db.collection("CodeList").document("test doc1")
                .delete();
        db.collection("CodeList").document("test doc2")
                .delete();
        solo.finishOpenedActivities();
    }

}
