import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Main
{

    public static void main(String[] args)
    {
        doWork("example");
        //doWork("small");
        //doWork("medium");
        //doWork("big");
    }

    static void doWork(String filename) {
        ArrayList<String> fileLines = Read.readFile(filename + ".in");


        // Logic goes here


        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename + ".out"))) {

            //Result is saved here
            //bw.write("Test\n");

            System.out.println("Saved to " + filename + ".out");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}