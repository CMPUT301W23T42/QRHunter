package com.example.qrhunter;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class ScannerTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class, true, true);
    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());
    }
    @Test
    public void checkButton (){
        solo.assertCurrentActivity("Wrong Main Activity", MainActivity.class);
        solo.clickOnView(solo.getView(R.id.wallet_button_scan));
        solo.assertCurrentActivity("Wrong Profile Activity", MainActivity.class);
    }
}
