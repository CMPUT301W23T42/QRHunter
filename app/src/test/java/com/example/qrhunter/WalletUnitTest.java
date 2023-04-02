package com.example.qrhunter;

import com.example.qrhunter.fragments.WalletFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.apache.tools.ant.taskdefs.Delete;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WalletUnitTest {

    FirebaseFirestore db;
    CollectionReference collectionReference;

    private ArrayList<QRCode> qrList() {
        ArrayList<QRCode> list = new ArrayList<>();
        list.add(new QRCode("test date", "c603089790d3df00ae3a563e77dd6ac720722c44507ed043c64a46627e6416f3", "test name", null, "test owner", 7, "test id"));
        return list;
    }

    @Before
    public void setup() {
        db = FirebaseFirestore.getInstance();
        collectionReference = db.collection("CodeList");
        Map<String, Object> QRInfo1 = new HashMap<>();
        QRInfo1.put("name", "Absolutely Solid Ready To Go TurboDraconic Tesseract");
        QRInfo1.put("date", "2023-03-12 11:24");
        QRInfo1.put("hash", "6fb8be06ad18006337bc6dbfd760eec088a69ba9aa021420213cf8bd1166f2c7");
        QRInfo1.put("owner", "Roy");
        QRInfo1.put("location", null);
        QRInfo1.put("score", 1);
        collectionReference
                .document("test doc1")
                .set(QRInfo1);
    }

    @Test
    public void testDeleteData() {
        WalletFragment walletFragment = new WalletFragment();
        // assert that document id exists
        //ArrayList<Boolean> found = new ArrayList<Boolean>();
        //found.add(true);
        //assertEquals(false,found.get(0));

        // assert that document id does not exist
    }

    @Test
    public void testCountPoints() {
        ArrayList<QRCode> list = qrList();
        WalletFragment walletFragment = new WalletFragment();
        assertEquals(7, walletFragment.countPoints(list));
    }
}
