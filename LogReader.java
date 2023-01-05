import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class LogReader {
    public static void main(String[] args) {

        try {
            FileInputStream fis = new FileInputStream("extracted_log");
            Scanner sc = new Scanner(fis);
    
            while(sc.hasNextLine()) {
                System.out.println(sc.nextLine());
            }
            sc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}