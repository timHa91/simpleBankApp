package bankapp;

import java.util.Scanner;

public abstract class UI{

    public static void printMainMenu() {
        System.out.println("1. Create an account");
        System.out.println("2. Log into account");
        System.out.println("0. Exit");
    }

    public static void printLoginMenu() {
        System.out.println("1. Balance");
        System.out.println("2. Add income");
        System.out.println("3. Do transfer");
        System.out.println("4. Close account");
        System.out.println("5. Log out");
        System.out.println("0. Exit");
    }

    public static void mainMenuGetChoice(SQLiteDB base) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        switch (input) {
            case "1": {createAnAccount(base); break;}
            case "2": {logIntoAccount(base);break;}
            case "0": {exit();break;}
            default: {System.out.println("Wrong input!"); break;}
        }
        scanner.close();
    }

    private static void createAnAccount(SQLiteDB base) {
        Card card = new Card(base);
        System.out.printf("Your card has been created\n" +
                        "Your card number:\n" +
                        "%s\n" +
                        "Your card PIN:\n" +
                        "%s\n\n"
                , card.getCardNumber(), card.getPIN());
        printMainMenu();
        mainMenuGetChoice(base);
    }

    private static void loginMenuGetChoice(SQLiteDB base, String number) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        switch (input) {
            case "1": {
                System.out.println("Balance: " + SQLiteDB.getBalanceAsInt(base.getBaseName(), number));
                printLoginMenu();
                loginMenuGetChoice(base, number);
                break;
            }
            case "2": {
                addIncome(scanner, base, number);
                printLoginMenu();
                loginMenuGetChoice(base, number);
                break;
            }
            case "3": {
                makeTransfer(scanner, base, number);
                printLoginMenu();
                loginMenuGetChoice(base, number);
                break;
            }
            case "4": {
                closeAccount(scanner, base, number);
                break;

            }
            case "5": {
                System.out.println("You have successfully logged out!");
                printMainMenu();
                mainMenuGetChoice(base);
                break;
            }
            case "0": {exit(); break;}
            default: {System.out.println("Wrong input!");break;}
        }
        scanner.close();
    }

    private static void addIncome(Scanner scanner, SQLiteDB base, String number) {
        System.out.println("Enter income: ");
        int income = scanner.nextInt();
        if(SQLiteDB.insertBalance(base.getBaseName(), income, number) == 1) {
            System.out.println("Income was added!");
        }
        else {
            System.out.println("Something went wrong!");
        }
    }

    private static void makeTransfer(Scanner scanner, SQLiteDB base, String number) {
        System.out.println("Transfer\n" +
                "Enter card number:");
        String targetCard = scanner.nextLine();
        // check if it is the same Account
        if(targetCard.equals(number)){
            System.out.println("You can't transfer money to the same account!");
        }
        // check with lua
        else if(!Card.checkLastDigit(targetCard)){
            System.out.println("Probably you made a mistake in the card number. Please try again!");
        }
        // check if Account exists in DB
        else if(SQLiteDB.checkExistence(base.getBaseName(), targetCard) == 0) {
            System.out.println("Such a card does not exist.");
        }

        else {
            System.out.println("How much money do you want to transfer:");
            int amountToTransfer = scanner.nextInt();
            if(SQLiteDB.transferSequence(base.getBaseName(), number, targetCard, amountToTransfer) == 1) {
                System.out.println("Transfer was successful!");
            }
        }
    }

    private static void closeAccount(Scanner scanner, SQLiteDB base, String number) {
        System.out.println("Enter your PIN to commit: ");
        String PIN = scanner.nextLine();
        if(SQLiteDB.deleteAccount(base.getBaseName(), number, PIN) == 1) {
            System.out.println("Your Account got closed!");
            printMainMenu();
            mainMenuGetChoice(base);
        }
        else {
            System.out.println("Something went wrong!");
            printLoginMenu();
            loginMenuGetChoice(base, number);
        }
    }

    private static void logIntoAccount(SQLiteDB base) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your card number:");
        String cardNumber = scanner.nextLine();
        System.out.println("Enter your PIN:");
        String PIN = scanner.nextLine();
        // Check existence in DB
        int indexDbEntry = SQLiteDB.checkExistence(base.getBaseName(), cardNumber, PIN);
        if(indexDbEntry > 0) {
               System.out.println("\nYou have successfully logged in!:");
               printLoginMenu();
               loginMenuGetChoice(base, cardNumber);
        }
        else {
            System.out.println("Wrong card number or PIN!:");
            printMainMenu();
            mainMenuGetChoice(base);
        }
        scanner.close();
    }

    private static void exit() {
        System.out.println("Bye!");
    }
}
