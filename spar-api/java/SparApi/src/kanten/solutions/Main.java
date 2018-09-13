package kanten.solutions;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        NorgesGruppenAPI ngSpar = new NorgesGruppenAPI(1);
        try {
            System.out.println(Float.valueOf(ngSpar.getPrice("7311041013649")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println("\nOutput: \n" + callURL("https://spar.no/"));
    }

}
