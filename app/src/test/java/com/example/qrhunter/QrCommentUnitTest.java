package com.example.qrhunter;

import com.example.qrhunter.qrProfile.QRComment;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class QrCommentUnitTest {
    public QRComment newComment(){
        QRComment qrComment = new QRComment("Test comment","20223-3-25 10:25","Roy");
        return qrComment;
    }

    /**
     *  Test the getter and setter og QRComment class.
     */
    @Test
    public void testGetter(){
        QRComment comment = newComment();
        Assert.assertEquals(comment.getComment(),"Test comment");
        Assert.assertEquals(comment.getDate(),"20223-3-25 10:25");
        Assert.assertEquals(comment.getUser(),"Roy");
    }
}
