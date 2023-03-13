package com.example.qrhunter;

import android.app.Activity;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.qrhunter.qrProfile.QRProfileActivity;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;


public class QRProfileTest {
    private Solo solo;


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
     * Check whether wallet connect to QR profile when click on QR code item
     */
    @Test
    public void checkQRProfileSwitch (){
        solo.assertCurrentActivity("Wrong Main Activity", MainActivity.class);
        solo.clickInList(1);
        solo.assertCurrentActivity("Wrong Profile Activity", QRProfileActivity.class);
    }


    /**
     * Check whether chosen QR code shows a correct info
     */
    @Test
    public void checkQRProfileInfo (){
        solo.assertCurrentActivity("Wrong Main Activity", MainActivity.class);
        solo.clickOnText("Fiery Massive Extremely Expensive GigaMulti-Dimensional Poly-Dodecahedron");
        solo.assertCurrentActivity("Wrong Profile Activity", QRProfileActivity.class);
        solo.waitForText("Fiery Massive Extremely Expensive GigaMulti-Dimensional Poly-Dodecahedron", 1, 2000);
        solo.waitForText("Date:2023-03-10 18:20", 1, 2000);
        solo.waitForText("Owner:Roy",1,2000);
        solo.waitForText("Score:36",1,2000);
    }

    /**
     * Check whether comment added successfully.
     */
    @Test
    public void checkCommentAddition(){
        solo.assertCurrentActivity("Wrong Main Activity", MainActivity.class);
        solo.clickOnText("Fiery Massive Extremely Expensive GigaMulti-Dimensional Poly-Dodecahedron");
        solo.assertCurrentActivity("Wrong Profile Activity", QRProfileActivity.class);
        solo.clickOnView(solo.getView(R.id.add_comment_button));
        solo.enterText((EditText) solo.getView(R.id.user_comment_context), "This is text comment");
        solo.clickOnButton("Add");
        solo.waitForText("This is text comment!", 1, 2000);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String time = simpleDateFormat.format(new Date()).toString();
        solo.waitForText(time,1,2000);
    }
}