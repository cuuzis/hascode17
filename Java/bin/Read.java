import java.io.*;

public class Read
{
    public Read() {}

    public ArrayList<String> readFile(String fileName)
    {
        ArrayList<String> array=new ArrayList<String>;
        Scanner scan;
        try
        {
            scan=new Scanner(new File(fileName));
            String line=null;
            while((line=scan.hasNext())!=null)
                array.add(line);
        }
        catch(FileNotFoundException e)
        {
            System.out.println("The file "+fileName+" has not been found!");
        }
        catch(Exception e)
        {
            System.out.println("Exception occurred "+e);
        }
        finally
        {
            scan.close();
        }
        return array;
    }
}