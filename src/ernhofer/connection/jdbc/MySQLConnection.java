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
        DB_URL = "jdbc:mysql://debian/wahl";
        //STEP 2: Register JDBC driver
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void connect(){
        System.out.println("Connecting to database...");
        try {
            conn = DriverManager.getConnection(DB_URL,USER,PASS);
            System.out.println("Creating statement...");
            stmt = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet execute(String query) throws SQLException {
        return stmt.executeQuery(query);
    }

    public String print(ResultSet rs){
        ResultSetMetaData rsmd = null;
        try {
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
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
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
