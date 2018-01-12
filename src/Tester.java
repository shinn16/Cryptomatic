import java.io.File;
import java.util.HashSet;
import java.util.Scanner;

/**
 * Tester class
 *
 * @author Patrick Shinn
 * @version 1/11/18
 */
public class Tester {

    public static void main(String[] args) throws Exception{
        HashSet<String> dictionary = new HashSet<>();
        Scanner scanner = new Scanner(new File("Dictionary.txt"));
        while (scanner.hasNextLine()) dictionary.add(scanner.nextLine()); // adds the words to the dictionary

        System.out.println(dictionary.contains("them"));
    }


}
