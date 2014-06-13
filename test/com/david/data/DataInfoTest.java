package com.david.data;

import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertSame;

import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

public class DataInfoTest {

    @Test
    public void testDataInfo() {
        DataInfo dataInfo = new DataInfo(5, 10);
        org.junit.Assert.assertSame("Should be same", 5, dataInfo.getDocCnt());  
        org.junit.Assert.assertSame("Should be same", 10, dataInfo.getWordCnt());  
    }

}
