package cs321.create;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class GeneBankFileReader implements GeneBankFileReaderInterface {
    private File gbkFile;
    private int currentPosition;
    private int subsequenceLength;
    private ArrayList<String> dnaSequences;

    public GeneBankFileReader(String dnaFile, int subsequenceLength) throws IOException {
        this.gbkFile = new File(dnaFile);
        this.currentPosition = 0;
        this.subsequenceLength = subsequenceLength;
        this.dnaSequences = new ArrayList<>();
        processSequences();
    }

    @Override
    public long getNextSequence() throws IOException {
        if (currentPosition >= dnaSequences.size()) {
            return 0;
        }
        String sequence = dnaSequences.get(currentPosition);
        currentPosition++;
        return SequenceUtils.dnaStringToLong(sequence);
    }

    public int getSize() {
        return dnaSequences.size();
    }

    private void processSequences() throws IOException {
            try (Scanner fileScanner = new Scanner(gbkFile)) {
                String currentLine;
                StringBuilder dnaBuilder = new StringBuilder();

                // Find the start of the DNA sequence
            while(fileScanner.hasNextLine()) {
                while (fileScanner.hasNextLine()) {
                    currentLine = fileScanner.nextLine().trim();
                    if (currentLine.contains("ORIGIN")) {
                        break;
                    }
                }

                // Process each line of the DNA sequence
                while (fileScanner.hasNextLine()) {
                    currentLine = fileScanner.nextLine().trim();

                    // Skip empty lines and the end marker "//"
                        if(currentLine.isEmpty()) {
                            continue;
                        }
                        if(currentLine.startsWith("//")) {
                            dnaBuilder.append(".");
                            break;
                        }

                    // Remove numbers from the line
                    currentLine = currentLine.replaceAll("\\d+", "");

                    // Append valid DNA characters to the StringBuilder
                    for (char currentChar : currentLine.toCharArray()) {
                        if(currentChar == 'n' || currentChar == 'N') {
                            dnaBuilder.append('.');
                        }
                        if (isValidDNACharacter(currentChar)) {
                            dnaBuilder.append(Character.toUpperCase(currentChar));
                        }
                    }
                
                }
            }

            // Extract subsequences of the specified length from the DNA sequence
            String dnaSequence = dnaBuilder.toString();
            for (int i = 0; i < dnaSequence.length() - subsequenceLength; i++) {
                String subsequence = dnaSequence.substring(i, i + subsequenceLength);
                if(subsequence.contains(".")) {
                    continue;
                }
                dnaSequences.add(subsequence);
            }
        } catch (FileNotFoundException e) {
            throw new IOException("File not found: " + gbkFile.getPath(), e);
        }
    }

    private boolean isValidDNACharacter(char c) {
        return c == 'A' || c == 'C' || c == 'G' || c == 'T' ||
                c == 'a' || c == 'c' || c == 'g' || c == 't';
    }
}