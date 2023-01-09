import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Scanner;

public class LogReader {
    public static void main(String[] args) {

        // categorizeJobTypes("extracted_log");
        // createJobTable("extracted_log");
        getMonthlyJobCreated("extracted_log");
    }

    public static void getMonthlyJobCreated(String filename) {

        int[] monthlyCount = new int[7];
        String[] monthStrings = {"June", "July", "August", "September", "October", "November", "December"};

        try {
            FileInputStream fis = new FileInputStream(filename);
            Scanner logSc = new Scanner(fis); 
            while(logSc.hasNextLine()) {
                String[] line = logSc.nextLine().split(" ");
                String date = line[0];
                int month = Integer.parseInt(date.split("-")[1]);
                String jobType = line[1];

                if(jobType.compareTo("sched:") == 0) {
                    monthlyCount[month-6] += 1;
                }
            }
            logSc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("+" + "-".repeat(35) + "+");
        System.out.printf("| %-10s | %-20s |\n", "Month", "No. of Jobs created");
        System.out.println("+" + "-".repeat(35) + "+");

        for(int i = 0; i < monthlyCount.length; i++) {
            System.out.printf("| %-10s | %-20s |\n", monthStrings[i], Integer.toString(monthlyCount[i]));
        }
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

    public static void createJobTable(String filepath) {
        try{
            BufferedReader get = new BufferedReader(new FileReader(filepath));
            
            System.out.println("+----------------------------------------------------------+");
            System.out.printf("%-2s%-12s%-20s%-12s%-13s%1s%n","|","Job ID","Node List","CPUs","Partition","|");
            System.out.println("+----------------------------------------------------------+");
            
            String[]seperate, grab;
            String line,id=null,node=null, cpu=null, partition=null;
            int numJobCreated=0;
            
            line = get.readLine();
            while(line!=null)
            {
                seperate = line.split(" ");
                if(seperate.length==7 && seperate[2].equals("Allocate"))
                {   
                         for(int i=3; i<seperate.length;i++)
                        {   
                             grab = seperate[i].split("=");
                             if(i==3)
                                id = grab[1];
                             else if(i==4)
                                node = grab[1];
                             else if(i==5)
                                cpu = grab[1];
                             else if(i==6)
                                partition = grab[1];
                        }
                        System.out.printf("%-2s%-12s","|",id);
                        System.out.printf("%-20s",node);
                        System.out.printf("%-12s",cpu);
                        System.out.printf("%-11s%3s%n",partition,"|");
                }
                  
                line = get.readLine();
            }
            System.out.println("+----------------------------------------------------------+");
            
            get.close();
        }catch(IOException i){
            System.out.println("Problem with input file");
        }
    }
}