package com.example.qrhunter;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class ProfileUnitTest {

    /**
     * Generates a dummy user profile for testing
     * @return userProfile UserProfile object representing a user
     */
    public UserProfile newProfile() {
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
    public void testSetName() {
        UserProfile userProfile = newProfile();
        userProfile.setFullName("testingName");
        Assert.assertTrue("Names do not match",
                userProfile.getFullName().equals("testingName"));
    }

    /**
     * Tests if setUserName method of UserProfile class functions properly
     */
    @Test
    public void testSetUserName() {
        UserProfile userProfile = newProfile();
        userProfile.setUserName("testingUserName");

        Assert.assertTrue("Usernames do not match",
                userProfile.getUserName().equals("testingUserName"));
    }

    /**
     * Tests if setPhone method of UserProfile class functions properly
     */
    @Test
    public void testSetPhone() {
        UserProfile userProfile = newProfile();
        userProfile.setPhone("1111111111");

        Assert.assertTrue("Phone numbers do not match",
                userProfile.getPhone().equals("1111111111"));
    }

    /**
     * Tests if setEmail method of UserProfile class functions properly
     */
    @Test
    public void testSetEmail() {
        UserProfile userProfile = newProfile();
        userProfile.setEmail("testEmail@email.com");

        Assert.assertTrue("Emails do not match",
                userProfile.getEmail().equals("testEmail@email.com"));
    }
}
