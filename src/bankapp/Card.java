package bankapp;

import java.util.*;

public class Card {
    // first digit
    private final String MII = "4";
    // first six digits
    private final String BIN = this.MII + "00000";
    // whole Cardnumber
    private final String cardNumber;
    // PIN
    private final String PIN;
    private List<String> cardNumbersRepository = repositoryFiller();
    private static HashMap<String, String> issuedCards = new HashMap<>();

    public Card(SQLiteDB base) {
        // The format String %04d is for 0 filled 4 digits
        Random r = new Random();
        this.PIN = String.format("%04d", r.nextInt(10000));
        this.cardNumber = cardNumbersRepository.get(0);
        SQLiteDB.insert(base.getBaseName(), this.cardNumber, this.PIN);
        issuedCards.put(this.cardNumber, this.PIN);
        cardNumbersRepository.remove(0);

    }

    //Getter
    public String getCardNumber() {return this.cardNumber;}
    public String getPIN() {return this.PIN;}

    //Generate random Accounts
    private List <String> repositoryFiller() {
        List<String> repository = new ArrayList<>();
        int numberOfAccounts = 50;
        while (repository.size() < numberOfAccounts) {
            int accountNumber = (int) (Math.random() * 999999999);
            StringBuilder ai = new StringBuilder(Integer.toString(accountNumber));
            while (ai.length() < 9) {
                ai.insert(0, "0");
            }
            // check if already in DB
            repository.add(this.BIN + ai + generateCheckSum(String.valueOf(ai)));
        }
        Collections.shuffle(repository);
        return repository;
    }

    //Generate the last digit
    private int generateCheckSum(String accountNumber) {
        String accountNumberWithoutCheck = this.BIN + accountNumber;
        int sum = 0;
        for(int i = 0; i < accountNumberWithoutCheck.length(); i++) {
            int digit = Integer.parseInt(String.valueOf(accountNumberWithoutCheck.charAt(i)));
             if(i % 2 == 0) {
                 digit *= 2;
             }
             else {
                 digit *= 1;
             }
             if(digit > 9) {
                 digit -= 9;
             }
             sum += digit;

           }
        return (sum * 9) % 10;
    }

    public static boolean checkLastDigit(String cardNumber) {
        String lastCharacter = cardNumber.substring(cardNumber.length() - 1);
        cardNumber = cardNumber.substring(0, cardNumber.length()-1);
        int sum = 0;
        for(int i = 0; i < cardNumber.length(); i++) {
            int digit = Integer.parseInt(String.valueOf(cardNumber.charAt(i)));
            if(i % 2 == 0) {
                digit *= 2;
            }
            else {
                digit *= 1;
            }
            if(digit > 9) {
                digit -= 9;
            }
            sum += digit;

        }
        if(Integer.toString((sum * 9) % 10).equals(lastCharacter)) {
            return true;
        }
        return false;
    }
}
