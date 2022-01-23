package bankapp;

import java.sql.*;

public class SQLiteDB {

    private final String baseName;

    public SQLiteDB(String baseName) {
        this.baseName = baseName;
        createNewDatabase();
    }

    public String getBaseName() {
        return this.baseName;
    }

    private void createNewDatabase() {
        Connection conn = connect(this.baseName);
        if (conn != null) {
            try {
                Statement statement = conn.createStatement();
                String sql = "CREATE TABLE IF NOT EXISTS card(\n" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                        "number TEXT,\n" +
                        "pin TEXT,\n" +
                        "balance INTEGER DEFAULT 0\n" +
                        ");";
                statement.executeUpdate(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static Connection connect(String baseName) {
        // SQLite connection string
        String url = "jdbc:sqlite:" + baseName;
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public static void insert(String baseName, String number, String pin) {
        String sql = "INSERT INTO card(number,pin) VALUES(?,?);";

        try (Connection conn = connect(baseName);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, number);
            pstmt.setString(2, pin);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    // check for Login
    public static int checkExistence(String baseName, String number, String pin) {
        String sql = "UPDATE card SET number = ? WHERE number = ? AND pin = ?";

        try(Connection con = connect(baseName);
            PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, number);
            pstmt.setString(2, number);
            pstmt.setString(3, pin);
            return pstmt.executeUpdate();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }
    // check only for existence
    public static int checkExistence(String baseName, String number) {
        String sql = "UPDATE card SET number = ? WHERE number = ?";

        try(Connection con = connect(baseName);
            PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, number);
            pstmt.setString(2, number);

            return pstmt.executeUpdate();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }

    public static int insertBalance(String baseName, int balance, String number) {
        String sql = "UPDATE card SET balance = balance + ? WHERE number = ?";

        try (Connection conn = connect(baseName);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, balance);
            pstmt.setString(2, number);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return -1;
    }

    public static int withdraw(String baseName, int amount, String number) {

        String sql = "UPDATE card SET balance = balance - ? WHERE number = ?";

        try (Connection conn = connect(baseName);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, amount);
            pstmt.setString(2, number);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return -1;
    }

    public static int getBalanceAsInt(String baseName, String number) {
        String sql = "SELECT balance FROM card WHERE number = ?";

        try (Connection conn = connect(baseName);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, number);
            ResultSet balance = pstmt.executeQuery();
            return balance.getInt(1);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return -1;
    }

    public static int transferSequence(String baseName, String number, String targetCard, int amount) {
        //check if account is liquid
        if(getBalanceAsInt(baseName, number) < amount) {
            System.out.println("Not enough money!");
            return 0;
        }
        // withdraw from card
        else if (withdraw(baseName, amount, number) == 1) {
            insertBalance(baseName, amount, targetCard);
            return 1;
        }
        return 0;
    }

    public static int deleteAccount(String baseName, String number, String PIN) {
        String sql = "DELETE FROM card WHERE number = ? AND PIN";

        try(Connection con = connect(baseName);
            PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, number);
            pstmt.setString(2, PIN);
            return pstmt.executeUpdate();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }
}
