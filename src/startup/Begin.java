package startup;

public class Begin {
    public static void main(String[] args) throws InterruptedException {

        StartUp startUp = new StartUp();
        startUp.run(Integer.parseInt(args[0]));

        System.out.println("Begin has ended.");
    }
}
