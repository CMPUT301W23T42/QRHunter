package com.example.qrhunter;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.qrhunter.fragments.EditProfileFragment;
import com.example.qrhunter.fragments.LoginFragment;
import com.example.qrhunter.fragments.ProfileFragment;
import com.example.qrhunter.fragments.ScannerFragment;
import com.example.qrhunter.fragments.WalletFragment;
import com.example.qrhunter.fragments.LeaderboardFragment;

/**
 * Class responsible for handling tab switching and information passing between tabs
 */
public class TabManager implements UserInfo {
    private final String TAG = "Tab Manager";
    private UserProfile profile = null;

    private FragmentManager fragmentManager;
    private Fragment currentFragment;
    private FragmentActivity activity;

    /**
     * TabManager initializer
     * @param fragmentActivity  object representing main activity
     */
    public TabManager(@NonNull FragmentActivity fragmentActivity) {
        activity = fragmentActivity;
        fragmentManager = fragmentActivity.getSupportFragmentManager();
    }

    /**
     * Utilizes fragment transaction to replace fragment on display
     * @param   fragment    fragment object to place on display
     */
    public void replaceFragment(Fragment fragment, String transactionTAG) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment, transactionTAG);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        transaction.commitAllowingStateLoss();
    }

    /**
     * Switches main fragment display based on parameter
     * @param   position    integer representation of fragment to display
     */
    public void switchFragment(int position) {
        Log.d(TAG, "Running createFragment, " + position);
        String transactionTAG;
        switch (position) {
            case 0:
                transactionTAG = "Wallet Fragment";
                currentFragment = new WalletFragment();
                break;
            case 2:
                LeaderboardFragment leaderboardFragment = new LeaderboardFragment();

                transactionTAG = "Search Player Fragment";
                currentFragment = leaderboardFragment;
                break;
            case 3:
                ProfileFragment profileFragment = new ProfileFragment(profile);
                profileFragment.setOnEditProfileListener(new ProfileFragment.onEditProfileListener() {
                    @Override
                    public void onEditProfile() {
                        switchFragment(6);
                    }
                });
                transactionTAG = "Profile Fragment";
                currentFragment = profileFragment;
                break;

            case 6:
                EditProfileFragment editProfileFragment = new EditProfileFragment(profile);
                editProfileFragment.setOnCompleteListener(new EditProfileFragment.onCompleteListener() {
                    @Override
                    public void onComplete() {
                        switchFragment(4);
                    }
                });
                transactionTAG = "Edit Profile Fragment";
                currentFragment = editProfileFragment;
                break;

            case 7:
                transactionTAG = "Login Fragment";
                currentFragment = new LoginFragment();
                break;

            default:
                transactionTAG = "Wallet Fragment";
                currentFragment = new WalletFragment();
                break;
        }
        replaceFragment(currentFragment, transactionTAG);
        Log.d(TAG, "Fragment Type: " + currentFragment.getClass().toString());
    }

    /**
     * Set the profile attribute of object
     * @param   profile UserProfile object representing the user profile
     */
    @Override
    public void setProfile(UserProfile profile) {
        Log.d(TAG, "setProfile running");
        this.profile = profile;
        if (currentFragment instanceof ProfileFragment) {
            onChange();
        }
    }

    /**
     * Set profile on profile fragment attribute on changes to object
     */
    @Override
    public void onChange() {
        Log.d(TAG, "onChange running");
        ((ProfileFragment) currentFragment).setProfile(profile);
    }
}
