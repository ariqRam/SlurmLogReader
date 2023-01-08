import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

public class LogReader {
    public static void main(String[] args) {

        countTypeOfJob("extracted_log");
    }

    public static void countTypeOfJob(String filename) {
        try {
            FileInputStream fis = new FileInputStream(filename);
            Scanner logSc = new Scanner(fis);

            
            outerloop:
            while(logSc.hasNextLine()) {
                String[] splittedLine = logSc.nextLine().split(" ");
                String newType =  splittedLine[1];
                FileInputStream jobTypeFile = new FileInputStream("job_types.txt");
                Scanner jobTypeScanner = new Scanner(jobTypeFile);


                FileOutputStream fos = new FileOutputStream("job_types.txt", true);
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
}