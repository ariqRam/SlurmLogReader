import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class LogReader {
    public static void main(String[] args) {

        categorizeJobTypes("extracted_log");
    }

    public static void categorizeJobTypes(String filename) {
        String jobTypeFilepath = "./Outputs/job_types.txt";

        createFile(jobTypeFilepath);

        try {
            FileInputStream fis = new FileInputStream(filename);
            Scanner logSc = new Scanner(fis);

            
            outerloop:
            while(logSc.hasNextLine()) {
                String[] splittedLine = logSc.nextLine().split(" ");
                String newType =  splittedLine[1];
                FileInputStream jobTypeFile = new FileInputStream(jobTypeFilepath);
                Scanner jobTypeScanner = new Scanner(jobTypeFile);


                FileOutputStream fos = new FileOutputStream(jobTypeFilepath, true);
                PrintWriter printer = new PrintWriter(fos);

                if(!jobTypeScanner.hasNextLine()) {
                    printer.println(newType);
                                   
                    jobTypeScanner.close();
                    printer.close();
                    continue;
                }

                while(jobTypeScanner.hasNextLine()) {
                    String nextLine = jobTypeScanner.nextLine();
                    if(newType.compareTo(nextLine) == 0) {
                        // System.out.printf("%s == %s already exists in job_types.txt\n", newType, nextLine);
                        continue outerloop;
                    }
                }
                System.out.println("writing " + newType);
                printer.println(newType);               
                jobTypeScanner.close();
                printer.close();
            }
            logSc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void createFile(String filepath) {
        try {
            File myObj = new File(filepath);
            if (myObj.createNewFile()) {
              System.out.println("File created: " + myObj.getName());
            } else {
              System.out.println("File already exists.");
            }
          } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
          }
    }
}