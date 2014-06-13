package com.david.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class UtilsTest {

    @Test
    public void testNormalizeVector() {
        double[] vec = {0.5, 0.8, 0.7, 0.0};
        double tol = 1.0E-6;
        
        Utils.normalize(vec);
        assertEquals("Should equals, tol=1.0E-6", 0.25, vec[0], tol); 
        assertEquals("Should equals, tol=1.0E-6", 0.4, vec[1], tol); 
        assertEquals("Should equals, tol=1.0E-6", 0.35, vec[2], tol); 
        assertEquals("Should equals, tol=1.0E-6", 0.0, vec[3], tol); 
    }

    @Test
    public void testNormalizeMatrix() {
        double[][] mat = {{1.0, 3.0},
            {2.0, 4.0},
            {2.0, 3.0}};
        double tol = 1.0E-6;
        Utils.normalize(mat);
        // first column
        assertEquals("Should equals, tol=1.0E-6", 0.2, mat[0][0], tol); 
        assertEquals("Should equals, tol=1.0E-6", 0.4, mat[1][0], tol); 
        assertEquals("Should equals, tol=1.0E-6", 0.4, mat[2][0], tol); 
        // second column
        assertEquals("Should same, tol=1.0E-6", 0.3, mat[0][1], tol); 
        assertEquals("Should same, tol=1.0E-6", 0.4, mat[1][1], tol); 
        assertEquals("Should same, tol=1.0E-6", 0.3, mat[2][1], tol); 
    }

}
