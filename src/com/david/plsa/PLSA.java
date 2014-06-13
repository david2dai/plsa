package com.david.plsa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;
import java.util.PriorityQueue;
import java.util.Comparator;

import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

import com.david.data.DataInfo;
import com.david.data.DocWordFreq;
import static com.david.utils.Utils.*;

public class PLSA {

    private HashMap<Integer, String> vocR = null;
    private ArrayList<DocWordFreq> docVocSMatrix = null;
    private int V = 0;
    private int D = 0;
    private int K = 0;
    private int totalWords = 0;

    private double[] p_z = null; 
    private double[][] p_w_z = null; 
    private double[][] p_d_z = null; 
    
    private double[] p_z_old = null; 
    private double[][] p_w_z_old = null; 
    private double[][] p_d_z_old = null; 

    private double[] p_z_dw = null; 
    
    public PLSA (String vocFile, String matrixFile, int K) {
        docVocSMatrix = new ArrayList<DocWordFreq>();
        vocR = PreProcess.loadVocR(vocFile);
        DataInfo dataInfo = PreProcess.loadDVMatrix(matrixFile, docVocSMatrix);
        
        V = vocR.size();
        D = dataInfo.getDocCnt();
        totalWords = dataInfo.getWordCnt();
        this.K = K;

        p_z = new double[K];
        p_w_z = new double[V][K];
        p_d_z = new double[D][K];

        p_z_old = new double[K];
        p_w_z_old = new double[V][K];
        p_d_z_old = new double[D][K];

        p_z_dw = new double[K];
        System.out.println("V=" + V + " D=" + D + " K=" + K + " total=" + totalWords);
    }

    private void randomInit() {
        // p_z random assign
        for(int k=0;k<K;++k) { p_z[k] = Math.random() + 0.5; }
        normalize(p_z);

        // p_w_z random assign and normalize(col) 
        for(int i=0;i<V;++i) {
            for(int k=0;k<K;++k) { p_w_z[i][k] = Math.random() + 0.5; }
        }
        normalize(p_w_z);

        // p_d_z random assign and normalize(col) 
        for(int i=0;i<D;++i) {
            for(int k=0;k<K;++k) { p_d_z[i][k] = Math.random() + 0.5; }
        }
        normalize(p_d_z);
    }

    public void train (int maxIter, double eps) {
        int docId = 0;
        int wordId = 0;
        int freq = 0;
        double deltaP = 0.0; 
        double lastLikelihood = 0.0;
        double curLikelihood = 0.0;
        double deltaLikelihood = 0.0;
        double [] tmp1 = null;
        double [][] tmp2 = null;
        
        randomInit();
        lastLikelihood = logLikelihood();
        
        for(int iter=0;iter<maxIter;++iter) {
            // swap
            tmp1 = p_z; p_z = p_z_old; p_z_old = tmp1;
            tmp2 = p_d_z; p_d_z = p_d_z_old; p_d_z_old = tmp2;
            tmp2 = p_w_z; p_w_z = p_w_z_old; p_w_z_old = tmp2;
            
            zero(p_z); zero(p_d_z); zero(p_w_z);

            for(DocWordFreq docWordFreq: docVocSMatrix) {
                docId = docWordFreq.getDocId();
                wordId = docWordFreq.getWordId();
                freq = docWordFreq.getFreq();

                // E step
                for(int k=0;k<K;++k) {
                    p_z_dw[k] = p_z_old[k] * p_d_z_old[docId][k] * p_w_z_old[wordId][k];
                }
                normalize(p_z_dw);
                
                // M step
                for(int k=0;k<K;++k) {
                    deltaP = freq * p_z_dw[k];
                    p_z[k] += deltaP;
                    p_d_z[docId][k] += deltaP;
                    p_w_z[wordId][k] += deltaP;
                }
            } // train a <doc, word, freq>

            // normalize
            normalize(p_d_z);
            normalize(p_w_z);
            //normalize(p_z);
            for(int k=0;k<K;++k) { p_z[k] /= totalWords; }
            
            curLikelihood = logLikelihood();
            deltaLikelihood = curLikelihood - lastLikelihood;
            lastLikelihood = curLikelihood;
            System.out.println("iter:" + (iter + 1) + "/" + maxIter + " likelihood delta:" + deltaLikelihood);
            
            if(deltaLikelihood < eps) {
                System.out.println("likelihood delta < eps:" + deltaLikelihood);
                break;
            }
        } // a iter
    } // train funtion end

    private double logLikelihood() {
        double likelihood = 0.0;
        double p_wd = 0.0;
        int docId = 0;
        int wordId = 0;
        int freq = 0;

        for(DocWordFreq docWordFreq: docVocSMatrix) {
            docId = docWordFreq.getDocId();
            wordId = docWordFreq.getWordId();
            freq = docWordFreq.getFreq();
            p_wd = 0.0;
            for(int k=0;k<K;++k) {
                p_wd += p_z[k] * p_d_z[docId][k] * p_w_z[wordId][k];
            }
            if(p_wd>0) {
                likelihood += freq * Math.log(p_wd);
            }
        }
        return likelihood;
    }

    public void saveResult (String folder, int topK) {
        save(p_z, folder + "/p_z");
        //w1: ...
        //wV: ...
        save(p_w_z, folder + "/p_w_z");
        //d1: ...
        //dD: ...
        save(p_d_z, folder + "/p_d_z");
        
        calDocTopics(folder + "/docTopics");
        calWordTopics(folder + "/wordTopics");
        calTopicWords(topK, folder + "/topicTopKWords");
    }

    // Cal document topic distribution
    private void calDocTopics(String file) {
        double[][] p_z_d = new double[D][K];
        for(int i=0;i<D;++i) {
            for(int k=0;k<K;++k) { p_z_d[i][k] = p_z[k] * p_d_z[i][k]; }
            normalize(p_z_d[i]);
        }
        //d1: ...
        //d2: ...
        //dD: ...
        save(p_z_d, file);
    }

    // Cal word topic distribution
    private void calWordTopics(String file) {
        double[][] p_z_w = new double[V][K];
        for(int i=0;i<V;++i) {
            for(int k=0;k<K;++k) { p_z_w[i][k] = p_z[k] * p_w_z[i][k]; }
            normalize(p_z_w[i]);
        }
        //w1: ...
        //w2: ...
        //wV: ...
        save(p_z_w, file);
    }

    // get topic words
    private class WordProb {
        int wordId;
        double prob;

        public WordProb(int wordId, double prob) {
            this.wordId = wordId;
            this.prob = prob;
        }
    }

    private void calTopicWords(int topK, String topicWordsFile) {

        int[] topicWords = new int[topK];
        Comparator<WordProb> comp = new Comparator<WordProb>() {

            public int compare(WordProb a, WordProb b) {
                if(a.prob>b.prob) { return 1; }
                else if (a.prob<b.prob) { return -1; }
                else { return 0; }
            }
        };

        Queue<WordProb> pq = new PriorityQueue<WordProb>(topK, comp);
        WordProb minPWord = null;
        int wordId = 0;
        
        try {
            BufferedWriter bWriter= new BufferedWriter( new FileWriter(topicWordsFile) );
            
            for(int k=0;k<K;++k) {
                for(int j=0;j<V;++j) {
                    if(pq.size()<topK) {
                        pq.add(new WordProb(j, p_w_z[j][k]));
                    } else {
                        minPWord = pq.peek();
                        if(p_w_z[j][k]>minPWord.prob) {
                            pq.poll();
                            pq.add(new WordProb(j, p_w_z[j][k]));
                        }
                    }
                }

                int m = topK;
                while(!pq.isEmpty()) {
                    topicWords[--m] = pq.peek().wordId;
                    pq.poll();    
                }

                bWriter.write("Topic " + k + ":\n");
                for(m=0;m<topK;++m) {
                    wordId = topicWords[m];
                    bWriter.write(vocR.get(new Integer(wordId)) + "\n");
                }
                bWriter.write("\n");
            }
            bWriter.close();
        } catch(IOException e) {
            System.out.println(e);
        }
    }
}
