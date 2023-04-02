package com.example.qrhunter;

import com.example.qrhunter.qrProfile.QRComment;
import com.example.qrhunter.searchPlayer.QRCodeListItem;
import com.example.qrhunter.searchPlayer.UserListItem;
import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class SearchPlayerUnitTest {

    /**
     *  Test the getter and setter og QRComment class.
     */
    @Test
    public void userListItemTest(){
        UserListItem userListItem = new UserListItem("test1",16);
        assertEquals("test1", userListItem.getUsername());
        assertEquals(16, userListItem.getScore());
        userListItem.setUsername("test21");
        assertEquals("test21", userListItem.getUsername());
        userListItem.setScore(64);
        assertEquals(64,userListItem.getScore());
    }
    @Test
    public  void qrCodeListItemTest() {
        QRCodeListItem qrCodeListItem = new QRCodeListItem("test1",16, "asimplehash");
        assertEquals("test1",qrCodeListItem.getName());
        assertEquals(16,qrCodeListItem.getScore());
        assertEquals("asimplehash",qrCodeListItem.getHash());
        qrCodeListItem.setName("test2");
        qrCodeListItem.setScore(64);
        qrCodeListItem.setHash("acomplexhash");
        assertEquals("test2",qrCodeListItem.getName());
        assertEquals(64,qrCodeListItem.getScore());
        assertEquals("acomplexhash",qrCodeListItem.getHash());
    }
}
