package com.example.qrhunter;

import androidx.fragment.app.Fragment;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.qrhunter.databinding.AddQrDialogBinding;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class QrCodeOnAddDialogTest extends Fragment {
    private Solo solo;
    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class, true, true);
    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());
    }

    @Test
    public void test() {
        String hash = "f23ef90d3b61ebadadb75a803a8430e2ac1a383323faf19b86ecc6f375325c8a";
        QrCodeOnAddDialog dialogQR = new QrCodeOnAddDialog(hash, rule.getActivity());
        dialogQR.show(getParentFragmentManager(),"Test" );
        solo.waitForText("Continue",1,1);
        solo.clickOnButton("Continue");

    }

}
