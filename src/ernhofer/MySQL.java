package ernhofer;//STEP 1. Import required packages
import java.sql.*;

/*
            Es muss SERIALIZABLE sein!!
 */

public class MySQL {
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://debian/wahl";

    //  Database credentials
    static final String USER = "wahl";
    static final String PASS = "wahl";

    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        try{
            //STEP 2: Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");

            //STEP 3: Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);

            //STEP 4: Execute a query
            System.out.println("Creating statement...");
            stmt = conn.createStatement();
            String sql;
            sql = "XA START 'x';";
            ResultSet rs = stmt.executeQuery(sql);
            stmt.executeQuery("XA PREPARE 'x'");

            //STEP 6: Clean-up environment
            rs.close();
            stmt.close();
            conn.close();
        }catch(SQLException se){
            //Handle errors for JDBC
            switch (se.getErrorCode()){
                case 1399:
                    System.out.println("!!!!!The command cannot be executed when global transaction is in the  ACTIVE state!!!!!");
                    break;
                default:
                    System.out.println(se.getErrorCode());
                    se.printStackTrace();
            }
        }catch(Exception e){
            //Handle errors for Class.forName
            e.printStackTrace();
        }finally{
            //finally block used to close resources
            try{
                if(stmt!=null)
                    stmt.close();
            }catch(SQLException se2){
            }// nothing we can do
            try{
                if(conn!=null)
                    conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }//end finally try
        }//end try
        System.out.println("Goodbye!");
    }//end main
}//end FirstExample