public class Main
{
    public static void main(String[] args)
    {
        System.out.println("Instantiating the Read class");
        Read r=new Read();
        System.out.println("Read class instantiated\nReading file pl.pl");
        r.readFile("pl.pl");
        System.out.println("pl.pl file read");
    }
}