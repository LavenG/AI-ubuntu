import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;

import java.lang.reflect.Array;
import java.util.*;
import java.io.*;

/**
 * Created by min on 10/5/15.
 */
public class DPLLSearch {
    public static ArrayList<ArrayList<String>> clauseSet = new ArrayList<>();
    public static ArrayList<Integer> atoms = new ArrayList<>();
    public static ArrayList<Integer> pureLiteral = new ArrayList<>();
    public class Atom {
        int atomIndex;
        String atomValue;
    }

    public static void splitAtoms (ArrayList<ArrayList<String>> newClauseSet) {
        for (int i = 0; i < newClauseSet.size(); i++) {
            for (int j = 0; j < newClauseSet.get(i).size(); j++) {
                int temp = Integer.parseInt(newClauseSet.get(i).get(j));
                if (!atoms.contains(Math.abs(temp))) {
                    if (pureLiteral.contains(-temp)) {
                        atoms.add(Math.abs(temp));
                        pureLiteral.remove(new Integer(-temp));
                    } else if (!pureLiteral.contains(temp)) {
                        pureLiteral.add(temp);
                    }
                }
            }
        }
    }

    public static void readClauseSet (String inputFile) {
       try {
           BufferedReader bufferedReader = new BufferedReader(new FileReader(inputFile));
           String line = null;
           while((line = bufferedReader.readLine()) != null) {
               if (line.compareTo("0") != 0) {
                   ArrayList<String> tempSplit = new ArrayList<>(Arrays.asList(line.split("\\s+")));
                   clauseSet.add(tempSplit);
                   System.out.println("clauseSet " + clauseSet);
               } else {
                   break;
               }
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

    public static boolean unsatisfiedSet(ArrayList<ArrayList<String>> newClauseSet) {
        for (int i = 0; i < newClauseSet.size(); i++) {
            if ((newClauseSet.get(i).size() == 1) && (newClauseSet.get(i).get(0).equals("false"))) {
                return true;
            }
        }
        return false;
    }

    public static ArrayList<ArrayList<String>> updateClauseSet (Atom atom, ArrayList<ArrayList<String>> newClauseSet){
        ArrayList<ArrayList<String>> copyclauseSet = new ArrayList<>();
        if (atom.atomValue.equals("true")) {
            for (int i = 0; i < newClauseSet.size(); i++) {
                ArrayList<String> temp = clauseSet.get(i);
                if(temp.contains(String.valueOf(atom.atomIndex))) {
                    temp.clear();
                }
                if((temp.contains(String.valueOf(-atom.atomIndex))) && (temp.size() == 1)) {
                    temp.clear();
                    temp.add("false");
                }
                if((temp.contains(String.valueOf(-atom.atomIndex))) && (temp.size() > 1)) {
                    temp.remove(String.valueOf(-atom.atomIndex));
                }
                if (!temp.isEmpty()) {
                    copyclauseSet.add(temp);
                }
            }
        }

        if (atom.atomValue.equals("false")) {
            for (int i = 0; i < newClauseSet.size(); i++) {
                ArrayList<String> temp = clauseSet.get(i);
                if(temp.contains(String.valueOf(-atom.atomIndex))) {
                    temp.clear();
                }
                if((temp.contains(String.valueOf(atom.atomIndex))) && (temp.size() > 1)) {
                    temp.remove(String.valueOf(atom.atomIndex));
                }
                if((temp.contains(String.valueOf(atom.atomIndex))) && (temp.size() == 1)) {
                    temp.clear();
                    temp.add("false");
                }
                if (!temp.isEmpty()) {
                    copyclauseSet.add(temp);
                }
            }
        }
    return copyclauseSet;
    }

    /*public static String chooseAtom (ArrayList<ArrayList<String>> newClauseSet) {
       for (ArrayList<String> temp : newClauseSet) {
           if (temp.size() == 1) {
               return temp.get(0);
           }
       }
    }*/


    /*public static boolean dpll (ArrayList<ArrayList<String>> newClauseSet) {
        if (newClauseSet.isEmpty()) {
            return true;
        } else if (unsatisfiedSet(newClauseSet))  {
            return false;
        } else {

        }
    }*/

    public  static  void main (String[] args) {
        String inputFile = null;
        if (args.length > 0) {
            inputFile = args[0];
        }
        readClauseSet(inputFile);
        DPLLSearch tempDpll = new DPLLSearch();
        Atom test = tempDpll.new Atom();
        test.atomIndex = 3;
        test.atomValue = "true";
        splitAtoms(clauseSet);
        System.out.println(updateClauseSet(test, clauseSet));

        System.out.println("atoms " + atoms);
        System.out.println("pureLiteral" + pureLiteral);


    }
}
