package com.example.qrhunter;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.qrhunter.fragments.EditProfileFragment;
import com.example.qrhunter.fragments.HomeFragment;
import com.example.qrhunter.fragments.LoginFragment;
import com.example.qrhunter.fragments.ProfileFragment;

public class TabManager extends FragmentStateAdapter implements UserInfo {
    private final String TAG = "Tab Manager";
    private UserProfile profile = null;
    private ProfileFragment profileFragment = null;

    public TabManager(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Log.i(TAG, "Running createFragment, " + position);
        switch (position) {
            case 3:
                if (profileFragment != null) {
                    return profileFragment;
                }
                throw new RuntimeException("Unintended Access");
            case 4:
                return new EditProfileFragment(profile);
            case 5:
                return new LoginFragment();
            default:
                return new HomeFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 6;
    }

    @Override
    public void setProfile(UserProfile profile) {
        Log.d(TAG, "setProfile running");
        this.profile = profile;
        if (profileFragment != null) {
            onChange();
        } else {
            profileFragment = new ProfileFragment(profile);
        }
    }

    @Override
    public void onChange() {
        Log.d(TAG, "onChange running");
        profileFragment.setProfile(profile);
    }
}
