package com.david.data;

public class DataInfo {
    private int docCnt = 0;
    private int wordCnt = 0;

    public DataInfo(int docCnt, int wordCnt) {
        this.docCnt = docCnt;
        this.wordCnt = wordCnt;
    }

    public int getDocCnt() {
        return docCnt;
    }

    public int getWordCnt() {
        return wordCnt;
    }

}
