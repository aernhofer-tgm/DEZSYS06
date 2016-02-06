package ernhofer.connection.jdbc;

import ernhofer.Station.*;

import java.sql.*;
import java.sql.Connection;

/**
 * Created by andie on 03.02.2016.
 */
public class MySQLConnection implements ernhofer.Station.Connection{

    //  Database credentials
    final String ADDRESS;
    final String DB;
    final String USER;
    final String PASS;
    final String DB_URL;
    Connection conn;
    Statement stmt;

    public MySQLConnection(){
        ADDRESS = "debian";
        DB = "wahl";
        USER = "wahl";
        PASS = "wahl";
        DB_URL = "jdbc:mysql://"+ADDRESS+"/"+DB;
        //STEP 2: Register JDBC driver
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public MySQLConnection(String address){
        ADDRESS = address;
        DB = "wahl";
        USER = "wahl";
        PASS = "wahl";
        DB_URL = "jdbc:mysql://"+ADDRESS+"/"+DB;
        //STEP 2: Register JDBC driver
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void connect(){
        System.out.println("Connecting to database..."+DB_URL);
        try {
            conn = DriverManager.getConnection(DB_URL,USER,PASS);
            System.out.println("Creating statement...");
            stmt = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet execute(String query) throws SQLException {
        if(query.toLowerCase().contains("select")) {
            return stmt.executeQuery(query);
        }else{
            stmt.executeUpdate(query);
            return null;
        }
    }

    public String print(ResultSet rs){
        try {
            ResultSetMetaData rsmd = null;
            rsmd = rs.getMetaData();

            printColTypes(rs);
            System.out.println("");

            int numberOfColumns = rsmd.getColumnCount();

            for (int i = 1; i <= numberOfColumns; i++) {
                if (i > 1) System.out.print(",  ");
                String columnName = rsmd.getColumnName(i);
                System.out.print(columnName);
            }

            System.out.println("");

            while (rs.next()) {
                for (int i = 1; i <= numberOfColumns; i++) {
                    if (i > 1) System.out.print(",  ");
                    String columnValue = rs.getString(i);
                    System.out.print(columnValue);
                }
                System.out.println("");
            }
            System.out.print("\n");
            rs.close();
        } catch (SQLException e) {
            System.out.println("Keine Ausgabe Moeglich!!!!!!!!!!!!!!!!!!!!");
            //e.printStackTrace();
        }catch (java.lang.NullPointerException e){
            System.out.println("Nichts zum ausgeben da...");
        }
        return null;
    }

    public void printColTypes(ResultSet rs) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int columns = rsmd.getColumnCount();
        for (int i = 1; i <= columns; i++) {
            int jdbcType = rsmd.getColumnType(i);
            String name = rsmd.getColumnTypeName(i);
            System.out.print("Column " + i + " is JDBC type " + jdbcType);
            System.out.println(", which the DBMS calls " + name);
        }
    }

    public void close(){
        try {
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getAdress(){
        return ADDRESS;
    }
}
