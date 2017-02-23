import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Main
{
    public static void main(String[] args)
    {
        // Small problem
        doWork("me_at_the_zoo");
        //doWork("Data/kittens");
        //doWork("trending_today");
        //doWork("videos_worth_spreading");
    }

    static void doWork(String filename)
    {
        int videos, endpoints, requests, cache_servers, capacity;
        ArrayList<String> fileLines = Read.readFile(filename + ".in");


        // Logic goes here


        String[] firstLine=fileLines.get(0).split(" ");
        videos=Integer.parseInt(firstLine[0]);
        endpoints=Integer.parseInt(firstLine[1]);
        requests=Integer.parseInt(firstLine[2]);
        cache_servers=Integer.parseInt(firstLine[3]);
        capacity=Integer.parseInt(firstLine[4]);
        System.out.println("# of videos: "+videos+"\n#of endpoints: "+endpoints+"\n#of requests: "+requests+"\n# of cache servers: "+cache_servers+"\ncapacity of each cache server: "+capacity);

 /*       try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename + ".out"))) {

            //Result is saved here
            bw.write("Test\n");

            System.out.println("Saved to " + filename + ".out");
        } catch (IOException e) {
            e.printStackTrace();
        }
   */ }
}