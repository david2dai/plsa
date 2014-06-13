package com.david.utils;

import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

public class Utils {
    
    public static void zero(double[][] matrix) {
        int R = matrix.length;
        int C = matrix[0].length;
        for(int i=0;i<R;++i) {
            for(int j=0;j<C;++j) { matrix[i][j] = 0.0; }
        }
    }

    public static void zero(double[] vector) {
        int length = vector.length;
        for(int i=0;i<length;++i) { vector[i] = 0.0; }
    }

    public static void normalize(double[] vector) {
        double sum = 0.0;
        int length = vector.length;
        for(int i=0;i<length;++i) { 
            if(vector[i]<1E-20) 
                vector[i] = 0.0; 
            else
                sum += vector[i]; 
        }
        for(int i=0;i<length;++i) { vector[i] /= sum; }
    }

    // col normalize
    public static void normalize(double[][] matrix) {
        double sum = 0.0;
        int R = matrix.length;
        int C = matrix[0].length;
        for(int i=0;i<C;++i) {
            sum = 0.0;
            for(int j=0;j<R;++j) { 
                if(matrix[j][i]<1E-20) 
                    matrix[j][i] = 0.0;
                else 
                    sum += matrix[j][i]; 
            }
            for(int j=0;j<R;++j) { matrix[j][i] /= sum; }
        }
    }

    // row first
    public static void save(double[][] matrix, String file) {
        try {
            BufferedWriter bWriter = new BufferedWriter( new FileWriter(file) );
            int R = matrix.length;
            int C = matrix[0].length;
            for(int i=0;i<R;++i) {
                for(int j=0;j<C;++j) {
                    bWriter.write(matrix[i][j] + " ");
                }
                bWriter.write("\n");
            }
            bWriter.close();
        } catch(IOException e) {
            System.out.println(e);
        }
    }
    
    public static void save(double[] vector, String file) {
        try {
            BufferedWriter bWriter = new BufferedWriter( new FileWriter(file) );
            int length = vector.length;
            for(int i=0;i<length;++i) {
                bWriter.write(vector[i] + " ");
            }
            bWriter.write("\n");
            bWriter.close();
        } catch(IOException e) {
            System.out.println(e);
        }
    }

}
