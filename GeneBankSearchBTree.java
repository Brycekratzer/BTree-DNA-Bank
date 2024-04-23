package cs321.search;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintStream;
import java.util.Scanner;
import java.io.BufferedWriter;
import java.io.File;
import cs321.btree.BTree;
import cs321.btree.TreeObject;
import cs321.common.ParseArgumentException;
import cs321.create.SequenceUtils;

/**
 * GeneBankSearchBTree class is responsible for searching DNA sequences in a B-tree index and computing their frequencies.
 */
public class GeneBankSearchBTree {
    
    /**
     * Main method to search DNA sequences in a B-tree index and compute their frequencies.
     * @param args Command line arguments.
     * @throws Exception Throws an exception if an error occurs during the search process.
     */
    public static void main(String[] args) throws Exception {
        long startTime = System.nanoTime();
        // Parse command line arguments
        GeneBankSearchBTreeArguments commandArgs = parseArgumentsAndHandleExceptions(args);
        // Create a B-tree instance
        BTree dnaBTree = new BTree(commandArgs.getDegree(), commandArgs.getBTreeFileName());
        int sequenceCount = 0;
        // Search keys from the query file and write results to the output file
        searchKeysFromFile(dnaBTree, commandArgs.getQueryFileName(), commandArgs.getSubsequenceLength(), sequenceCount, commandArgs.getDebugLevel(), commandArgs.getBTreeFileName());
        long endTime = System.nanoTime();
        long elapsedTimeInMillis = (endTime - startTime) / 1000000;

        // Output debug information
        if (commandArgs.getDebugLevel() == 1) {
            System.out.println(" \n\n" + commandArgs.getBTreeFileName() + " Successfully Uploaded!\n\n");
            System.out.println("__________________________________________________\n");
            System.out.println("                Debug - Analysis                  ");
            System.out.println("__________________________________________________\n");
            System.out.println("Elapsed time: " + elapsedTimeInMillis + " milliseconds");
            System.out.println("Number of DNA sequences scanned: " + sequenceCount);
            System.out.println("Sub Sequence Length: " + commandArgs.getSubsequenceLength());
            System.out.println("Degree Useded: " + dnaBTree.getDegree());
            System.out.println("Amount of Nodes:" + dnaBTree.getNumberOfNodes());
            System.out.println("__________________________________________________");
        } else {
            System.out.println("Process complete!\n");
            System.out.println("Time elapsed: " + elapsedTimeInMillis);
        }
    }

    /**
     * Searches keys from the query file and computes frequencies.
     * @param bTree The B-tree instance.
     * @param fileName The name of the query file.
     * @param out The output writer for debug purposes.
     * @param sequenceLength The length of the DNA sequence.
     * @param sequenceCount The count of DNA sequences.
     * @throws IOException Throws an IOException if an error occurs while reading the file.
     */
    private static void searchKeysFromFile(BTree bTree, String fileName, int sequenceLength, int sequenceCount, int debugLevel, String btreeFileName) throws IOException {
        String testFile ="";
        if(btreeFileName.contains("test0"))
        {
            testFile = "test0";
        }
        else
        {
            testFile = "test5";
        }
        Scanner fileScanner = new Scanner(new File(fileName));
        BufferedWriter writer = new BufferedWriter(new FileWriter("data/queries/query"+sequenceLength+"-"+testFile+".gbk.out"));
        String dnaSequence = "";
        long dnaLong = 0;
        long frequencyCount1 = 0;
        long frequencyCount2 = 0;
        long frequencyCount = 0;
        TreeObject ph = null;
        // Iterate through each line of the query file
        while (fileScanner.hasNextLine()) {
            dnaSequence = fileScanner.nextLine().trim();
            // Convert DNA sequence to a long value
            dnaLong = SequenceUtils.dnaStringToLong(dnaSequence);
            // Search for the DNA sequence and its complement in the B-tree
            ph = bTree.search(dnaLong);
            if(ph == null)
            {
                frequencyCount1 = 0;
            }
            else
            {
                frequencyCount1 = bTree.search(dnaLong).getFrequency();
            }
            ph = bTree.search(SequenceUtils.getComplement(dnaLong, sequenceLength));
            if(ph == null)
            {
                frequencyCount2 = 0;
            }
            else
            {
                frequencyCount2  = bTree.search(SequenceUtils.getComplement(dnaLong, sequenceLength)).getFrequency();
            }
            // Compute the total frequency count
            frequencyCount = frequencyCount1 + frequencyCount2;
            // Write the result to the output file or increment the sequence count
            if (debugLevel == 0) {
                writer.write(dnaSequence.toLowerCase() + " " + frequencyCount + "\n");
                System.out.println(dnaSequence.toLowerCase() + " " + frequencyCount);
            } else {
                sequenceCount++;
            }
        }
        // Close the file scanner
        writer.close();
        fileScanner.close();
    }

    /**
     * Parses command line arguments and handles exceptions.
     * @param args Command line arguments.
     * @return Parsed command line arguments.
     */
    private static GeneBankSearchBTreeArguments parseArgumentsAndHandleExceptions(String[] args) {
        GeneBankSearchBTreeArguments geneBankSearchBTreeArguments = null;
        try {
            geneBankSearchBTreeArguments = parseArguments(args);
        } catch (ParseArgumentException e) {
            printUsageAndExit(e.getMessage());
        }
        return geneBankSearchBTreeArguments;
    }

    /**
     * Prints usage information and exits the program.
     * @param errorMessage Error message to display.
     */
    private static void printUsageAndExit(String errorMessage) {
        System.out.println(errorMessage);
        System.out.println("Usage: GeneBankSearchBTree --cache=<0|1> --degree=<btree-degree> --btreefile=<b-tree-file> --length=<sequence-length> --queryfile=<query-file> [--cachesize=<n>] [--debug=0|1]");
        System.exit(1);
    }

    /**
     * Parses command line arguments.
     * @param args Command line arguments.
     * @return Parsed command line arguments.
     * @throws ParseArgumentException Throws an exception if an error occurs while parsing arguments.
     */
    public static GeneBankSearchBTreeArguments parseArguments(String[] args) throws ParseArgumentException {
        boolean useCache = false;
        int degree = 0;
        String BTreeFileName = null;
        int subsequenceLength = 0;
        String queryFileName = null;
        int cacheSize = 0;
        int debugLevel = 0;

        // Parse each argument
        for (String arg : args) {
            if (arg.startsWith("--cache=")) {
                useCache = Integer.parseInt(arg.substring(8)) == 1;
            } else if (arg.startsWith("--degree=")) {
                degree = Integer.parseInt(arg.substring(9));
            } else if (arg.startsWith("--btreefile=")) {
                BTreeFileName = arg.substring(12);
            } else if (arg.startsWith("--length=")) {
                subsequenceLength = Integer.parseInt(arg.substring(9));
            } else if (arg.startsWith("--cachesize=")) {
                cacheSize = Integer.parseInt(arg.substring(12));
            } else if (arg.startsWith("--debug=")) {
                debugLevel = Integer.parseInt(arg.substring(8));
            } else if (arg.startsWith("--queryfile=")) {
                queryFileName = arg.substring(12);
            } else {
                throw new ParseArgumentException("Invalid Argument: " + arg);
            }
        }

        // Validate required arguments
        if (BTreeFileName == null || subsequenceLength == 0 || queryFileName == null) {
            throw new ParseArgumentException("Missing required arguments: btreefile, length, or queryfile");
        }

        // Validate optional arguments
        if (useCache && cacheSize < 100) {
            throw new ParseArgumentException("Cache size must be specified when using cache. Cache size should be >=100");
        }

        return new GeneBankSearchBTreeArguments(useCache, degree, BTreeFileName, subsequenceLength, queryFileName, cacheSize, debugLevel);
    }
}