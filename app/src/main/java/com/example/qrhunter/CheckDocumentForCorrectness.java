package com.example.qrhunter;

import android.util.Log;

import com.google.firebase.firestore.QueryDocumentSnapshot;

public class CheckDocumentForCorrectness {
    public static boolean codelistCheckForCorrectness(QueryDocumentSnapshot doc) {
        if (doc.get("score") == null || doc.get("owner") == null || doc.get("hash") == null || doc.get("name") == null) {
            return false;
        }
        try {
            Integer.parseInt((String) doc.get("score"));
        }
        catch(Exception e){
            return false;
        }
        double score = Integer.parseInt((String) doc.get("score"));
        if (score < 0 || score > 2147483647) {
            return false;
        }
        String hash = (String) doc.get("hash");
        if (hash.length() != 64) {
            return false;
        }
        return true;
    }
}
