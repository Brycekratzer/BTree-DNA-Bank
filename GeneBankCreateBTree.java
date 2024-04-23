package cs321.create;

import java.io.PrintWriter;
import cs321.btree.BTree;
import cs321.btree.TreeObject;
import cs321.common.ParseArgumentException;

/**
 * GeneBankCreateBTree class is responsible for creating a B-tree index from a DNA sequence file.
 */
public class GeneBankCreateBTree {

    /**
     * Main method to create a B-tree index from a DNA sequence file.
     * @param args Command line arguments.
     * @throws Exception Throws an exception if an error occurs during B-tree creation.
     */
    public static void main(String[] args) throws Exception {
        long startTime = System.nanoTime();

        // Parse command line arguments
        GeneBankCreateBTreeArguments commandArgs = parseArgumentsAndHandleExceptions(args);
        // Read DNA sequences from the input file
        GeneBankFileReader fileReader = new GeneBankFileReader(commandArgs.getGbkFileName(), commandArgs.getSubsequenceLength());

        // Create a B-tree
        BTree dnaBTree = new BTree(commandArgs.getDegree(), commandArgs.getGbkFileName() + ".btree.data." + commandArgs.getSubsequenceLength() + "." + commandArgs.getDegree());
        dnaBTree.setSubsequenceLength(commandArgs.getSubsequenceLength());

        // Set cache size if cache is enabled
        if (commandArgs.isUseCache()) {
            dnaBTree.setUseCacheAndCacheSize(commandArgs.isUseCache(), commandArgs.getCacheSize());
        }

        // Insert DNA sequences into the B-tree
        int sequenceCount = 0;
        for (int i = 0; i < fileReader.getSize(); i++) {
            TreeObject currentTreeObject = new TreeObject(fileReader.getNextSequence(), 1);
            dnaBTree.insert(currentTreeObject);
            sequenceCount++;
        }

        // Dump B-tree to file if debug level is 1
        if(commandArgs.getDebugLevel() == 1){
            try (PrintWriter writer = new PrintWriter(commandArgs.getGbkFileName() + ".dump." + commandArgs.getSubsequenceLength())) {
                dnaBTree.dumpToFile(writer);
            }
        }

        long endTime = System.nanoTime();
        long elapsedTimeInMillis = (endTime - startTime) / 1000000;

        // Output debug information
        if (commandArgs.getDebugLevel() == 0) {
            System.out.println(" \n\n" + commandArgs.getGbkFileName() + " Successfully Uploaded!\n\n");
            System.out.println("__________________________________________________\n");
            System.out.println("                Debug - Analysis                  ");
            System.out.println("__________________________________________________\n");
            System.out.println("Elapsed time: " + elapsedTimeInMillis + " milliseconds");
            System.out.println("Number of DNA sequences uploaded: " + sequenceCount);
            System.out.println("Sub Sequence Length: " + commandArgs.getSubsequenceLength());
            System.out.println("Degree Useded: " + dnaBTree.getDegree());
            System.out.println("Amount of Nodes: " + dnaBTree.getNumberOfNodes());
            System.out.println("Cache Used: " + commandArgs.isUseCache());
            System.out.println("Cache Size: " + commandArgs.getCacheSize());
            System.out.printf("Cache Hit Ratio: %.2f%%\n", (dnaBTree.getCacheHitRatio() * 100.0));
            System.out.println("__________________________________________________");
        }
    }

    /**
     * Parses command line arguments and handles exceptions.
     * @param args Command line arguments.
     * @return Parsed command line arguments.
     */
    private static GeneBankCreateBTreeArguments parseArgumentsAndHandleExceptions(String[] args) {
        GeneBankCreateBTreeArguments geneBankCreateBTreeArguments = null;
        try {
            geneBankCreateBTreeArguments = parseArguments(args);
        } catch (ParseArgumentException e) {
            printUsageAndExit(e.getMessage());
        }
        return geneBankCreateBTreeArguments;
    }

    /**
     * Prints usage information and exits the program.
     * @param errorMessage Error message to display.
     */
    private static void printUsageAndExit(String errorMessage) {
        System.out.println("--cache=<0|1>  --degree=<btree-degree> \n" + //
                "\t--gbkfile=<gbk-file> --length=<sequence-length> [--cachesize=<n>] [--debug=0|1]");
        System.exit(1);
    }

    /**
     * Parses command line arguments.
     * @param args Command line arguments.
     * @return Parsed command line arguments.
     * @throws ParseArgumentException Throws an exception if an error occurs while parsing arguments.
     */
    public static GeneBankCreateBTreeArguments parseArguments(String[] args) throws ParseArgumentException {
        boolean useCache = false;
        int degree = 0;
        String gbkFileName = null;
        int subsequenceLength = 0;
        int cacheSize = 0;
        int debugLevel = 0;
    
        for (String arg : args) {
            if (arg.startsWith("--cache=")) {
                useCache = Integer.parseInt(arg.substring(8)) == 1;
            } else if (arg.startsWith("--degree=")) {
                degree = Integer.parseInt(arg.substring(9));
            } else if (arg.startsWith("--gbkfile=")) {
                gbkFileName = arg.substring(10);
            } else if (arg.startsWith("--length=")) {
                subsequenceLength = Integer.parseInt(arg.substring(9));
            } else if (arg.startsWith("--cachesize=")) {
                cacheSize = Integer.parseInt(arg.substring(12));
            } else if (arg.startsWith("--debug=")) {
                debugLevel = Integer.parseInt(arg.substring(8));
            } else {
                throw new ParseArgumentException("Invalid Argument: " + arg);
            }
        }
        // Validate required arguments
        if (gbkFileName == null || subsequenceLength == 0) {
            throw new ParseArgumentException("Missing required arguments: gbkfile or length");
        }

        // Validate optional arguments
        if (useCache && cacheSize < 100 && cacheSize > 10000) {
            throw new ParseArgumentException("Cache size must be specified when using cache. Cache size should be 100 <= x <= 10000");
        }
    
        return new GeneBankCreateBTreeArguments(useCache, degree, gbkFileName, subsequenceLength, cacheSize, debugLevel);
    }
}
