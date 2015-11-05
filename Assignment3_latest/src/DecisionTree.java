/**
 * Created by min on 11/1/15.
 */
import javax.swing.text.AsyncBoxView;
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
    public class Node {
        Node yesPath;
        Node noPath;
        Node choiceNode;
        String publish = "Null";
        String reject = "Null";
        int value;
        String ID;
    }



    public static int reviewerNumber;
    public static int maxValue;
    public static int utility_S;
    public static int utility_F;
    public static double originalPro_S;
    public static String firstStep;
    public static ArrayList<Integer> remainReviewer_cp = new ArrayList<>();
    public static DecisionTree dt = new DecisionTree();
    public static ArrayList<Integer> reviewerIDList = new ArrayList<>();
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
        remainReviewer_cp.add(i);
        reviewerIDList.add(i);
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
        return -payment;
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
        /*System.out.println(prev_S * currentReviewer.probability_S / reviewerYes(prev_S, currentReviewer));*/
        return prev_S * currentReviewer.probability_S / reviewerYes(prev_S, currentReviewer);
    }

    public static double updatePrevNo_S (double prev_S, Reviewer currentReviewer) {
        return prev_S * (1 - currentReviewer.probability_S) / (1 - reviewerYes(prev_S, currentReviewer));
    }

    public static Node reviewYes_Value (double prev_S, ArrayList<Reviewer> remainReviewer, Reviewer currentReviewer) {
        double temp;
        Node tempNode = dt. new Node();
        Node choice = dt.new Node();
        Node tempchoice;
        if (remainReviewer.isEmpty()) {
            temp = reviewerYes(prev_S, currentReviewer) * publishValue(updatePrevYes_S(prev_S, currentReviewer), reviewersList);
            choice.publish = "Publish";
            /*choice.value = (int)Math.round(temp);
            tempNode.choiceNode = choice;*/
        } else {
            int tempPub = publishValue(updatePrevYes_S(prev_S, currentReviewer), consultReviewers(remainReviewer));
            tempchoice = expectedValue(updatePrevYes_S(prev_S, currentReviewer), remainReviewer, tempPub);
            int tempEp = tempchoice.value;
            temp = reviewerYes(prev_S, currentReviewer) * tempEp;
            if (tempPub == tempEp) {
                choice.publish = "Publish";
                choice.value = tempPub;
            } else {
                choice = tempchoice;
                /*choice.ID = "Reviewer " + " " + String.valueOf(currentReviewer.reviewerID);*/
            }

        }
        tempNode.choiceNode = choice;
        tempNode.value = (int)Math.round(temp);
        return tempNode;
    }

    public static Node reviewNo_Value (double prev_S, ArrayList<Reviewer> remainReviewer, Reviewer currentReviewer) {
        double temp;
        Node tempNode = dt. new Node();
        Node choice = dt.new Node();
        if (remainReviewer.isEmpty()) {
            temp = (1 - reviewerYes(prev_S, currentReviewer)) * rejValue(reviewersList);
            choice.value = (int)Math.round(temp);
            choice.reject = "Reject";
            /*tempNode.choiceNode = choice;*/
        } else {
            int tempRej = rejValue(consultReviewers(remainReviewer));
            Node tempchoice = expectedValue(updatePrevNo_S(prev_S, currentReviewer), remainReviewer, tempRej);
            int tempEp = tempchoice.value;
            temp = (1 - reviewerYes(prev_S, currentReviewer)) * tempEp;
            if (tempRej == tempEp) {
                choice.reject = "Reject";
                choice.value = tempRej;
            } else {
                choice = tempchoice;
                /*choice.ID = "Reviewer " + " " + String.valueOf(currentReviewer.reviewerID);*/
            }
        }
        tempNode.choiceNode = choice;
        tempNode.value = (int)Math.round(temp);
        return tempNode;
    }

    public static Node review_Value (double prev_S, ArrayList<Reviewer> remainReviewer, Reviewer currentReviewer) {
        Node tempYesNode;
        Node tempNoNode;
        Node tempNode = dt. new Node();
        ArrayList<Reviewer> remainReviewerYes_cp = new ArrayList<>(remainReviewer);
        ArrayList<Reviewer> remainReviewerNo_cp = new ArrayList<>(remainReviewer);
        tempYesNode = reviewYes_Value(prev_S, remainReviewerYes_cp, currentReviewer);
        tempNoNode = reviewNo_Value(prev_S, remainReviewerNo_cp, currentReviewer);
        tempNode.value = tempNoNode.value + tempYesNode.value;
        tempNode.yesPath = tempYesNode;
        tempNode.noPath = tempNoNode;
        return tempNode;
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



    public static ArrayList<Reviewer> consultReviewers(ArrayList<Reviewer> remainReviewer) {
        ArrayList<Integer> tempRemainList = new ArrayList<>();
        ArrayList<Integer> tempConsultIDList = new ArrayList<>();
        ArrayList<Reviewer> tempConsultList = new ArrayList<>();
        for (Reviewer temp:remainReviewer) {
            tempRemainList.add(temp.reviewerID);
        }
        for (int temp:reviewerIDList) {
            if (!tempRemainList.contains(temp)) {
                tempConsultIDList.add(temp);
            }
        }
        for (int temp:tempConsultIDList) {
            for (Reviewer tempReviewer: reviewersList) {
                if (temp == tempReviewer.reviewerID) {
                    Reviewer tempR = dt.new Reviewer();
                    tempR.reviewerID = tempReviewer.reviewerID;
                    tempR.cost = tempReviewer.cost;
                    tempR.probability_F = tempReviewer.probability_F;
                    tempR.probability_S = tempReviewer.probability_S;
                    tempConsultList.add(tempR);
                }
            }
        }
        return tempConsultList;
    }

    public static Node expectedValue(double prev_S, ArrayList<Reviewer> remainReviewer, int pub_rejValue) {
        maxValue = pub_rejValue;
        Node tempNode = dt.new Node();
        Node tempChoice;
        Node choice = dt.new Node();
        for (int i = 0; i < remainReviewer.size(); i++) {
            Reviewer currentReviewer = dt.new Reviewer();
            currentReviewer.reviewerID = remainReviewer.get(i).reviewerID;
            currentReviewer.cost = remainReviewer.get(i).cost;
            currentReviewer.probability_S = remainReviewer.get(i).probability_S;
            currentReviewer.probability_F = remainReviewer.get(i).probability_F;
            ArrayList<Reviewer> remainReviewer_cp = new ArrayList<>(remainReviewer);
            ArrayList<Reviewer> newRemain = new ArrayList<>(upDateRemainReviewer(currentReviewer.reviewerID, remainReviewer_cp));
            tempChoice = review_Value(prev_S, newRemain, currentReviewer);
            maxValue = Math.max(maxValue, tempChoice.value);
            if (maxValue == pub_rejValue) {
                choice.publish = "Publish";
                choice.value = pub_rejValue;
                tempNode = choice;
            } else {
                choice = tempChoice;
                choice.ID = "Reviewer " + " " + String.valueOf(currentReviewer.reviewerID);
                tempNode = choice;
            }
            if (maxValue == pub_rejValue) {
                firstStep = "Publish";
            } else {
                firstStep = "Consult reviewer " + String.valueOf(currentReviewer.reviewerID + ": ");
            }

        }

        /*System.out.println("MaxValue: " + tempNode.value + "   " + tempNode.ID + " " + tempNode.publish + " " + tempNode.reject);*/
        return tempNode;
    }

    public static void printResult () {
        Node currentNode;
        String output = "Start";
        double temp = utility_S * originalPro_S + utility_F * (1 - originalPro_S);
        int startPubValue = (int)Math.round(temp);
        currentNode = expectedValue(originalPro_S, reviewersList, startPubValue);
        Scanner scan = new Scanner(System.in);
        String input;
        System.out.println("Expected value:" + " " + expectedValue(originalPro_S, reviewersList, startPubValue).value);
        System.out.print(firstStep);
        input = scan.next();
        while (!output.equals("Publish") && !output.equals("Reject")) {
            if (input.equals("yes")) {
                currentNode = currentNode.yesPath;
                if (currentNode.choiceNode.publish.equals("Publish")) {
                    output = "Publish";
                    System.out.println(output);
                } else if (currentNode.choiceNode.reject.equals("Reject")) {
                    output = "Reject";
                    System.out.println(output);
                } else {
                    input = scan.next();
                }
            }
            if (input.equals("no")) {
                currentNode = currentNode.noPath;
                if (currentNode.choiceNode.publish.equals("Publish")) {
                    output = "Publish";
                    System.out.println(output);
                } else if (currentNode.choiceNode.reject.equals("Reject")) {
                    output = "Reject";
                    System.out.println(output);
                } else {
                    currentNode = currentNode.choiceNode;
                    System.out.print(currentNode.ID + ": ");
                    input = scan.next();
                }
            }

        }
    }

    public static void main (String[] args) throws IOException{
        String inputFile;
        inputFile = args[0];
        parser(inputFile);
        /*double temp = utility_S * originalPro_S + utility_F * (1 - originalPro_S);
        int startPubValue = (int)Math.round(temp);
        System.out.println("Expected value:" + " " + expectedValue(originalPro_S, reviewersList, startPubValue).value);
        System.out.println(firstStep);*/
        printResult();
    }
}
