import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteHandler {

    String url = "jdbc:sqlite:database/main.db";

    Connection conn;
    Statement statement;
    String query;
    ResultSet result;



    public void startConnection() {
        try {
            Class.forName("org.sqlite.JDBC");

            try {
                Connection conn = DriverManager.getConnection(url);
                statement = conn.createStatement();
            } catch(SQLException e) {
                System.out.println("Error connecting to SQLite database:" + e.getMessage());
            }


        } catch(ClassNotFoundException e) {

            System.out.println("unable to get drivers: " + e.getMessage());
            System.out.println("make sure you've added slf4j-api, sl4j-simple, and sqlite-jdbc jars to project dependencies");
        }



    }

    public void endConnection() {
        try {
            if(conn != null) {
                conn.close();
            }
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /*
    public Post getPost(int id) {
        Post post = null;

        int accountID;
        String title;
        String textContent;
        int votes;
        String timeStamp;
        try {
            String query = "SELECT * from post WHERE postID = " + id + " LIMIT 1;";

            result = statement.executeQuery(query);

            while(result.next()) {


                //post = new Post(result.getInt("postID"), )


                id = result.getInt("postID");
                accountID = result.getInt("accountID");
                title = result.getString("title");
                textContent = result.getString("textContent");
                votes = result.getInt("votes");
                timeStamp = result.getString("timeStamp");


            }
            //Should get Account i think (but check if we already loaded account??)
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
