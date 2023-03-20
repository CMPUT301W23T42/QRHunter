package com.example.qrhunter;

import com.example.qrhunter.fragments.EditProfileFragment;
import com.example.qrhunter.fragments.ProfileFragment;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class EditProfileFragmentUnitTest {
    private UserProfile profile;
    private EditProfileFragment fragment;

    /**
     * Initializes attributes before testing begins
     */
    @Before
    public void setup() {
        profile = newProfile();
        fragment = new EditProfileFragment(profile);
    }

    /**
     * Generates a dummy user profile for testing
     * @return userProfile UserProfile object representing a user
     */
    private UserProfile newProfile() {
        UserProfile userProfile = new UserProfile();
        userProfile.setFullName("Test User");
        userProfile.setUserName("TestU");
        userProfile.setPhone("4031111111");
        userProfile.setEmail("test@test.com");
        return userProfile;
    }

    /**
     * Generates dummy user profile for testing
     * @param name String object representing profile full name
     * @param user String object representing profile user name
     * @param phone String object representing profile phone number
     * @param email String object representing profile email address
     * @return userProfile UserProfile object representing a user
     */
    private UserProfile newProfile(String name, String user, String phone, String email) {
        UserProfile userProfile = new UserProfile();
        userProfile.setFullName(name);
        userProfile.setUserName(user);
        userProfile.setPhone(phone);
        userProfile.setEmail(email);
        return userProfile;
    }

    /**
     * Tests if EditProfileFragment class initialization, functions properly
     */
    @Test
    private void testEditProfileInit() {
        UserProfile fragmentProfile = fragment.getProfile();
        UserProfile testProfile = newProfile();
        Assert.assertTrue("Full names do not match",
                testProfile.getFullName() == fragmentProfile.getFullName());
        Assert.assertTrue("Full names do not match",
                testProfile.getUserName() == fragmentProfile.getUserName());
        Assert.assertTrue("Full names do not match",
                testProfile.getPhone() == fragmentProfile.getPhone());
        Assert.assertTrue("Full names do not match",
                testProfile.getEmail() == fragmentProfile.getEmail());
    }

    /**
     * Tests if setProfile method of EditProfileFragment class functions properly
     */
    @Test
    private void testSetProfile() {
        UserProfile testProfile = newProfile("testcase", "testuser",
                "4034034034", "test@email.com");
        fragment.setProfile(testProfile);
        UserProfile fragmentProfile = fragment.getProfile();

        Assert.assertTrue("Full names do not match",
                testProfile.getFullName() == fragmentProfile.getFullName());
        Assert.assertTrue("Full names do not match",
                testProfile.getUserName() == fragmentProfile.getUserName());
        Assert.assertTrue("Full names do not match",
                testProfile.getPhone() == fragmentProfile.getPhone());
        Assert.assertTrue("Full names do not match",
                testProfile.getEmail() == fragmentProfile.getEmail());
    }
}
