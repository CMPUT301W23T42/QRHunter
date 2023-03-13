package com.example.qrhunter;

import com.example.qrhunter.fragments.WalletFragment;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class WalletUnitTest {

    private ArrayList<QRCode> qrList() {
        ArrayList<QRCode> list = new ArrayList<>();
        list.add(new QRCode("test date", "c603089790d3df00ae3a563e77dd6ac720722c44507ed043c64a46627e6416f3", "test name", null, "test owner", 7, "test id"));
        return list;
    }

    @Test
    void testCountPoints() {
        ArrayList<QRCode> list = qrList();
        WalletFragment walletFragment = new WalletFragment();
        walletFragment.countPoints(list);

    }
}
