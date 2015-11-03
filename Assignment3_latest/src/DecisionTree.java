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
    public static ArrayList<Integer> remainReviewer_original;
    public static ArrayList<Integer> remainReviewer_cp;
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
        remainReviewer_original.add(tempReviewer.reviewerID);
        remainReviewer_cp.add(tempReviewer.reviewerID);
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
        double temp = (utility_S + consultPayment(consultReviewer)) * prev_S + (consultPayment(consultReviewer) + utility_F) * (1 - prev_S);
        return (int)Math.round(temp);

    }

    public static int rejValue (ArrayList<Reviewer> consultReviewer) {
        return consultPayment(consultReviewer);
    }

    public static double reviewerYes (double prev_S, Reviewer currentReviewer) {
        return prev_S * currentReviewer.probability_S + (1 - prev_S) * currentReviewer.probability_F;
    }

    public static double updatePrevYes_S (double prev_S, Reviewer currentReviewer) {
        return prev_S * currentReviewer.probability_S / reviewerYes(prev_S, currentReviewer);
    }

    public static double updatePrevNo_S (double prev_S, Reviewer currentReviewer) {
        return currentReviewer.probability_F * currentReviewer.probability_S / (1 - reviewerYes(prev_S, currentReviewer));
    }

    public static int reviewYes_Value (double prev_S, ArrayList<Integer> remainReviewer, Reviewer currentReviewer) {
        double temp;
        if (remainReviewer.isEmpty()) {
            temp = reviewerYes(prev_S, currentReviewer) * publishValue(updatePrevYes_S(prev_S, currentReviewer), consultReviewer(currentReviewer));
        } else {
            temp = reviewerYes(prev_S, currentReviewer) * expectedValue(updatePrevYes_S(prev_S, currentReviewer), remainReviewer, publishValue(prev_S, consultReviewer(currentReviewer)));
        }
        return (int)Math.round(temp);
    }

    public static int reviewNo_Value (double prev_S, ArrayList<Integer> remainReviewer, Reviewer currentReviewer) {
        double temp;
        if (remainReviewer.isEmpty()) {
            temp = rejValue(consultReviewer(currentReviewer));
        } else {
            temp = (1 - reviewerYes(prev_S, currentReviewer)) * expectedValue(updatePrevNo_S(prev_S, currentReviewer), remainReviewer, rejValue(consultReviewer(currentReviewer)));
        }
        return (int)Math.round(temp);
    }

    public static int review_Value (double prev_S, ArrayList<Integer> remainReviewer, Reviewer currentReviewer) {
        return reviewYes_Value(prev_S, remainReviewer, currentReviewer) + reviewNo_Value(prev_S, remainReviewer, currentReviewer);
    }

    public static ArrayList<Reviewer> consultReviewer (Reviewer currentReviewer) {
        ArrayList<Reviewer> temp = new ArrayList<>();
        temp.add(currentReviewer);
        return temp;
    }

    public static ArrayList<Integer> upDateRemainReviewer (int currentReviewer) {
        remainReviewer_cp.remove(currentReviewer);
        return remainReviewer_cp;
    }


    public static Reviewer currentReviewer (ArrayList<Reviewer> reviewersList, int i) {
        Reviewer currentReviewer = dt.new Reviewer();
        currentReviewer.reviewerID = reviewersList.get(i).reviewerID;
        currentReviewer.cost = reviewersList.get(i).cost;
        currentReviewer.probability_S = reviewersList.get(i).probability_S;
        currentReviewer.probability_F = reviewersList.get(i).probability_F;
        consultReviewer(currentReviewer);
        return  currentReviewer;
    }

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

    public static int expectedValue(double prev_S, ArrayList<Integer> remainReviewer, int pub_rejValue) {
            maxValue = pub_rejValue;
            for (int i = 0; i < remainReviewer.size(); i++) {
                currentReviewer(remainReviewerList(remainReviewer), i);
                int tempID = currentReviewer(remainReviewerList(remainReviewer), i).reviewerID;
                maxValue = Math.max(maxValue, review_Value(prev_S, upDateRemainReviewer(tempID), currentReviewer(remainReviewerList(remainReviewer), i)));
            }
        return maxValue;
    }

    public static void main (String[] args) {
        String inputFile;
        inputFile = args[0];
        parser(inputFile);
    }
}
