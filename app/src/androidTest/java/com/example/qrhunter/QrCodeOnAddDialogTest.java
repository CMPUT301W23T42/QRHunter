package com.example.qrhunter;

import android.app.AlertDialog;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.qrhunter.databinding.AddQrDialogBinding;
import com.example.qrhunter.fragments.ScannerFragment;
import com.example.qrhunter.fragments.WalletFragment;
import com.google.android.gms.common.api.internal.LifecycleFragment;
import com.google.zxing.client.android.Intents;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class QrCodeOnAddDialogTest extends Fragment {
    private Solo solo;
    FragmentManager fm;
    ScannerFragment scannerFragment;
    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class, true, true);

    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());
        fm = rule.getActivity().getSupportFragmentManager();
    }

    @Test
    public void QRDialogTest() {
        String hash = "f23ef90d3b61ebadadb75a803a8430e2ac1a383323faf19b86ecc6f375325c8a";
        QrCodeOnAddDialog dialogQR = new QrCodeOnAddDialog(hash, rule.getActivity());
        dialogQR.show(fm, "Test");
        solo.sleep(100);
        solo.clickOnView(solo.getView(R.id.qr_add_location_button));
        solo.waitForText("Continue",1,100);
        solo.waitForText(" has been added to your inventory!", 1, 100);
        solo.waitForText("Score",1,100);
        solo.clickOnText("Continue");
    }

}
