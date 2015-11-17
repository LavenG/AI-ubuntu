import java.io.*;
import java.util.*;

/**
 * Created by min on 11/15/15.
 */
public class Clustering {
    public class Document {
        String name;
        ArrayList<String> words;
    }
    public static ArrayList<String> stopWords = new ArrayList<>();
    public static ArrayList<Document> file;

    public static void readStopWords(String stopwordsFile) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(stopwordsFile));
            String line;
            line = bufferedReader.readLine();
            while((line = bufferedReader.readLine()) != null) {
                if (line.trim().length() == 0) {
                    continue;
                } else {
                    ArrayList<String> tempSplit = new ArrayList<>(Arrays.asList(line.split("\\s+")));
                    for (String temp: tempSplit) {
                        stopWords.add(temp);
                    }
                }
            }

            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" +
                            stopwordsFile + "'");
        }
        catch(IOException ex) {
            System.out.println(
                    "Error reading file '"
                            + stopwordsFile + "'");
        }

    }

    public static void main (String[] args) throws IOException {
        String stopwordsFile = args[0];
        readStopWords(stopwordsFile);
    }
}
