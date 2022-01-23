package bankapp;

public class Main {
    public static void main(String[] args) {
        SQLiteDB base = new SQLiteDB("card.s3db");
        UI.printMainMenu();
        UI.mainMenuGetChoice(base);

    }
}