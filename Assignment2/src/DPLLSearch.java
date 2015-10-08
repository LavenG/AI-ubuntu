import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;
import org.omg.CORBA.ARG_IN;
import sun.org.mozilla.javascript.ast.WhileLoop;

import java.lang.reflect.Array;
import java.util.*;
import java.io.*;

/**
 * Created by min on 10/5/15.
 */
public class DPLLSearch {
    public class Atom {
        int atomIndex;
        String atomValue;
    }
    public static ArrayList<ArrayList<String>> clauseSet = new ArrayList<>();
    public static ArrayList<Integer> atoms = new ArrayList<>();
    public static ArrayList<Integer> pureLiteral = new ArrayList<>();
    public static DPLLSearch dp = new DPLLSearch();
    public static Atom pickAtom = dp.new Atom();
    public static Stack<Atom> exploredAtom = new Stack<>();

    /*public static void splitAtoms (ArrayList<ArrayList<String>> newClauseSet) {
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
    }*/

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
                if((temp.contains(String.valueOf(-(atom.atomIndex)))) && (temp.size() == 1)) {
                    temp.clear();
                    temp.add("false");
                }
                if((temp.contains(String.valueOf(-(atom.atomIndex)))) && (temp.size() > 1)) {
                    temp.remove(String.valueOf(-(atom.atomIndex)));
                }
                if (!temp.isEmpty()) {
                    copyclauseSet.add(temp);
                }
            }
        }

        if (atom.atomValue.equals("false")) {
            for (int i = 0; i < newClauseSet.size(); i++) {
                ArrayList<String> temp = clauseSet.get(i);
                if(temp.contains(String.valueOf(-(atom.atomIndex)))) {
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

    public static boolean singleton (ArrayList<ArrayList<String>> newClauseSet) {
        for (ArrayList<String> temp : newClauseSet) {
            if (temp.size() == 1) {
                pickAtom.atomIndex = Integer.parseInt(temp.get(0));
                pickAtom.atomValue = "true";
                return true;
            }
        }
        return false;
    }

    public static boolean pureLiteral (ArrayList<ArrayList<String>> newClauseSet) {
        for (ArrayList<String> temp : newClauseSet) {
            if (temp.size() == 1) {
                pickAtom.atomIndex = Integer.parseInt(temp.get(0));
                pickAtom.atomValue = "true";
                return true;
            }
        }
        return false;
    }

    public static boolean chooseAtom (ArrayList<ArrayList<String>> newClauseSet) {
        if (!pureLiteral.isEmpty()) {
            pickAtom.atomIndex = pureLiteral.get(0);
            pickAtom.atomValue = "true";
            pureLiteral.remove(0);
            return true;
        } else if (newClauseSet.isEmpty()) {
            return false;
        } else {
            Collections.sort(atoms);
            pickAtom.atomIndex = atoms.get(0);
            atoms.remove(0);
            return true;
        }
    }




    public static boolean dpll (ArrayList<ArrayList<String>> newClauseSet) {
        ArrayList<ArrayList<String>> tempClause = new ArrayList<>();
        if (newClauseSet.isEmpty()) {
            return true;
        } else if (unsatisfiedSet(newClauseSet))  {
            DPLLSearch temp = new DPLLSearch();
            Atom tempAtom = temp.new Atom();
            while (tempAtom.atomValue.equals("true")) {
                tempAtom = exploredAtom.pop();
            }
            tempAtom.atomValue = "false";
            dpll(updateClauseSet(tempAtom, newClauseSet));
            exploredAtom.push(tempAtom);

        } else {
            if (singleton(newClauseSet)) {
                tempClause = updateClauseSet(pickAtom, newClauseSet);
                dpll(tempClause);
            } else if (pureLiteral(newClauseSet)) {
                tempClause = updateClauseSet(pickAtom, newClauseSet);
                exploredAtom.push(pickAtom);
                dpll(tempClause);
            } else if (chooseAtom(newClauseSet)){
                tempClause = updateClauseSet(pickAtom, newClauseSet);
                exploredAtom.push(pickAtom);
                dpll(tempClause);
            }
        }
        System.out.println("No Solution.");
        return false;
    }

    public  static  void main (String[] args) {
        String inputFile = null;
        if (args.length > 0) {
            inputFile = args[0];
        }
        readClauseSet(inputFile);
        dpll(clauseSet);
    }
}
