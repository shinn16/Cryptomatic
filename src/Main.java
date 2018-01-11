import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Scanner;

public class Main {
    public static void main(String[] args){
        // initial variables
        boolean done = false;
        Scanner scanner = new Scanner(System.in);
        char[] data = null;
        String filename, key;
        int choice = 0;
        Crypto crypto = null;

        try{
            crypto = new Crypto("Dictionary.txt");
        }catch (FileNotFoundException e){
            System.out.println("Fatal error, dictionary not found.");
            System.exit(0);
        }

        // begin menu
        System.out.println("Welcome to the Cryptomatic.");
        while(!done){
            System.out.println("What would you like to do?.\n" +
                    "1. Enter the filename and key to encrypt.\n" +
                    "2. Enter the filename and key to decrypt.\n" +
                    "3. Enter the filename to brute force.\n" +
                    "4. Exit.");
            System.out.print(":> ");

            // begin user input
            try {  // in the event that a user puts in bad input, catch it and rerun the menu
                choice = Integer.parseInt(scanner.nextLine());
                switch (choice){
                    case 1:
                        crypto.encrypt(getFileCharacters(), getUserKey());
                        break;
                    case 2:
                        crypto.decrypt(getFileCharacters(), getUserKey());
                        break;
                    case 3:
                        break;
                    case 4:
                        done = true;
                        break;
                    default:
                        System.out.println("The option you have selected is invalid.");
                        break;
                }
            }catch (NumberFormatException e){
                System.out.println("\"" + choice + "\"" + " was not a valid selection.");
            }
            System.out.println(); // blank line between runs for easier menu reading
        }
        System.out.println("Goodbye.");

    }

    private static char[] getFileCharacters(){
        boolean validFile = true;
        Scanner scanner = new Scanner(System.in);
        String filename;
        char[] data = null;

        do{ // let the user input a file, if it is valid continue
            try{
                System.out.println("Please enter a file location.");
                System.out.print(":> ");
                filename = scanner.nextLine();
                data = loadFile(filename);
                validFile = true;
            }catch (IOException e){
                validFile = false;
                System.out.println("Please enter a valid file location.\n");
            }
        }while (!validFile); // if the file is not valid, go until the file is valid.
        return data;
    }

    private static char[] getUserKey(){
        Scanner scanner = new Scanner(System.in);
        boolean validKey = false;
        String key = null;

        while(!validKey){
            System.out.println("Please enter a two character encryption key.");
            System.out.print(":> ");
            key = scanner.nextLine();

            if (key.length() == 2) validKey = true;
            else System.out.println("Please enter a valid key of length 2.\n");
        }
        return key.toCharArray();
    }

    private static char[] loadFile(String fileName) throws IOException{

        System.out.print("Loading file...");

        InputStream in = new FileInputStream(new File(fileName));
        Reader reader = new InputStreamReader(in, "UTF-8");

        // buffer for efficiency
        Reader buffer = new BufferedReader(reader);

        int x, pos = 0;
        char[] characters = new char[0];
        while ((x = buffer.read()) != -1) {
            char ch = (char) x;
            characters = Arrays.copyOf(characters, characters.length + 1);
            characters[pos++] = ch;
        }
        System.out.println("Done!");
        return characters;
    }
}

class Crypto{
    private HashSet<String> dictionary = new HashSet<>();

    Crypto(String dictionaryFile) throws FileNotFoundException{
        Scanner scanner = new Scanner(new File(dictionaryFile));
        while (scanner.hasNextLine()){
            String line = scanner.nextLine(); // gets the line
            String[] words = line.split(" "); // turns the line into words
            Collections.addAll(dictionary, words); // adds the words to the dictionary
        }
    }

    String encrypt(char[] filename, char[] key){
        String encrypted = "";
        return encrypted;
    }

    String decrypt(char[] filename, char[] key){
        String decrypted = "";
        return decrypted;
    }

    void bruteForce(String filename){

    }
}