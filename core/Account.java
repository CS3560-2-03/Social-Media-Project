package core;

import java.sql.*;
import java.util.*;

public class Account {
	private int accountId;
	private String username;
	private String displayName;

	public Account(int accountId, String username, String displayName) {
		this.accountId = accountId;
		this.username = username;
		this.displayName = displayName;
	}

	//Connects to databases needed for program to function
	//If unable to connect to database, return null and print error message in console
	public static Connection connectToDatabase() {
		Connection c = null;

		try {
			String url = "jdbc:sqlite:database/main.db";

			//Try to connect to our databases
			c = DriverManager.getConnection(url);

			System.out.println("Connection to database was successful.");

			return c;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return null;
		}
	}

	public void setDisplayName(String displayName){
		displayName = displayName;
	}

	public String getDisplayName(){
		return displayName;
	}

	public int getId(){
		return accountId;
	}
	
	public String getUsername() {
		return username;
	}
}
