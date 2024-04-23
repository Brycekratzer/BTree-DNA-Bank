package cs321.search;

import cs321.common.ParseArgumentException;

/**
 * GeneBankSearchBTreeArguments class represents the command line arguments for the GeneBankSearchBTree program.
 */
public class GeneBankSearchBTreeArguments {
    private final boolean useCache;
    private final int degree;
    private final String BTreeFileName;
    private final int subsequenceLength;
    private final String queryFileName;
    private final int cacheSize;
    private final int debugLevel;

    /**
     * Constructor for GeneBankSearchBTreeArguments class.
     * @param useCache Whether to use cache or not.
     * @param degree Degree of the B-tree.
     * @param BTreeFileName Name of the B-tree file.
     * @param subsequenceLength Length of the DNA subsequence.
     * @param queryFileName Name of the query file.
     * @param cacheSize Size of the cache.
     * @param debugLevel Debugging level.
     */
    public GeneBankSearchBTreeArguments(boolean useCache, int degree, String BTreeFileName, int subsequenceLength, String queryFileName, int cacheSize, int debugLevel) {
        this.useCache = useCache;
        this.degree = degree;
        this.BTreeFileName = BTreeFileName;
        this.subsequenceLength = subsequenceLength;
        this.queryFileName = queryFileName;
        this.cacheSize = cacheSize;
        this.debugLevel = debugLevel;
    }
    
    /**
     * Overrides the equals method to compare two GeneBankSearchBTreeArguments objects.
     * @param obj The object to compare.
     * @return True if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        // This method was generated using an IDE
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        GeneBankSearchBTreeArguments other = (GeneBankSearchBTreeArguments) obj;
        if (cacheSize != other.cacheSize) {
            return false;
        }
        if (debugLevel != other.debugLevel) {
            return false;
        }
        if (degree != other.degree) {
            return false;
        }
        if (BTreeFileName == null) {
            if (other.BTreeFileName != null) {
                return false;
            }
        } else if (!BTreeFileName.equals(other.BTreeFileName)) {
            return false;
        }
        if (subsequenceLength != other.subsequenceLength) {
            return false;
        }
        if (queryFileName == null) {
            if (other.queryFileName != null) {
                return false;
            }
        } else if (!queryFileName.equals(other.queryFileName)) {
            return false;
        }
        return useCache == other.useCache;
    }

    /**
     * Overrides the toString method to provide a string representation of the GeneBankSearchBTreeArguments object.
     * @return A string representation of the object.
     */
    @Override
    public String toString() {
        // This method was generated using an IDE
        return "GeneBankCreateBTreeArguments{" +
                "useCache=" + useCache +
                ", degree=" + degree +
                ", BTreeFileName='" + BTreeFileName + '\'' +
                ", subsequenceLength=" + subsequenceLength +
                ", queryFileName='" + queryFileName + '\'' +
                ", cacheSize=" + cacheSize +
                ", debugLevel=" + debugLevel +
                '}';
    }

    /**
     * Gets the value of the useCache field.
     * @return The value of the useCache field.
     */
    public boolean isUseCache() {
        return useCache;
    }

    /**
     * Gets the value of the degree field.
     * @return The value of the degree field.
     */
    public int getDegree() {
        return degree;
    }

    /**
     * Gets the value of the BTreeFileName field.
     * @return The value of the BTreeFileName field.
     */
    public String getBTreeFileName() {
        return BTreeFileName;
    }

    /**
     * Gets the value of the subsequenceLength field.
     * @return The value of the subsequenceLength field.
     */
    public int getSubsequenceLength() {
        return subsequenceLength;
    }

    /**
     * Gets the value of the queryFileName field.
     * @return The value of the queryFileName field.
     */
    public String getQueryFileName() {
        return queryFileName;
    }

    /**
     * Gets the value of the cacheSize field.
     * @return The value of the cacheSize field.
     */
    public int getCacheSize() {
        return cacheSize;
    }

    /**
     * Gets the value of the debugLevel field.
     * @return The value of the debugLevel field.
     */
    public int getDebugLevel() {
        return debugLevel;
    }
}
