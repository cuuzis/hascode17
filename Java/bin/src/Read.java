import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Read
{

    public static ArrayList<String> readFile(String fileName)
    {
        ArrayList<String> array=new ArrayList<String>();
        try
        {
            Scanner scan=new Scanner(new File(fileName));
            String line=null;
            while((line=scan.nextLine())!=null)
                array.add(line);
            scan.close();
        }
        catch(FileNotFoundException e)
        {
            System.out.println("The file "+fileName+" has not been found!");
        }
        catch(Exception e)
        {
            System.out.println("Exception occurred "+e);
        }
        System.out.println(fileName + ".in read");
        return array;
    }
}