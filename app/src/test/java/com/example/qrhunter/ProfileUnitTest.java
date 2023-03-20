package com.example.qrhunter;

import org.junit.Assert;
import org.junit.Test;

public class ProfileUnitTest {

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
     * Tests if setFullName method of UserProfile class functions properly
     */
    @Test
    private void testSetName() {
        UserProfile userProfile = newProfile();
        userProfile.setFullName("testingName");
        Assert.assertTrue("Names do not match",
                userProfile.getUserName() == "testingName");
    }

    /**
     * Tests if setUserName method of UserProfile class functions properly
     */
    @Test
    private void testSetUserName() {
        UserProfile userProfile = newProfile();
        userProfile.setUserName("testingUserName");

        Assert.assertTrue("Usernames do not match",
                userProfile.getUserName() == "testingUserName");
    }

    /**
     * Tests if setPhone method of UserProfile class functions properly
     */
    @Test
    private void testSetPhone() {
        UserProfile userProfile = newProfile();
        userProfile.setPhone("1111111111");

        Assert.assertTrue("Phone numbers do not match",
                userProfile.getPhone() == "1111111111");
    }

    /**
     * Tests if setEmail method of UserProfile class functions properly
     */
    @Test
    private void testSetEmail() {
        UserProfile userProfile = newProfile();
        userProfile.setEmail("testEmail@email.com");

        Assert.assertTrue("Emails do not match",
                userProfile.getEmail() == "testEmail@email.com");
    }
}
