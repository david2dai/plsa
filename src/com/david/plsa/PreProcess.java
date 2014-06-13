package com.david.plsa;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.ArrayList;

import java.util.regex.*;

import com.david.data.DocWordFreq;
import com.david.data.DataInfo;

public class PreProcess {

    // if freq(word) < lowFreq: it will discard
    private int lowFreq = 0;  
    // raw corpus folder 
    private String folderPath = null; 
    // stopwords dict
    private String stopWordFile = null; 
    // corpus vocabulary
    private String vocFile = null; 
    // the final sparse matrix file for plsa
    private String matrixFile = null; 

    public PreProcess(String folderPath, String stopWordFile, String vocFile, int lowFreq,
            String matrixFile) {
        this.lowFreq = lowFreq;
        this.folderPath = folderPath;
        this.vocFile = vocFile;
        this.stopWordFile = stopWordFile;
        this.matrixFile = matrixFile;
    }

    public void doProcess() {
        ArrayList<String> files = new ArrayList<String>();
        // get files list of the corpus
        getFileList(folderPath, files);
        // generate the vocabulary
        genVocabulary(files, stopWordFile, lowFreq, vocFile);
        // generate the D*V sparse matrix and save it into a file.
        genDVMatrix(files, vocFile, matrixFile);
    }

    private void getFileList(String path, ArrayList<String> files) {
        File curFile = new File(path);
        if(curFile.isDirectory()) { // current path is a dir
            String[] subPaths = curFile.list();
            for(int i=0;i<subPaths.length;++i) {
                getFileList(path+"/"+subPaths[i], files);
            }
        } else if(curFile.isFile()) { // current path is a file
            files.add(path);
        }
    }

    private HashMap<String,Integer> loadVoc(String file) {
        HashMap<String,Integer> voc= new HashMap<String,Integer>();
        try {
            BufferedReader bReader = new BufferedReader(new FileReader(file));
            String line = null;
            int wordId = 0;
            while( (line=bReader.readLine())!=null ) {
                voc.put(line, new Integer(wordId));
                ++wordId;
            } 
            bReader.close();
        } catch(IOException e) {
            System.out.println(e);
        }
        
        System.out.println("The size of the vocabulary is:"+voc.size()); 
        return voc;
    }

    private void genVocabulary(ArrayList<String> files, String stopWordsFile, int lowFreq,
            String vocFile) {
        //HashSet<String> stopWords = loadStopWords(stopWordsFile);
        HashMap<String, Integer> stopWords = loadVoc(stopWordsFile);
        Map<String, Integer> voc = new TreeMap<String, Integer>();
        // a single char is filterd
        Pattern pattern = Pattern.compile("[A-Za-z]{2,}");
        String line = null;
        String word = null;
        Integer freq = null;
        final Integer FREQ_1 = new Integer(1);

        //for(int i=0;i<files.size();++i) {
        for(String file: files) {
            
            try {
                BufferedReader bReader = new BufferedReader( new FileReader(file) );

                while( (line=bReader.readLine())!=null ) {
        
                    Matcher match = pattern.matcher(line);
                    while(match.find()) {
                        word = match.group().toLowerCase();
                        if(stopWords.containsKey(word)) {
                            //System.out.println("Find a Stop words!");
                        } else {
                            freq = voc.get(word);
                            if(freq==null) {
                                voc.put(word, FREQ_1);
                            } else {
                                voc.put(word, new Integer(freq.intValue()+1));
                            }
                        }
                    }
                }
                
                bReader.close();

            } catch (IOException e) {
                System.out.println(e);
            }

        } // for

        // Save the words to file
        try {
            BufferedWriter bWriter = new BufferedWriter( new FileWriter(vocFile) );
            for(Entry<String, Integer> entry: voc.entrySet()) {
                if(entry.getValue()>=lowFreq) {
                    bWriter.write(entry.getKey()+"\n");
                }
            }
            bWriter.close();
        } catch(IOException e) {
            System.out.println(e);
        }
        
    }

    // generate the D*V sparse matrix and save it into a file.
    // filepath1: v1:freq v2:freq ...
    // filepath2: v1:freq v2:freq ...
    // filepathM: v1:freq v2:freq ...
    private void genDVMatrix(ArrayList<String> files, String vocFile, String matrixFile) {
        String line = null;
        String word = null;
        HashMap<String, Integer> voc = loadVoc(vocFile);
        Pattern pattern = Pattern.compile("[A-Za-z]{2,}");
        final Integer FREQ_1 = new Integer(1);
        Integer freq = null; 
        Integer wordId = null; 

        BufferedWriter bWriter = null;
        
        try {
            bWriter = new BufferedWriter( new FileWriter(matrixFile) );
        } catch(IOException e) {
            System.out.println(e);
        }

        for(String file: files) {

            Map<Integer, Integer> wordsFreq = new TreeMap<Integer, Integer>(); 
            try {
                
                BufferedReader bReader = new BufferedReader( new FileReader(file) );
                
                while( (line=bReader.readLine())!=null ) {         
                    Matcher match = pattern.matcher(line);
                    while(match.find()) {
                        word = match.group().toLowerCase();
                        wordId = voc.get(word);
                        if(wordId!=null) {
                            freq = wordsFreq.get(wordId);
                            if(freq==null) {
                                wordsFreq.put(wordId, FREQ_1);
                            } else {
                                wordsFreq.put(wordId, new Integer(freq.intValue()+1));
                            }
                        }
                    }
                }

                bReader.close();

                // write
                if(wordsFreq.size()==0) {
                    System.out.println("Doc has no word in vocabulary");
                    continue;
                }

                bWriter.write(file);
                for(Entry<Integer, Integer> entry: wordsFreq.entrySet()) {
                    wordId = entry.getKey(); 
                    freq = entry.getValue(); 
                    bWriter.write(" "+wordId.toString()+":"+freq.toString());
                }
                bWriter.write("\n");

            } catch(IOException e) {
                System.out.println(e);
            }

        } // for
        try {
            if(bWriter!=null) bWriter.close();
        } catch(IOException e) {
            System.out.println(e);
        }
       
    } 
   
    public static HashMap<Integer, String> loadVocR(String file) {
        HashMap<Integer, String> vocR = new HashMap<Integer, String>();
        try {
            BufferedReader bReader = new BufferedReader( new FileReader(file) );
            String word = null;
            int wordId = 0;
            while( (word=bReader.readLine())!=null ) {
                vocR.put(new Integer(wordId), word);
                ++wordId;
            }
            bReader.close();
        } catch(IOException e) {
            System.out.println(e);
        }

        System.out.println("The size of the vocabularyR is:"+vocR.size()); 
        return vocR;
    }
    
    public static DataInfo loadDVMatrix(String matrixFile, ArrayList<DocWordFreq> docVocSMatrix) {
        int docCnt = 0;
        int wordCnt = 0;

        try {
            BufferedReader bReader = new BufferedReader( new FileReader(matrixFile) );
            String line = null;
            int docId = 0;
            int wordId = 0;
            int freq = 0;

            while( (line=bReader.readLine())!=null ) {
                String[] items = line.split(" ");
                // items[0] is the doc full filename
                for(int i=1;i<items.length;++i) {
                    String[] subItems = items[i].split(":");
                    wordId = Integer.parseInt(subItems[0]);
                    freq = Integer.parseInt(subItems[1]);
                    docVocSMatrix.add(new DocWordFreq(docId, wordId, freq));
                    wordCnt += freq;
                }
                ++docId;
            }
            docCnt = docId;
            bReader.close();
        } catch(IOException e) {
            System.out.println(e);
        } 
        return new DataInfo(docCnt, wordCnt);
    }
}
