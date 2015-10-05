import java.util.ArrayList;
import java.io.*;

/**
 * Created by min on 10/5/15.
 */
public class DPLLSearch {
    public static  ArrayList<ArrayList<String>> clauseSet = new ArrayList<>();

    public static void readClauseSet (String inputFile) {
       try {
           BufferedReader bufferedReader = new BufferedReader(new FileReader(inputFile));
           String line = null;
           while((line = bufferedReader.readLine()) != null) {
               if (line.compareTo("0") != 0) {
                   System.out.println(line);
               } else {
                   System.out.println("irrelate " + line);
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
