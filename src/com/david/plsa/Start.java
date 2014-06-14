package com.david.plsa;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;

import com.david.utils.Utils;
import com.david.data.DataInfo;
import com.david.data.DocWordFreq;
import com.david.plsa.PreProcess;
import com.david.distance.Distance;

public class Start {
    
    public static void preProcess(String folderPath, String stopWordFile, 
            String vocFile, int lowFreq, String matrixFile) {

        System.out.println ("Start: PreProcess");
        PreProcess preProcess = new PreProcess(folderPath, stopWordFile, vocFile, 
                lowFreq, matrixFile); 
        preProcess.doProcess();
        System.out.println ("Finish: PreProcess");
    }

    public static void doPLSA(String vocFile, String matrixFile, int K, int iter, 
            double eps, String resultFolder, int topK) {

        System.out.println ("Start: doPLSA");
        PLSA plsa = new PLSA(vocFile, matrixFile, K);
        plsa.train(iter, eps);
        plsa.saveResult(resultFolder, topK);
        System.out.println ("Finish: doPLSA");
    }

    public static void processUasge() {
        String usage = "process\t-corpusfolder <folder> -stopwordfile <file> " +
            "-vocfile <file> -matrixfile <file> -lowfreq <int>\n" +
            "\nParameters:\n" +
            "-corpusfolder  raw corpus folder\n" +
            "-stopwordfile  stopwords vocabulary file.\n" +
            "-vocfile       the file for the generated vocabulary of corpus.\n" +
            "-matrixfile    the matrix file generated, this file will be used for train.\n" +
            "-lowfreq       low freq(<lowfreq) word will be discarded.\n" +
            "-topic         the topic #.\n";
        System.out.println(usage);
    }
    
    public static void plsaUasge() {
        String usage = "plsa\t-matrixfile <file> -vocfile <file> " +
            "-topic <int> -iter <int> -eps <double> -topk <int> " +
            "-resultfolder <file>\n" +
            "\nParameters:\n" +
            "-matrixfile    the matrix file generated, this file will be used for train.\n" +
            "-vocfile       the file for the generated vocabulary of corpus.\n" +
            "-topic         the topic #.\n" +
            "-iter          the max train iterate times.\n" +
            "-eps           early stop threshold(tool will sotp when the likelihood diff of 2 iterate < eps).\n" + 
            "-topk          the topK words of a topic will show.\n" +
            "-resultfolder  train reslut folder.";
        System.out.println(usage);
    }
    
    public static void main(String [] args) {

        Option opProcess = new Option ("process", "Do process");
        Option opDoPLSA = new Option ("plsa", "Do plsa");

        Option opVocFile = OptionBuilder.hasArg().create("vocfile");
        Option opMatrixFile = OptionBuilder.hasArg().create("matrixfile");
        Option opStopwordsFile = OptionBuilder.hasArg().create("stopwordfile");
        Option opFolderCorpus = OptionBuilder.hasArg().create ("corpusfolder");
        Option opLowFreq = OptionBuilder.hasArg().create("lowfreq");
        
        Option opTopic = OptionBuilder.hasArg().create ("topic");
        Option opIterate = OptionBuilder.hasArg().create ("iter");
        Option opEps = OptionBuilder.hasArg().create ("eps");
        Option opTopK = OptionBuilder.hasArg().create ("topk");
        Option opFolderResult = OptionBuilder.hasArg().create ("resultfolder");

        Options options = new Options();
        options.addOption(opProcess); 
        options.addOption(opDoPLSA); 
        options.addOption(opVocFile); 
        options.addOption(opMatrixFile); 
        options.addOption(opStopwordsFile); 
        options.addOption(opFolderCorpus); 
        options.addOption(opLowFreq); 

        options.addOption (opTopic);
        options.addOption (opIterate);
        options.addOption (opEps);
        options.addOption (opTopK);
        options.addOption (opFolderResult);

        CommandLineParser parser = new BasicParser();

        try {
            // parse the command line arguments
            CommandLine line = parser.parse(options, args);

            if(line.hasOption("process")) { // parse process args
                String corpusFolder = null; 
                String stopWordFile = null; 
                String vocFile = null; 
                String matrixFile = null; 
                int lowFreq = 5;

                if (line.hasOption("corpusfolder")) {
                    corpusFolder = line.getOptionValue("corpusfolder");
                } else {
                    System.out.println("No corpusfolder.");
                    processUasge();
                    return;
                }

                if (line.hasOption("stopwordfile")) {
                    stopWordFile = line.getOptionValue("stopwordfile");
                } else {
                    System.out.println("No stopwordfile.");
                    processUasge();
                    return;
                }

                if (line.hasOption("vocfile")) {
                    vocFile = line.getOptionValue("vocfile");
                } else {
                    System.out.println("No vocfile.");
                    processUasge();
                    return;
                }
                
                if (line.hasOption("matrixfile")) {
                    matrixFile = line.getOptionValue("matrixfile");
                } else {
                    System.out.println("No matrixFile.");
                    processUasge();
                    return;
                }

                if (line.hasOption("lowfreq")) {
                    lowFreq = Integer.valueOf (line.getOptionValue("lowfreq"));
                }
                
                preProcess(corpusFolder, stopWordFile, vocFile, lowFreq, matrixFile);

            } else if (line.hasOption("plsa")) { // parse plsa args
                String resultFolder = null; 
                String vocFile = null; 
                String matrixFile = null; 
                int K = 50;
                int iter = 2000;
                int topK = 25;
                double eps = 1E-4;

                if (line.hasOption("resultfolder")) {
                    resultFolder = line.getOptionValue("resultfolder");
                } else {
                    System.out.println("No resultfolder.");
                    plsaUasge();
                    return;
                }

                if (line.hasOption("vocfile")) {
                    vocFile = line.getOptionValue("vocfile");
                } else {
                    System.out.println("No vocfile.");
                    plsaUasge();
                    return;
                }
                
                if (line.hasOption("matrixfile")) {
                    matrixFile = line.getOptionValue("matrixfile");
                    plsaUasge();
                } else {
                    System.out.println("No matrixFile.");
                    return;
                }

                if (line.hasOption("topic")) {
                    K = Integer.valueOf (line.getOptionValue("topic"));
                }

                if (line.hasOption("iter")) {
                    iter = Integer.valueOf (line.getOptionValue("iter"));
                }

                if (line.hasOption("eps")) {
                    eps = Double.valueOf (line.getOptionValue("eps"));
                }
                
                if (line.hasOption("topk")) {
                    topK = Integer.valueOf (line.getOptionValue("topk"));
                }
        
                doPLSA(vocFile, matrixFile, K, iter, eps, resultFolder, topK);
            } else {
                return;
            }

        } catch(ParseException e) {
            System.out.println(e);
        }

        /* 
        Distance distance = new Distance();
        //distance.loadTopicDist("/home/david/David/java/corpus/result2/docTopics", 7193, 50);
        distance.loadTopicDist("/home/david/David/java/corpus/result2/wordTopics", 8497, 50);
        int origId = 3102; //wheat
        int topK2 = 50;
        int[] nearItems = distance.getNearest(origId, topK2);
        System.out.println("nearest items for " + origId);
        
        HashMap<Integer, String> vocR = PreProcess.loadVocR(vocFile);
        int id = 0;
        for(int i=0;i<topK2;++i) {
            id = nearItems[i];
            System.out.println(vocR.get(new Integer(id)));
            //System.out.println(nearItems[i]);
        }
        */
    }
}
