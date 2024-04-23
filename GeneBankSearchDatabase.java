package cs321.search;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import cs321.common.ParseArgumentException;

/**
 * GeneBankSearchDatabase class performs sequence frequency search in a database based on provided query files.
 */
public class GeneBankSearchDatabase {
    
    /**
     * Main method of the GeneBankSearchDatabase program.
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        GeneBankSearchDatabaseArguments arguments = parseArgumentsAndHandleExceptions(args);

        try {
            Map<String, Integer> frequencyMap = calculateSequenceFrequencies("jdbc:sqlite:" + arguments.databasePath, arguments.queryFilePath);
            writeResultsToFile(frequencyMap);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Calculates the frequency of sequences from the query file in the database.
     * @param databaseUrl URL of the database.
     * @param queryFilePath Path to the query file.
     * @return A map containing sequence and its frequency.
     * @throws SQLException If an SQL exception occurs.
     * @throws IOException If an I/O exception occurs.
     */
    private static Map<String, Integer> calculateSequenceFrequencies(String databaseUrl, String queryFilePath)
        throws SQLException, IOException {
        Map<String, Integer> frequencyMap = new HashMap<>();

        try (Connection connection = DriverManager.getConnection(databaseUrl);
            BufferedReader reader = new BufferedReader(new FileReader(queryFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.toLowerCase();
                // Search the database with each line of the query file
                int totalFrequency = getSequenceFrequency(connection, line.trim()) + getSequenceFrequency(connection, getComplementarySequence(line.trim()));
                frequencyMap.put(line.trim(), totalFrequency);
            }
        }
        return frequencyMap;
    }

    /**
     * Retrieves the frequency of a sequence from the database.
     * @param connection Connection to the database.
     * @param sequence The sequence to search for.
     * @return The frequency of the sequence.
     * @throws SQLException If an SQL exception occurs.
     */
    static int getSequenceFrequency(Connection connection, String sequence) throws SQLException {
        String sql = "SELECT SUBSTR(line, ?) AS number FROM dataset WHERE line LIKE ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, sequence.length() + 2); // Offset to skip the sequence and space
            statement.setString(2, sequence + " %"); // Search pattern with wildcard
    
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String numberString = resultSet.getString("number");
                    if (numberString != null && !numberString.isEmpty()) {
                        // Convert the retrieved number string to an integer
                        return Integer.parseInt(numberString.trim());
                    }
                }
                return 0; // Sequence not found or number string is empty
            }
        }
    }

    /**
     * Generates the complementary sequence of a given DNA sequence.
     * @param sequence The DNA sequence.
     * @return The complementary sequence.
     */
    private static String getComplementarySequence(String sequence) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < sequence.length(); i++) {
            char c = sequence.charAt(i);
            switch (c) {
                case 'a':
                    sb.append('t');
                    break;
                case 'c':
                    sb.append('g');
                    break;
                case 'g':
                    sb.append('c');
                    break;
                case 't':
                    sb.append('a');
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * Writes the frequency results to a file.
     * @param frequencyMap A map containing sequence and its frequency.
     * @throws IOException If an I/O exception occurs.
     */
    private static void writeResultsToFile(Map<String, Integer> frequencyMap) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("results.txt"))) {
            for (Map.Entry<String, Integer> entry : frequencyMap.entrySet()) {
                writer.write(entry.getKey() + " " + entry.getValue());
                writer.newLine();
            }
        }
    }

    /**
     * Parses the command-line arguments and handles any exceptions.
     * @param args Command-line arguments.
     * @return Parsed arguments.
     */
    private static GeneBankSearchDatabaseArguments parseArgumentsAndHandleExceptions(String[] args) {
        GeneBankSearchDatabaseArguments geneBankSearchDatabaseArguments = null;
        try {
            geneBankSearchDatabaseArguments = parseArguments(args);
        } catch (ParseArgumentException e) {
            printUsageAndExit(e.getMessage());
        }
        return geneBankSearchDatabaseArguments;
    }

    /**
     * Prints the usage message and exits the program.
     * @param errorMessage The error message to display.
     */
    private static void printUsageAndExit(String errorMessage) {
        System.out.println("Usage: GeneBankSearchDatabase --database=<database-path> --queryfile=<query-file-path>");
        System.exit(1);
    }

    /**
     * Parses the command-line arguments.
     * @param args Command-line arguments.
     * @return Parsed arguments.
     * @throws ParseArgumentException If an invalid argument is encountered.
     */
    private static GeneBankSearchDatabaseArguments parseArguments(String[] args) throws ParseArgumentException {
        String databasePath = null;
        String queryFilePath = null;

        for (String arg : args) {
            if (arg.startsWith("--database=")) {
                databasePath = arg.substring(11);
            } else if (arg.startsWith("--queryfile=")) {
                queryFilePath = arg.substring(12);
            } else {
                throw new ParseArgumentException("Invalid Argument: " + arg);
            }
        }

        if (databasePath == null || queryFilePath == null) {
            throw new ParseArgumentException("Missing required arguments: database or queryfile");
        }

        return new GeneBankSearchDatabaseArguments(databasePath, queryFilePath);
    }

    /**
     * Represents the command-line arguments for GeneBankSearchDatabase program.
     */
    private static class GeneBankSearchDatabaseArguments {
        private final String databasePath;
        private final String queryFilePath;

        /**
         * Constructor for GeneBankSearchDatabaseArguments class.
         * @param databasePath Path to the database.
         * @param queryFilePath Path to the query file.
         */
        public GeneBankSearchDatabaseArguments(String databasePath, String queryFilePath) {
            this.databasePath = databasePath;
            this.queryFilePath = queryFilePath;
        }
    }
}