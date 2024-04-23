package cs321.search;

import cs321.common.ParseArgumentException;

/**
 * GeneBankSearchDatabaseArguments represents the command-line arguments for the GeneBankSearchDatabase program.
 */
public class GeneBankSearchDatabaseArguments {
    private final String dataBasePath;
    private final String queryFileName;

    /**
     * Constructor for GeneBankSearchDatabaseArguments class.
     * @param dataBasePath Path to the database.
     * @param queryFileName Path to the query file.
     */
    public GeneBankSearchDatabaseArguments(String dataBasePath, String queryFileName) {
        this.dataBasePath = dataBasePath;
        this.queryFileName = queryFileName;
    }
    
    /**
     * Overrides the equals method to compare two GeneBankSearchDatabaseArguments objects.
     * @param obj The object to compare with.
     * @return True if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        // Generated method
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        GeneBankSearchDatabaseArguments other = (GeneBankSearchDatabaseArguments) obj;
        if (dataBasePath == null) {
            if (other.dataBasePath != null) {
                return false;
            }
        } else {
            if (!dataBasePath.equals(other.dataBasePath)) {
                return false;
            }
        }
        if (queryFileName == null) {
            if (other.queryFileName != null) {
                return false;
            }
        } else {
            if (!queryFileName.equals(other.queryFileName)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Overrides the toString method to generate a string representation of GeneBankSearchDatabaseArguments object.
     * @return A string representation of the object.
     */
    @Override
    public String toString() {
        // Generated method
        return "GeneBankSearchDatabaseArguments{" +
                "dataBasePath='" + dataBasePath + '\'' +
                ", queryFileName='" + queryFileName + '\'' +
                '}';
    }

    /**
     * Gets the path to the database.
     * @return The path to the database.
     */
    public String getDataBasePath() {
        return dataBasePath;
    }

    /**
     * Gets the path to the query file.
     * @return The path to the query file.
     */
    public String getQueryFileName() {
        return queryFileName;
    }
}