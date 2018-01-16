import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args){
        boolean done = false;
        int choice = 0;
        Wrapper wrapper;
        Crypto crypto = null;
        Scanner scanner = new Scanner(System.in);
        PrintWriter writer;
        try{
            crypto = new Crypto("DictionaryFiles/Dictionary.txt", "DictionaryFiles/Chars.txt");
        }catch (Exception e){
            System.out.println("Fatal error, dictionary or character set not found.");
            System.exit(0);
        }

        // begin menu
        System.out.println("Welcome to the Cryptomatic.");
        while(!done){
            System.out.println("What would you like to do?\n" +
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
                        wrapper = crypto.encrypt(getFileCharacters(), getUserKey());  // generate encrypted data
                        writer = new PrintWriter(wrapper.getFileName());
                        writer.write(wrapper.getEncrypted()); // write it out
                        writer.close();
                        break;
                    case 2:
                        wrapper = crypto.encrypt(getFileCharacters(), getUserKey());  // decrypt
                        System.out.println("File contents:\n" + wrapper.getEncrypted()); // print contents
                        writer = new PrintWriter(wrapper.getFileName());
                        writer.write(wrapper.getEncrypted());  //write out
                        writer.close();
                        break;
                    case 3:
                        wrapper = crypto.bruteForce(getFileCharacters());
                        if (wrapper != null){  // if we decrypted successfully
                            System.out.println("Decrypted successfully!\n");
                            System.out.println("Key is: " + Arrays.toString(wrapper.getData()) +  "\n Contents are:");
                            System.out.println(wrapper.getEncrypted());
                            writer = new PrintWriter(new File("bruteForce.txt"));
                            writer.write(wrapper.getEncrypted());
                            writer.close();
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

    /**
     * Asks user for a file path until a file path is given,
     * then returns all characters in said file.
     *
     * @return char array fo all characters in a given file
     */
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
            }catch (IOException e){ // if the user gives us a bad file
                validFile = false;
                System.out.println("Please enter a valid file location.\n");
            }
        }while (!validFile); // if the file is not valid, go until the file is valid.
        return new Wrapper(filename, data);
    }

    /**
     * Asks a user for a key until a valid key is given.
     *
     * @return two character user key
     */
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

    /**
     * Loads a file character by character.
     *
     * @param fileName String
     * @return char array of all characters in the file
     * @throws IOException if file not found
     */
    private static char[] loadFile(String fileName) throws IOException{

        System.out.print("Loading file...");

        InputStream in = new FileInputStream(new File(fileName));
        Reader reader = new InputStreamReader(in, "UTF-8");

        // buffer for efficiency
        Reader buffer = new BufferedReader(reader);

        int x, pos = 0;
        char[] characters = new char[0];
        while ((x = buffer.read()) != -1) {
            char character = (char) x;
            characters = Arrays.copyOf(characters, characters.length + 1);
            characters[pos++] = character;
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
        characters.add(' ');  // adding the space and other absent characters
        characters.add('\r');
        characters.add('\n');
    }

    /**
     * Used to both encrypt and decrypt a file with a known key.
     *
     * @param wrapper  Wrapper containing file name and data
     * @param key  char array user key
     * @return Wrapper containing encrypted or decrypted data string
     */
    Wrapper encrypt(Wrapper wrapper, char[] key){

        char[] chars = wrapper.getData();
        ArrayList<Character> converted = new ArrayList<>(); // storage for the converted characters
        String writeOut;

        if (chars.length%2 != 0){ // if we have an odd number of characters, add one space.
            chars = Arrays.copyOf(chars, chars.length+1);
            chars[chars.length - 1] = ' ';
        }

        for (int i = 0; i < chars.length; i+=2) { // for every set of two characters, XOR with our key
            char one = chars[i];
            char two = chars[i + 1];
            converted.add((char) (one ^ key[0]));
            converted.add((char) (two ^ key[1]));
        }

        // converting into a string writes it to file.
        StringBuilder builder = new StringBuilder(converted.size());
        for(Character ch: converted) builder.append(ch);
        writeOut = builder.toString();
        return new Wrapper(wrapper.getFileName(), writeOut);
    }

    /**
     * Used by the bruteForce method to decrypt files.
     *
     * What makes this different from the encrypt method is that
     * this method with check the file for correct characters as it
     * decrypts. In the event that a character is not a valid character,
     * decryption stops and a null Wrapper is returned.
     *
     * @param wrapper Wrapper containing file name and data
     * @param key  char array containing generated key
     * @return  Wrapper with decrypted data and decrypted key
     */
    private Wrapper decrypt(Wrapper wrapper, char[] key){
        char[] chars = wrapper.getData();
        ArrayList<Character> converted = new ArrayList<>(); // storage for the converted characters
        String writeOut;

        if (chars.length%2 != 0){ // if we have an odd number of characters, add one space.
            chars = Arrays.copyOf(chars, chars.length+1);
            chars[chars.length - 1] = ' ';
        }

        for (int i = 0; i < chars.length; i+=2) { // for every set of two characters, XOR with our key
            char one = (char)(chars[i] ^ key[0]);
            char two = (char)(chars[i + 1] ^ key[1]);

            if (!characters.contains(one) && !characters.contains(two)){ // if either character is invalid
                return null;
            }else{
                converted.add(one);
                converted.add(two);
            }

        }

        // converting into a string writes it to file.
        StringBuilder builder = new StringBuilder(converted.size());
        for(Character ch: converted) builder.append(ch);
        writeOut = builder.toString();
        return new Wrapper(wrapper.getFileName(), writeOut);
    }

    /**
     * This method generates all possible keys and check them
     * using the decrypt method. Using HashSets, we minimize the
     * time needed to iterate over the file and check validity
     * of the decryption.
     *
     * @param wrapper  Wrapper with data to decrypt
     * @return Wrapper that has decrypted key and data string
     */
    Wrapper bruteForce(Wrapper wrapper){
        HashSet<String> file = new HashSet<>();
        long startTime = System.currentTimeMillis(), endTime; // used to get decrypt time.
        // for all possible combos, generate a key
        for(char x : characters){
            for (char y: characters) {
                Wrapper attempt = decrypt(wrapper, new char[]{x, y}); // attempt to decode using current key
                if (attempt == null) break; // if we get a null return, it means we skipped out due to bad characters.

                // The decrypted string is stripped of all numeric characters and symbols, then made lower case, then lastly split into an array that can be checked with the dictionary hash
                file.addAll(Arrays.asList(attempt.getEncrypted().replaceAll("[?!,.:;#$\"'*-=+_()1234567890/@~`&^%]", "").toLowerCase().split("[ \n\r]")));

                // get the portion of text that is in the dictionary.
                double size = file.size(); // save current size
                file.retainAll(dictionary); // only keep words that were found in the dictionary
                size = (double) file.size()/ size;  // check for the percent of words that were in the dictionary

                // if 70 percent of the text is in the dictionary, accept it.
                if(size >= .7){
                    attempt.setData(new char[]{x,y});

                    // printing out time for decryption
                    endTime = System.currentTimeMillis();
                    long totalTime = endTime - startTime;
                    System.out.println("Decrypted in: " + totalTime + " milliseconds.");

                    return attempt;
                }
                file.clear(); // clear the set if for the next run.
            }

        }
        return null; // if we fail to decrypt, return null.
    }
}


class Wrapper{  // just a data wrapper to help with passing data between methods.
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

    void setData(char[] data) {
        this.data = data;
    }
}