import java.lang.reflect.Array;
import java.util.*;
import java.io.*;

/**
 * Created by min on 10/5/15.
 */
public class DPLLSearch {
    public static  ArrayList<ArrayList<String>> clauseSet = new ArrayList<>();
    public static ArrayList<Atom> atoms = new ArrayList<>();
    public class Atom {
        int atomIndex;
        String atomValue;
    }

    public static void readClauseSet (String inputFile) {
        ArrayList<Integer> tempAtoms = new ArrayList<>();
       try {
           BufferedReader bufferedReader = new BufferedReader(new FileReader(inputFile));
           String line = null;
           while((line = bufferedReader.readLine()) != null) {
               if (line.compareTo("0") != 0) {
                   ArrayList<String> tempSplit = new ArrayList<String>(Arrays.asList(line.split("\\s+")));

                   for (int i = 0; i < tempSplit.size(); i++) {
                       int temp = Integer.parseInt(tempSplit.get(i));
                       if((!tempAtoms.contains(temp)) && (!tempAtoms.contains(Math.abs(temp)))) {
                            tempAtoms.add(i, temp);
                       }
                   }
                   clauseSet.add(tempSplit);
                   System.out.println("clauseSet " + clauseSet);
               } else {
                   break;
               }
           }

           bufferedReader.close();
           Collections.sort(tempAtoms);
           for (int i = 0; i < tempAtoms.size(); i++) {
               DPLLSearch tempDpll = new DPLLSearch();
               Atom tempatom = tempDpll.new Atom();
               tempatom.atomIndex = tempAtoms.get(i);
               tempatom.atomValue = "null";
               atoms.add(i, tempatom);
               System.out.println(atoms.get(i).atomIndex + " " + atoms.get(i).atomValue);
           }

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
            if ((newClauseSet.size() == 1) && (newClauseSet.get(0).get(0).equals("false"))) {
                return true;
            }
        }
        return false;
    }

    public static ArrayList<ArrayList<String>> updateClauseSet (Atom atom, ArrayList<ArrayList<String>> newClauseSet){
        ArrayList<ArrayList<String>> copyclauseSet = new ArrayList<>();
        ArrayList<String> falseState = new ArrayList<>();
        falseState.add("false");
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
    /*public static boolean dpll (ArrayList<ArrayList<String>> newClauseSet) {
        if (newClauseSet.isEmpty()) {
            return true;
        } else if (unsatisfiedSet(newClauseSet))  {
            return false;
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
        System.out.println(updateClauseSet(test, clauseSet));


    }
}
