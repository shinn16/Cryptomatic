import java.util.Scanner;

public class Main {
    public static void main(String[] args){
        // initial variables
        boolean done = false;
        Crypto crypto = new Crypto();
        Scanner scanner = new Scanner(System.in);
        String filename, key;
        int choice = 0;

        // begin menu
        System.out.println("Welcome to the Cryptomatic.");
        while(!done){
            System.out.println("What would you like to do?\n" +
                    "1. Enter the filename and key to encrypt\n" +
                    "2. Enter the filename and key to decrypt\n" +
                    "3. Enter the filename to brute force\n" +
                    "4. Exit");
            System.out.print(":> ");

            // begin user input
            try {  // in the event that a user puts in bad input, catch it and rerun the menu
                choice = Integer.parseInt(scanner.nextLine());
                switch (choice){
                    case 1:
                        break;
                    case 2:
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
}

class Crypto{

    Crypto(){

    }

    void encrypt(){

    }

    void decrypt(){

    }

    void abbrakabra(){

    }
}