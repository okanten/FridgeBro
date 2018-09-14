package kanten.solutions;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        NorgesGruppenAPI ngSpar = new NorgesGruppenAPI(1300);
        try {
            System.out.println(Float.valueOf(ngSpar.getPrice("2000301700003")));
            System.out.println(ngSpar.getTitle("2000301700003"));
            System.out.println(ngSpar.getImageURL("2000301700003"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println("\nOutput: \n" + callURL("https://spar.no/"));
    }

}
