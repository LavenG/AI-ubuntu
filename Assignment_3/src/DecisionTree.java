/**
 * Created by min on 11/1/15.
 */
import java.util.*;
import java.io.*;
import java.lang.*;

public class DecisionTree {

    public class Reviewer {
        int reviewerID;
        int cost;
        double probability_S;
        double probability_F;
    }
    public static int reviewerNumber;
    public static int maxValue;
    public static int utility_S;
    public static int utility_F;
    public static double originalPro_S;
    public static ArrayList<Integer> consultReviewer;
    public static ArrayList<Integer> remainReviewer;
    public static DecisionTree dt = new DecisionTree();
    public static ArrayList<Reviewer> reviewersList = new ArrayList<>();


    public static void publisherInfo (ArrayList<String> info) {
        reviewerNumber = Integer.valueOf(info.get(0));
        utility_S = Integer.valueOf(info.get(1));
        utility_F = Integer.valueOf(info.get(2));
        originalPro_S = Double.parseDouble(info.get(3));
    }

    public static void reviewerInfo (ArrayList<String> info, int i) {
        Reviewer tempReviewer = dt.new Reviewer();
        tempReviewer.reviewerID = i;
        tempReviewer.cost = Integer.valueOf(info.get(0));
        tempReviewer.probability_S = Double.parseDouble(info.get(1));
        tempReviewer.probability_F = Double.parseDouble(info.get(2));
        reviewersList.add(tempReviewer);
        remainReviewer.add(tempReviewer.reviewerID);
    }

    public static void parser (String inputFile) {
        try {
            int i = 1;
            BufferedReader bufferedReader = new BufferedReader(new FileReader(inputFile));
            String line = null;
            line = bufferedReader.readLine();
            ArrayList<String> tempSplit = new ArrayList<>(Arrays.asList(line.split("\\s+")));
            publisherInfo(tempSplit);
            while((line = bufferedReader.readLine()) != null) {
                ArrayList<String> temp = new ArrayList<>(Arrays.asList(line.split("\\s+")));
                reviewerInfo(temp, i);
                i++;
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

    public static int consultPayment (ArrayList<Integer> consultReviewer) {
        int payment = 0;
        if (!consultReviewer.isEmpty()) {
            for (int i: consultReviewer) {
                payment += reviewersList.get(i - 1).cost;
            }
        }
    }

    public static int publishValue (double prevY_S, ArrayList<Integer> consultReviewer) {
        double temp = (utility_S + consultPayment(consultReviewer)) * prevY_S + (consultPayment(consultReviewer) + utility_F) * (1 - pro_prev_S);
        return (int)Math.round(temp);

    }

    public static int rejValue (ArrayList<Integer> consultReviewer) {
        return consultPayment(consultReviewer);
    }

    public static double reviewerYes (double prev_S, Reviewer currentReviewer) {
        return prev_S * currentReviewer.probability_S + (1 - prev_S) * currentReviewer.probability_F;
    }

    public static double updatePrevYes_S (double prob_prev_S, Reviewer currentReviewer, double pro_Yes) {
        return prob_prev_S * currentReviewer.probability_S / pro_Yes;
    }

    public static int reviewYes_Value (double prob_prev_S, ArrayList<Integer> consultReviewer, Reviewer currentReviewer) {


        return (int)Math.round(temp);
    }

    public static Reviewer currentReviewer (ArrayList<Reviewer> reviewersList, int i) {
        Reviewer currentReviewer = dt.new Reviewer();
        currentReviewer.reviewerID = reviewersList.get(i).reviewerID;
        currentReviewer.cost = reviewersList.get(i).cost;
        currentReviewer.probability_S = reviewersList.get(i).probability_S;
        currentReviewer.probability_F = reviewersList.get(i).probability_F;
        consultReviewer.add(currentReviewer.reviewerID);
        remainReviewer.remove(currentReviewer.reviewerID);
        return  currentReviewer;
    }

    public static int expectedValue(double prob_prev_S) {
        String reviewerStatus = null;
        if (reviewerStatus.equals("Yes")) {
            maxValue = publishValue(prob_prev_S, consultReviewer);
        }
        for (int i = 0; i < reviewerNumber; i++) {
            currentReviewer(reviewersList, i);
            maxValue = Math.max(maxValue, );
        }
    }

    public static void main (String[] args) {
        String inputFile;
        inputFile = args[0];
        parser(inputFile);
    }
}
