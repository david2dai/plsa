package com.david.data;

public class DocWordFreq {

    private int docId = -1;
    private int wordId = -1;
    private int freq = -1;

    public DocWordFreq(int docId, int wordId, int freq) {
        this.docId = docId;
        this.wordId = wordId;
        this.freq = freq;
    }

    public int getDocId() {
        return docId;
    }
    
    public int getWordId() {
        return wordId;
    }
    
    public int getFreq() {
        return freq;
    }
}
