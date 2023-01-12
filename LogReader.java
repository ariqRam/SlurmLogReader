import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import ErrorByUser.ErrorByUser;

public class LogReader {
    public static void main(String[] args) {

        // categorizeJobTypes("extracted_log");
        // createJobTable("extracted_log");
        // getMonthlyJobCreated("extracted_log"); // barchart-able
        // getJobTimeRange("extracted_log");
        ErrorByUser.getErrorByUser();
    }

    public static String parseTime(String date) {
        if(date.compareTo("-") == 0) {
            return date;
        }

        String[] months = {"June", "July", "August", "September", "October", "November", "December"};

        String cleanedDate = date.substring(1, date.length() - 1);
        String[] splittedDate = cleanedDate.split("T");
        String[] splittedDay = splittedDate[0].split("-");
        String month = months[Integer.parseInt(splittedDay[1])-6];
        String day = splittedDay[2];


        String time = splittedDate[1].substring(0,8);

        return String.format("%s - %s/%s", time, day, month);
    }

    public static void getJobTimeRange(String filename) {
        try{
            FileInputStream fis = new FileInputStream(filename);
            Scanner logSc = new Scanner(fis);

            FileOutputStream fos = new FileOutputStream("./Outputs/job_time_range.txt");
            PrintWriter output = new PrintWriter(fos);
            output.printf("%s | %-20s | %-20s | %10s\n", "JobID", "Start Date", "End Date", "Exit Status");
            output.println("-".repeat(65));
            while(logSc.hasNextLine()) {
                String[] splittedLine = logSc.nextLine().split(" ");
                String jobType = splittedLine[1];
                String date = splittedLine[0];
                if(jobType.compareTo("sched:") == 0 && splittedLine[2].compareTo("Allocate") == 0) {
                    int jobId = Integer.parseInt(splittedLine[3].split("=")[1]);
                    String[] wexitStatus = wexitstatusForJobId(jobId, filename);
                    String startDate = parseTime(date);
                    String endDate = parseTime(wexitStatus[0]);
                    String newLine = String.format("%d | %20s | %20s | %-10s\n", jobId, startDate, endDate, wexitStatus[1]);
                    output.print(newLine);
                }
            }
            output.close();
            logSc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String[] wexitstatusForJobId(int jobId, String filename) {
        // use this to help getJobTimeRange
        
        try{
            FileInputStream fis = new FileInputStream(filename);
            Scanner logSc = new Scanner(fis);
            while(logSc.hasNextLine()) {
                String line = logSc.nextLine();
                String[] splittedLine = line.split(" ");
                String jobType = splittedLine[1];
                if(jobType.compareTo("_job_complete:") == 0 && splittedLine[3].compareTo("WEXITSTATUS") == 0) {
                    int logJobId = Integer.parseInt(splittedLine[2].split("=")[1]);
                    if(logJobId == jobId) {
                        logSc.close();
                        return new String[]{splittedLine[0], "COMPLETED: " + splittedLine[4]};
                    }
                }
            }

            logSc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //if not found
        return new String[]{"-", "FAILED"};
    }

    public void getMonthlyJobCreated(String filename) {

        int[] monthlyCount = new int[7];
        String[] monthStrings = {"June", "July", "August", "September", "October", "November", "December"};

        try {
            FileInputStream fis = new FileInputStream(filename);
            Scanner logSc = new Scanner(fis); 
            while(logSc.hasNextLine()) {
                String[] splittedLine = logSc.nextLine().split(" ");
                String date = splittedLine[0];
                int month = Integer.parseInt(date.split("-")[1]);
                String jobType = splittedLine[1];

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

    public void categorizeJobTypes(String filename) {
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

    public void createJobTable(String filepath) {
        try{
            BufferedReader get = new BufferedReader(new FileReader(filepath));
            
            System.out.println("+----------------------------------------------------------+");
            System.out.printf("%-2s%-12s%-20s%-12s%-13s%1s%n","|","Job ID","Node List","CPUs","Partition","|");
            System.out.println("+----------------------------------------------------------+");
            
            String[]seperate, grab;
            String splittedLine,id=null,node=null, cpu=null, partition=null;
            
            splittedLine = get.readLine();
            while(splittedLine!=null)
            {
                seperate = splittedLine.split(" ");
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
                  
                splittedLine = get.readLine();
            }
            System.out.println("+----------------------------------------------------------+");
            
            get.close();
        }catch(IOException i){
            System.out.println("Problem with input file");
        }
    }
}