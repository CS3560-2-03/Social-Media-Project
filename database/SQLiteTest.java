package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

//This class is purely for reference
//do not include in final build
public class SQLiteTest {
    public static void main(String[] args) {

        try {
            Class.forName("org.sqlite.JDBC");
        } catch(ClassNotFoundException e) {

            System.out.println("unable to get drivers: " + e.getMessage());
            System.out.println("make sure you've added slf4j-api, sl4j-simple, and sqlite-jdbc jars to project dependencies");
        }


        String url = "jdbc:sqlite:database/main.db";

        try {
            Connection conn = DriverManager.getConnection(url);
            String query = "SELECT * FROM account";

            Statement statement = conn.createStatement();

            ResultSet result = statement.executeQuery(query);

            while(result.next()) {
                int id = result.getInt("accountID");
                String name = result.getString("username");
                System.out.println("ID: " + id +  ", name: " + name);
            }



        } catch(SQLException e) {
            System.out.println("Error connecting to SQLite database:" + e.getMessage());
        }
    }

}
