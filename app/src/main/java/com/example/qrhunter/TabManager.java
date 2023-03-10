package com.example.qrhunter;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.qrhunter.fragments.EditProfileFragment;
import com.example.qrhunter.fragments.HomeFragment;
import com.example.qrhunter.fragments.LoginFragment;
import com.example.qrhunter.fragments.ProfileFragment;
import com.example.qrhunter.fragments.ScannerFragment;
import com.example.qrhunter.fragments.WalletFragment;
import com.example.qrhunter.fragments.SearchPlayerFragment;

public class TabManager implements UserInfo {
    private final String TAG = "Tab Manager";
    private UserProfile profile = null;

    private FragmentManager fragmentManager;
    private Fragment currentFragment;

    public TabManager(@NonNull FragmentActivity fragmentActivity) {
        fragmentManager = fragmentActivity.getSupportFragmentManager();
    }

    public void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.commit();
    }

    public void switchFragment(int position) {
        Log.d(TAG, "Running createFragment, " + position);
        switch (position) {
            case 2:
                ScannerFragment scannerFragment = new ScannerFragment();
                scannerFragment.setOnCameraCloseListener(new ScannerFragment.onCameraClose() {
                    @Override
                    public void onCameraClose() {
                        switchFragment(0);
                    }
                });
                currentFragment = scannerFragment;
                break;

            case 4:
                ProfileFragment profileFragment = new ProfileFragment(profile);
                profileFragment.setOnEditProfileListener(new ProfileFragment.onEditProfileListener() {
                    @Override
                    public void onEditProfile() {
                        switchFragment(6);
                    }
                });
                currentFragment = profileFragment;
                break;

            case 3:
                SearchPlayerFragment searchPlayerFragment = new SearchPlayerFragment();

                currentFragment = searchPlayerFragment;
                break;

            case 6:
                EditProfileFragment editProfileFragment = new EditProfileFragment(profile);
                editProfileFragment.setOnCompleteListener(new EditProfileFragment.onCompleteListener() {
                    @Override
                    public void onComplete() {
                        switchFragment(4);
                    }
                });
                currentFragment = editProfileFragment;
                break;

            case 7:
                currentFragment = new LoginFragment();
                break;

            default:
                currentFragment = new WalletFragment();
                break;
        }
        replaceFragment(currentFragment);
        Log.d(TAG, "Fragment Type: " + currentFragment.getClass().toString());
    }

    @Override
    public void setProfile(UserProfile profile) {
        Log.d(TAG, "setProfile running");
        this.profile = profile;
        if (currentFragment instanceof ProfileFragment) {
            onChange();
        }
    }

    @Override
    public void onChange() {
        Log.d(TAG, "onChange running");
        ((ProfileFragment) currentFragment).setProfile(profile);
    }
}
