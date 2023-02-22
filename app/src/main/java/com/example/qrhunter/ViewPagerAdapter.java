package com.example.qrhunter;

import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.qrhunter.fragments.EditProfileFragment;
import com.example.qrhunter.fragments.HomeFragment;
import com.example.qrhunter.fragments.LoginFragment;
import com.example.qrhunter.fragments.ProfileFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private final String ID = Settings.Secure.ANDROID_ID;
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new LoginFragment();
            case 1:
                return new HomeFragment();
            case 3:
                return new ProfileFragment(ID);
            case 4:
                return new EditProfileFragment();
            default:
                return new HomeFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    public String getID() {
        return ID;
    }
}
