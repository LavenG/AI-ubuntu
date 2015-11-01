/**
 * Created by min on 11/1/15.
 */
import java.util.*;
import java.io.*;


public class DecisionTree {

    public class Reviewer {
        int cost;
        double probability_S;
        double probability_F;
    }
    public static int reviewerNumber;
    public static int utility_S;
    public static int utility_F;
    public static double originalPro_S;
    public static DecisionTree dt = new DecisionTree();
    public static ArrayList<Reviewer> reviewersList = new ArrayList<>();


    public static void publisherInfo (ArrayList<String> info) {
        reviewerNumber = Integer.valueOf(info.get(0));
        utility_S = Integer.valueOf(info.get(1));
        utility_F = Integer.valueOf(info.get(2));
        originalPro_S = Double.parseDouble(info.get(3));
    }

    public static void reviewerInfo (ArrayList<String> info) {
        Reviewer tempReviewer = dt.new Reviewer();
        tempReviewer.cost = Integer.valueOf(info.get(0));
        tempReviewer.probability_S = Double.parseDouble(info.get(1));
        tempReviewer.probability_F = Double.parseDouble(info.get(2));
        reviewersList.add(tempReviewer);
    }

    public static void parser (String inputFile) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(inputFile));
            String line = null;
            line = bufferedReader.readLine();
            ArrayList<String> tempSplit = new ArrayList<>(Arrays.asList(line.split("\\s+")));
            publisherInfo(tempSplit);
            while((line = bufferedReader.readLine()) != null) {
                ArrayList<String> temp = new ArrayList<>(Arrays.asList(line.split("\\s+")));
                reviewerInfo(temp);
            }

            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" +
                            inputFile + "'");
        }
        catch(IOException ex) {
            System.out.println(
                    "Error reading file '"
                            + inputFile + "'");
        }

    }

    public static void main (String[] args) {
        String inputFile;
        inputFile = args[0];
        parser(inputFile);
    }
}
