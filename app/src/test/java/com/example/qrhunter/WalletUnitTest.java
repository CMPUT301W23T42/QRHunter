package com.example.qrhunter;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class WalletUnitTest {

    private ArrayList<QRCode> qrList() {
        ArrayList<QRCode> list = new ArrayList<>();
        list.add(new QRCode("test date", "test hash", "test name", null, "test owner", 7, "test id"));
        return list;
    }

    @Test
    void testCountPoints() {
        ArrayList<QRCode> list = qrList();

    }
}
