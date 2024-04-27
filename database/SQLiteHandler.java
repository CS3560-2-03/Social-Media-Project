package database;

import core.Post;

import javax.swing.*;
import java.sql.*;
import java.util.List;

public class SQLiteHandler {

    String url = "jdbc:sqlite:database/main.db";

    Connection conn;
    Statement statement;
    String query;
    ResultSet result;



    public boolean startConnection() {
        //Try checking for needed sqlite drivers
        try {
            Class.forName("org.sqlite.JDBC");

            //If that didn't give an error, try to connect to file
            try {
                Connection conn = DriverManager.getConnection(url);
                statement = conn.createStatement();
                return true;
            } catch(SQLException e) {
                System.out.println("Error connecting to SQLite database:" + e.getMessage());
            }


        } catch(ClassNotFoundException e) {

            System.out.println("unable to get drivers: " + e.getMessage());
            System.out.println("make sure you've added slf4j-api, sl4j-simple, and sqlite-jdbc jars to project dependencies");
        }

        return false;

    }

    //endConnection should be called before exiting!
    public void endConnection() {
        try {
            if(conn != null) {
                conn.close();
            }
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public ResultSet getPosts(int limit) {
        try {
            String query = "SELECT * FROM post ORDER BY timeStamp DESC LIMIT " + limit + ";";

            result = statement.executeQuery(query);
            return result;
        } catch(Exception ex) {
            System.out.println("Failed to getPosts: " + ex.getMessage());
        }

        return null;
    }

    public Post createPost(ResultSet retrived) {
        try {
            int id = result.getInt("postID");
            int accountID = result.getInt("accountID");
            String title = result.getString("title");
            String textContent = result.getString("textContent");
            int votes = result.getInt("votes");
            String timeStamp = result.getString("timeStamp");
            Post newPost = new Post(id, accountID, title, textContent, votes, timeStamp);
            return newPost;
        } catch(Exception ex) {
            System.out.println("Unable to create post instance from ResultSet given: " + ex.getMessage());
        }

        return null;
    }

    public boolean validateAccount(String username, String password){


        //Used to execute SQLite commands
        PreparedStatement preparedStmt = null;
        String query = "INSERT INTO account(username, password) VALUES(?,?)";

        try {

            //"Prepare" the query
            preparedStmt = conn.prepareStatement(query);

            //***These setStrings are to set the arugments for INSERT INTO command***
            //Refers to first ? in the query
            preparedStmt.setString(1, username);
            //Refers to second ? in the query
            preparedStmt.setString(2, password);

            //Execute the actual query
            preparedStmt.executeUpdate();

            JOptionPane.showMessageDialog(null, "Account created!");
            return true;
            //...

            //If unable to create an account
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Account already exists.");

            //...
        }
        return false;
    }

    /*
    public core.Post getPost(int id) {
        core.Post post = null;

        int accountID;
        String title;
        String textContent;
        int votes;
        String timeStamp;
        try {
            String query = "SELECT * from post WHERE postID = " + id + " LIMIT 1;";

            result = statement.executeQuery(query);

            while(result.next()) {


                //post = new core.Post(result.getInt("postID"), )


                id = result.getInt("postID");
                accountID = result.getInt("accountID");
                title = result.getString("title");
                textContent = result.getString("textContent");
                votes = result.getInt("votes");
                timeStamp = result.getString("timeStamp");


            }
            //Should get core.Account i think (but check if we already loaded account??)
            //Wait how are we loading from database to runtime?

            statement = conn.createStatement();
            query = "SELECT * from account WHERE accountID = " + accountID + " LIMIT 1;";
            result = statement.executeQuery(query);



        } catch(Exception e) {
            System.out.println("Unable to get post " + id + ": " + e.getMessage());
        }
        return post;
    }
    */


}
