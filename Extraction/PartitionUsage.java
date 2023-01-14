//Q2 NUMBER OF JOBS BY PARTITION
package Extraction;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class PartitionUsage {

    public static void getJobsByPartition() 
    {
        try{
        Scanner ok = new Scanner(new FileInputStream("extracted_log"));
        
            
            String [] split,seperate,word;
            String get;             
            int []numP = new int[6];
            int b=6,digit,total=0;              //month
            String month="";
            String[]partition = {"gpu-v100s","cpu-opteron", "gpu-k10","gpu-titan","cpu-epyc","gpu-k40c"};
            
            while(ok.hasNextLine())
            {
                get = ok.nextLine();    //read line
                split = get.split(" "); 
                seperate = split[0].split("-");     //get the month in String
                digit=Integer.parseInt(seperate[1]);  //convert month to integer
                
                 if(split.length==7 && split[2].equals("Allocate"))
                 {  total++;
                     if(digit==b)
                     {
                         word = split[6].split("=");  //partition
                         
                        if(word[1].equals("gpu-v100s"))
                            numP[0] += 1;
                        else if(word[1].equals("cpu-opteron"))
                            numP[1]  += 1;
                        else if(word[1].equals("gpu-k10"))
                            numP[2] += 1;
                        else if(word[1].equals("gpu-titan"))
                            numP[3]  += 1;
                        else if(word[1].equals("cpu-epyc"))
                            numP[4]  += 1;
                        else if(word[1].equals("gpu-k40c"))
                            numP[5]  += 1;
                     }
                     if(b==12)
                         month = "December";
                     if(digit!=b)
                     {
                            switch(b)
                            {
                                case 6:
                                    month = "June";
                                    break;
                                case 7:
                                    month = "July";
                                    break;
                                case 8:
                                    month = "August";
                                    break;
                                case 9:
                                    month = "September";
                                    break;
                                case 10:
                                    month = "October";
                                    break;
                                case 11:
                                    month = "November";
                                    break;
                             }
                  
                        display(month);
                        for(int i=0; i<partition.length;i++)
                        {
                            System.out.printf("|%-20s%12s%13s%n",partition[i] ,numP[i],"|" );
                        }
                        print();
                        System.out.println("\n");
                        
                        
                         b++;
                         split = get.split(" ");
                         seperate = split[0].split("-");
                         digit=Integer.parseInt(seperate[1]);
                         
                         for(int i=0; i<numP.length;i++)
                              numP[i]=0;
                         
                         if(digit==b)
                         {
                             word = split[6].split("=");  
                         
                        if(word[1].equals("gpu-v100s"))
                            numP[0] += 1;
                        else if(word[1].equals("cpu-opteron"))
                            numP[1]  += 1;
                        else if(word[1].equals("gpu-k10"))
                            numP[2] += 1;
                        else if(word[1].equals("gpu-titan"))
                            numP[3]  += 1;
                        else if(word[1].equals("cpu-epyc"))
                            numP[4]  += 1;
                        else if(word[1].equals("gpu-k40c"))
                            numP[5]  += 1;
                        }
                
                     }
                 }
            }          
                        display(month);
                        for(int i=0; i<partition.length;i++)
                        {
                            System.out.printf("|%-20s%12s%13s%n",partition[i] ,numP[i],"|" );
                        }
                        print();
                        System.out.println();
                        total(total);
        ok.close();
        } 
        catch(IOException i)
         {   System.out.println("Problem with input file");}
         }
    
    public static void print()
    {
        System.out.println("+--------------------------------------------+");
    }
    
    public static void display(String month)
    {
        System.out.println("Number of Jobs by Partition in " + month + " 2022");
        print();
        System.out.printf("|%-25s%-19s|%n", "Partition", "Total Jobs");
        print();
        
    }
    
    public static void total(int total)
    {
        System.out.println("\nTotal number of Job Created from June to December is " + total + "\n\n");
    }
    public static void getNodeUsagePerPartition() 
    {
        try{
            BufferedReader get = new BufferedReader(new FileReader("extracted_log"));
           
            String line;                          //read text in line
            String[]seperate,node,partitions;     //seperate is use to split the line     //node is use to split the nodelist    //partitions is used to split the partition
            String[]partition = {"cpu-epyc","cpu-opteron","gpu-v100s", "gpu-k10","gpu-titan","gpu-k40c"};
            String[]nodeCPU = {"cpu01","cpu03","cpu04","cpu05","cpu07","cpu08","cpu09","cpu10","cpu11","cpu12","cpu13","cpu14","cpu15"};
            String[]nodeGPU = {"gpu01","gpu02","gpu03","gpu04","gpu05"};
            int [] calcCPU1,calcCPU2;                   
            int [] calcGPU1,calcGPU2,calcGPU3,calcGPU4;
            
            calcCPU1 = new int[nodeCPU.length];
            calcCPU2 = new int[nodeCPU.length];
            
            calcGPU1 = new int[nodeGPU.length];
            calcGPU2 = new int[nodeGPU.length];
            calcGPU3 = new int[nodeGPU.length];
            calcGPU4 = new int[nodeGPU.length];
            
            int i=0;
            line = get.readLine();
            
             while(line!=null)
            {
                seperate = line.split(" ");
                if(seperate.length==7 && seperate[2].equals("Allocate"))        //find the line that contain "Allocate" 
                {    
                    partitions = seperate[6].split("=");                        
                    node = seperate[4].split("=");
                   
                    if(partitions[1].equals(partition[i]))                      //check for partition cpu-epyc
                    {    for(int c=0; c<nodeCPU.length;c++)
                        {
                               if(node[1].equals(nodeCPU[c]))
                                    calcCPU1[c]++;
                            
                        }
                    }
                    i++;
                    if(partitions[1].equals(partition[i]))                      //check for partition cpu-opteron
                    {    for(int c=0; c<nodeCPU.length;c++)
                        {
                               if(node[1].equals(nodeCPU[c]))
                                    calcCPU2[c]++;
                            
                        }
                    }
                    i++;
                    if(partitions[1].equals(partition[i]))                      //check for partition gpu-v100s
                    {    for(int c=0; c<nodeGPU.length;c++)
                        {
                               if(node[1].equals(nodeGPU[c]))
                                    calcGPU1[c]++;
                            
                        }
                    }
                    i++;
                    if(partitions[1].equals(partition[i]))                      //check for partition gpu-k10
                    {    for(int c=0; c<nodeGPU.length;c++)
                        {
                               if(node[1].equals(nodeGPU[c]))
                                    calcGPU2[c]++;
                            
                        }
                    }
                    i++;
                    if(partitions[1].equals(partition[i]))                      //check for partition gpu-titan
                    {    for(int c=0; c<nodeGPU.length;c++)
                        {
                               if(node[1].equals(nodeGPU[c]))
                                    calcGPU3[c]++;
                            
                        }
                    }
                    i++;
                    if(partitions[1].equals(partition[i]))                      //check for partition gpu-k40c
                    {    for(int c=0; c<nodeGPU.length;c++)
                        {
                               if(node[1].equals(nodeGPU[c]))
                                    calcGPU4[c]++;
                            
                        }
                    }
                     i=0;       //set the i to zero to repeat the same process
                }
               
                line = get.readLine();
            }
            System.out.printf("%46s%n","NUMBER OF NODES USAGE PER PARTITION");
            System.out.println("+---------------------------------------------------------+");
            System.out.printf("|%13s%5s%12s%8s%10s%10s%n","Partition","|","Node","|","Total","|");
            System.out.println("+---------------------------------------------------------+");
            display(partition[0],nodeCPU,calcCPU1);     //call display method
            display(partition[1],nodeCPU,calcCPU2);
            display(partition[2],nodeGPU,calcGPU1);
            display(partition[3],nodeGPU,calcGPU2);
            display(partition[4],nodeGPU,calcGPU3);
            display(partition[5],nodeGPU,calcGPU4);
            get.close();
            
            }catch(IOException i)
            {System.out.println("Problem with input file");}
    }
    
    public static void display(String a, String[]b, int[]c)         //create a method that used to print the result 
    {
        for(int i=0; i<b.length; i++)
        {
            if(c[i]==0)
               continue;
            System.out.printf("|%13s%5s%12s%8s%10s%10s%n",a,"|",b[i] ,"|",c[i],"|");
        }
        System.out.println("+---------------------------------------------------------+");
    }
}
    
