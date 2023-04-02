package com.example.qrhunter;

import com.example.qrhunter.fragments.WalletFragment;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WalletUnitTest {

    private ArrayList<QRCode> qrList() {
        ArrayList<QRCode> list = new ArrayList<>();
        list.add(new QRCode("test date", "c603089790d3df00ae3a563e77dd6ac720722c44507ed043c64a46627e6416f3", "test name", null, "test owner", 7, "test id"));
        return list;
    }

    @Test
    public void testCountPoints() {
        ArrayList<QRCode> list = qrList();
        WalletFragment walletFragment = new WalletFragment();
        assertEquals(7, walletFragment.countPoints(list));
    }

    @Test
    public void testQRCodeGetDate() {
        QRCode qrCode = new QRCode("test date", "c603089790d3df00ae3a563e77dd6ac720722c44507ed043c64a46627e6416f3", "test name", null, "test owner", 7, "test id");
        assertEquals("test date", qrCode.getDate());
    }

    @Test
    public void testQRCodeGetHash() {
        QRCode qrCode = new QRCode("test date", "c603089790d3df00ae3a563e77dd6ac720722c44507ed043c64a46627e6416f3", "test name", null, "test owner", 7, "test id");
        assertEquals("c603089790d3df00ae3a563e77dd6ac720722c44507ed043c64a46627e6416f3", qrCode.getHash());
    }

    @Test
    public void testQRCodeGetName() {
        QRCode qrCode = new QRCode("test date", "c603089790d3df00ae3a563e77dd6ac720722c44507ed043c64a46627e6416f3", "test name", null, "test owner", 7, "test id");
        assertEquals("test name", qrCode.getName());
    }

    @Test
    public void testQRCodeGetOwner() {
        QRCode qrCode = new QRCode("test date", "c603089790d3df00ae3a563e77dd6ac720722c44507ed043c64a46627e6416f3", "test name", null, "test owner", 7, "test id");
        assertEquals("test owner", qrCode.getOwner());
    }

    @Test
    public void testQRCodeGetScore() {
        QRCode qrCode = new QRCode("test date", "c603089790d3df00ae3a563e77dd6ac720722c44507ed043c64a46627e6416f3", "test name", null, "test owner", 7, "test id");
        assertEquals(7, qrCode.getScore());
    }

    @Test
    public void testQRCodeGetId() {
        QRCode qrCode = new QRCode("test date", "c603089790d3df00ae3a563e77dd6ac720722c44507ed043c64a46627e6416f3", "test name", null, "test owner", 7, "test id");
        assertEquals("test id", qrCode.getId());
    }
}
