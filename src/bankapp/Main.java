package bankapp;

public class Main {
    public static void main(String[] args) {
        SQLiteDB base = new SQLiteDB(args[1]);
        UI.printMainMenu();
        UI.mainMenuGetChoice(base);

    }
}