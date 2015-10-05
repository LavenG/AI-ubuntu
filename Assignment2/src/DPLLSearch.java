import java.lang.reflect.Array;
import java.util.*;
import java.io.*;

/**
 * Created by min on 10/5/15.
 */
public class DPLLSearch {
    public static  ArrayList<List<String>> clauseSet = new ArrayList<>();

    public static void readClauseSet (String inputFile) {
       try {
           BufferedReader bufferedReader = new BufferedReader(new FileReader(inputFile));
           String line = null;
           while((line = bufferedReader.readLine()) != null) {
               if (line.compareTo("0") != 0) {
                   List<String> tempSplit = Arrays.asList(line.split("\\s+"));
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


    public  static  void main (String[] args) {
        String inputFile = null;
        if (args.length > 0) {
            inputFile = args[0];
        }
        readClauseSet(inputFile);
    }
}
