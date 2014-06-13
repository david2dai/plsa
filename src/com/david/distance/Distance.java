package com.david.distance;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

import java.util.Queue;
import java.util.PriorityQueue;
import java.util.Comparator;

public class Distance {
    private int R = 0;
    private int K = 0;
    private double[][] topicDistMatrix = null; 
    
    // topicDistFile word/doc topic distribution file
    public void loadTopicDist(String topicDistFile, int R, int K) {
        topicDistMatrix = new double[R][K];
        this.R = R;
        this.K = K;

        try {
            BufferedReader bReader = new BufferedReader( new FileReader(topicDistFile) );
            String line = null;
            String[] probs = null;
            int id = 0;
            double prob = 0.0;
            double len = 0.0;
            while( (line=bReader.readLine())!=null ) {
                len = 0.0;
                probs = line.split(" ");
                for(int k=0;k<K;++k) {
                    prob = Double.parseDouble(probs[k]);
                    topicDistMatrix[id][k] = prob;
                    len += prob * prob;
                }
                len = Math.sqrt(len);
                for(int k=0;k<K;++k) {
                    topicDistMatrix[id][k] /= len;
                }
                ++id;
            }
            bReader.close();
        } catch(IOException e) {
            System.out.println(e);
        }
    }

    class NearItem {
        int id;
        double similarity;
        public NearItem(int id, double similarity) {
            this.id = id;
            this.similarity = similarity;
        }
    }

    public int[] getNearest(int origId, int topK) {
        int[] nearItems = new int[topK];
        Comparator<NearItem> comp = new Comparator<NearItem>() {

            public int compare(NearItem a, NearItem b) {
                if(a.similarity>b.similarity) { return 1; }
                else if (a.similarity<b.similarity) { return -1; }
                else { return 0; }
            }
        };

        int dim = topicDistMatrix[0].length;
        for(int i=0;i<dim;++i) {
            System.out.println(topicDistMatrix[origId][i]);
        }


        Queue<NearItem> pq = new PriorityQueue<NearItem>(topK, comp);
        NearItem peekItem = null;
        double similarity = 0.0;
       
        for(int i=0;i<R;++i) {
            if(origId==i) { continue; }
             
            similarity = cosine(topicDistMatrix[origId], topicDistMatrix[i]);
            //System.out.println(similarity);
            if(pq.size()<topK) {
                pq.add(new NearItem(i, similarity));
            } else {
                peekItem = pq.peek();
                if(similarity>peekItem.similarity) {
                    pq.poll();
                    pq.add(new NearItem(i, similarity));
                }
            }
        }

        int m = topK;
        while(!pq.isEmpty()) {
            nearItems[--m] = pq.peek().id;
            pq.poll();    
        }
       
        return nearItems; 
    }

    private double cosine(double[] X, double[] Y) {
        double res = 0.0;
        int dim = X.length;
        for(int i=0;i<dim;++i) {

            res += X[i] * Y[i];
        }
        return res;
    }
}
