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
    public static ArrayList<Integer> remainReviewer_original = new ArrayList<>();
    public static ArrayList<Integer> remainReviewer_cp = new ArrayList<>();
    public static DecisionTree dt = new DecisionTree();

    public static ArrayList<Reviewer> reviewersList = new ArrayList<>();
    public static ArrayList<Reviewer> consultReviewer = new ArrayList<>();


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
        remainReviewer_original.add(i);
        remainReviewer_cp.add(i);
    }

    public static void parser (String inputFile) {
        try {
            int i = 1;
            BufferedReader bufferedReader = new BufferedReader(new FileReader(inputFile));
            String line;
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

    public static int consultPayment (ArrayList<Reviewer> consultReviewer) {
        int payment = 0;
        if (!consultReviewer.isEmpty()) {
            for (Reviewer temp: consultReviewer) {
                payment += temp.cost;
            }
        }
        return payment;
    }

    public static int publishValue (double prev_S, ArrayList<Reviewer> consultReviewer) {
        System.out.println(consultPayment(consultReviewer));
        double temp = (utility_S - consultPayment(consultReviewer)) * prev_S - (consultPayment(consultReviewer) - utility_F) * (1 - prev_S);
        System.out.println((int)Math.round(temp));
        return (int)Math.round(temp);

    }

    public static int rejValue (ArrayList<Reviewer> consultReviewer) {
        return consultPayment(consultReviewer);
    }

    public static double reviewerYes (double prev_S, Reviewer currentReviewer) {
        System.out.println(prev_S * currentReviewer.probability_S + (1 - prev_S) * currentReviewer.probability_F);
        return prev_S * currentReviewer.probability_S + (1 - prev_S) * currentReviewer.probability_F;
    }

    public static double updatePrevYes_S (double prev_S, Reviewer currentReviewer) {
        System.out.println(prev_S * currentReviewer.probability_S / reviewerYes(prev_S, currentReviewer));
        return prev_S * currentReviewer.probability_S / reviewerYes(prev_S, currentReviewer);
    }

    public static double updatePrevNo_S (double prev_S, Reviewer currentReviewer) {
        System.out.println(currentReviewer.probability_F * currentReviewer.probability_S / (1 - reviewerYes(prev_S, currentReviewer)));
        return currentReviewer.probability_F * currentReviewer.probability_S / (1 - reviewerYes(prev_S, currentReviewer));
    }

    public static int reviewYes_Value (double prev_S, ArrayList<Reviewer> remainReviewer, Reviewer currentReviewer) {
        double temp;
        if (remainReviewer.isEmpty()) {
            temp = reviewerYes(prev_S, currentReviewer) * publishValue(updatePrevYes_S(prev_S, currentReviewer), consultReviewer);
            System.out.println(reviewerYes(prev_S, currentReviewer) + " " + updatePrevYes_S(prev_S, currentReviewer) + " " + publishValue(updatePrevYes_S(prev_S, currentReviewer), consultReviewer) );
        } else {
            temp = reviewerYes(prev_S, currentReviewer) * expectedValue(updatePrevYes_S(prev_S, currentReviewer), remainReviewer, publishValue(updatePrevYes_S(prev_S, currentReviewer), consultReviewer));
        }
        System.out.println((int)Math.round(temp));
        return (int)Math.round(temp);
    }

    public static int reviewNo_Value (double prev_S, ArrayList<Reviewer> remainReviewer, Reviewer currentReviewer) {
        double temp;
        if (remainReviewer.isEmpty()) {
            temp = (1 - reviewerYes(prev_S, currentReviewer)) * rejValue(consultReviewer);
        } else {
            temp = (1 - reviewerYes(prev_S, currentReviewer)) * expectedValue(updatePrevNo_S(prev_S, currentReviewer), remainReviewer, rejValue(consultReviewer));
        }
        System.out.println((int)Math.round(temp));
        return (int)Math.round(temp);
    }

    public static int review_Value (double prev_S, ArrayList<Reviewer> remainReviewer, Reviewer currentReviewer) {
        System.out.println(reviewYes_Value(prev_S, remainReviewer, currentReviewer) - reviewNo_Value(prev_S, remainReviewer, currentReviewer));
        return reviewYes_Value(prev_S, remainReviewer, currentReviewer) - reviewNo_Value(prev_S, remainReviewer, currentReviewer);
    }

    public static void consultReviewerFun (Reviewer currentReviewer) {
        consultReviewer.add(currentReviewer);
    }

    public static ArrayList<Reviewer> upDateRemainReviewer (int currentReviewer, ArrayList<Reviewer> prev_remain) {
        int temp = 0;
        for (int i = 0; i < prev_remain.size(); i++) {
            if (prev_remain.get(i).reviewerID == currentReviewer) {
                temp = i;
            }
        }
        prev_remain.remove(temp);
        return prev_remain;
    }


    /*public static Reviewer currentReviewerFun (ArrayList<Reviewer> reviewersList, int i) {
        Reviewer currentReviewer = dt. new Reviewer();
        currentReviewer.reviewerID = reviewersList.get(i).reviewerID;
        currentReviewer.cost = reviewersList.get(i).cost;
        currentReviewer.probability_S = reviewersList.get(i).probability_S;
        currentReviewer.probability_F = reviewersList.get(i).probability_F;
        consultReviewer.add(currentReviewer);
        return currentReviewer;
    }*/

    public static ArrayList<Reviewer> remainReviewerList (ArrayList<Integer> remainReviewer) {
        ArrayList<Reviewer> tempList = new ArrayList<>();
        for (int i:remainReviewer) {
            for (Reviewer temp: reviewersList) {
                if (temp.reviewerID == i) {
                   tempList.add(temp);
                }
            }
        }
        return tempList;
    }

    public static int expectedValue(double prev_S, ArrayList<Reviewer> remainReviewer, int pub_rejValue) {
        maxValue = pub_rejValue;
        for (int i = 0; i < remainReviewer.size(); i++) {
            Reviewer currentReviewer = dt. new Reviewer();
            currentReviewer.reviewerID = remainReviewer.get(i).reviewerID;
            currentReviewer.cost = remainReviewer.get(i).cost;
            currentReviewer.probability_S = remainReviewer.get(i).probability_S;
            currentReviewer.probability_F = remainReviewer.get(i).probability_F;
            consultReviewer.add(currentReviewer);
            maxValue = Math.max(maxValue, review_Value(prev_S, upDateRemainReviewer(currentReviewer.reviewerID, remainReviewer), currentReviewer));
        }
        return maxValue;
    }

    public static void main (String[] args) {
        String inputFile;
        inputFile = args[0];
        parser(inputFile);
        double temp = utility_S * originalPro_S + utility_F * (1 - originalPro_S);
        int startPubValue = (int)Math.round(temp);
        expectedValue(originalPro_S, reviewersList, startPubValue);
    }
}
