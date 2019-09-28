package utils;

import java.util.Random;

/**
 * Created by HENRI on 21/04/2017.
 */

public class GenerateCode {

    private static String caracteres[] = {"A", "B",  "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N",
            "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};

    public static String getNextCode(){
        Random random = new Random();
        int tam = caracteres.length;
        String pos0 = caracteres[random.nextInt(tam)];
        String pos1 = caracteres[random.nextInt(tam)];
        String pos2 = caracteres[random.nextInt(tam)];
        String pos3 = caracteres[random.nextInt(tam)];

        String code = pos0 + pos1 + pos2 + pos3;
        return code;
    }
}
