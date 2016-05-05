package com.example.hanjun.testbook;

import android.test.InstrumentationTestCase;

/**
 * Created by hanjun on 2016/4/29.
 */
public class TestClass extends InstrumentationTestCase {
    public void test() throws Exception{
        final int expected = 1;
        final int reality = 5;
        assertEquals(expected, reality);
    }
}
