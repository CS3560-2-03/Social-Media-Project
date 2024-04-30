package core;

import database.SQLiteHandler;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AccountManager {
    private List<Account> accountList;
    private SQLiteHandler sqliteHandler;

    public AccountManager() {
        accountList = new ArrayList<Account>();
        sqliteHandler = new SQLiteHandler();
    }

    public Account fetchAccountById(int id) {
        //First check if account already was pulled
        for(Account a : accountList) {
            if(a.getId() == id) {
                return a;
            }
        }
        //We didn't find an account. Let's grab it from the database
        Account a = null;
        sqliteHandler.startConnection();
        ResultSet result = sqliteHandler.getAccountById(id);
        try {
            while(result.next()) {
                try {
                    int accountId = result.getInt("accountID");
                    String username = result.getString("username");
                    String displayName = result.getString("displayName");
                    a = new Account(accountId, username, displayName);
                } catch(Exception ex) {
                    System.out.println("Failed to parse result: " + ex.getMessage());
                }

            }
        } catch(Exception ex) {
            System.out.println("resultset was not valid: " + ex.getMessage());
        }

        sqliteHandler.endConnection();
        return a;
    }

}
