import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args){
        // initial variables
        boolean done = false;
        int choice = 0;
        Wrapper wrapper = null;
        Crypto crypto = null;
        Scanner scanner = new Scanner(System.in);
        PrintWriter writer;


        try{
            crypto = new Crypto("Dictionary.txt", "Chars.txt");
        }catch (Exception e){
            System.out.println("Fatal error, dictionary not found or character set not found.");
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
                        wrapper = crypto.encrypt(getFileCharacters(), getUserKey());
                        writer = new PrintWriter(wrapper.getFileName());
                        writer.write(wrapper.getEncrypted());
                        writer.close();
                        break;
                    case 2:
                        wrapper = crypto.decrypt(getFileCharacters(), getUserKey());
                        writer = new PrintWriter(wrapper.getFileName());
                        writer.write(wrapper.getEncrypted());
                        writer.close();
                        break;
                    case 3:
                        wrapper = crypto.bruteForce(getFileCharacters());
                        if (wrapper != null){
                            System.out.println("Decrypted successfully!\n");
                            System.out.println(wrapper.getEncrypted());
                        }else System.out.println("Failed to decrypt the file.");
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
            }catch (IOException e){
                e.printStackTrace();
            }
            System.out.println(); // blank line between runs for easier menu reading
        }
        System.out.println("Goodbye.");

    }

    private static Wrapper getFileCharacters(){
        boolean validFile;
        Scanner scanner = new Scanner(System.in);
        String filename = null;
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
        return new Wrapper(filename, data);
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
    private HashSet<Character> characters = new HashSet<>();

    Crypto(String dictionaryFile, String characterFile) throws Exception{
        Scanner scanner = new Scanner(new File(dictionaryFile));
        while (scanner.hasNextLine()) dictionary.add(scanner.nextLine()); // adds the words to the dictionary

        scanner = new Scanner(new File(characterFile));
        while (scanner.hasNextLine()) characters.add(scanner.nextLine().charAt(0)); // adds chars to the character dictionary
        characters.add(' ');  // adding the space because it disappears from the text file.
    }

    Wrapper encrypt(Wrapper wrapper, char[] key) throws IOException{

        char[] characters = wrapper.getData();
        ArrayList<Character> converted = new ArrayList<>(); // storage for the converted characters
        PrintWriter writer;
        String writeOut;

        if (characters.length%2 != 0){ // if we have an odd number of characters, add one space.
            characters = Arrays.copyOf(characters, characters.length+1);
            characters[characters.length - 1] = ' ';
        }

        for (int i = 0; i < characters.length; i+=2) { // for every set of two characters, XOR with our key
            char one = characters[i];
            char two = characters[i + 1];
            converted.add((char) (one ^ key[0]));
            converted.add((char) (two ^ key[1]));
        }

        // converting into a string writes it to file.
        StringBuilder builder = new StringBuilder(converted.size());
        for(Character ch: converted) builder.append(ch);
        writeOut = builder.toString();
        return new Wrapper(wrapper.getFileName(), writeOut);
    }

    Wrapper decrypt(Wrapper wrapper, char[] key) throws IOException{
       return encrypt(wrapper, key);
    }

    Wrapper bruteForce(Wrapper wrapper) throws IOException{
        HashSet<String> file = new HashSet<>();
        Wrapper success = null;
        int pass = 0;
        boolean done = false;
        // for all possible combos, generate a key
        for(char x : characters){
            for (char y: characters) {
                Wrapper attempt = decrypt(wrapper, new char[]{x, y}); // attempt to decode using current key
                file.addAll(Arrays.asList(attempt.getEncrypted().split(" "))); // hash the decrypt attempt
                for (String element : file){
                    if (dictionary.contains(element.toLowerCase())){  // check the lower case of all elements
                        pass ++;
                    }
                    else break;
                }
                if (pass == file.size()){
                    done = true;
                    success = attempt;
                    break;
                }
                else pass = 0;
                file.clear();

            }
            if (done) break;
        }
        return success;
    }
}

class Wrapper{
    private String fileName, encrypted;
    private char[] data;

    Wrapper(String fileName, char[] data){
        this.fileName = fileName;
        this.data = data;
    }

    Wrapper(String fileName, String encrypted){
        this.fileName = fileName;
        this.encrypted = encrypted;
    }

    String getEncrypted() {
        return encrypted;
    }

    String getFileName() {
        return fileName;
    }

    char[] getData() {
        return data;
    }
}