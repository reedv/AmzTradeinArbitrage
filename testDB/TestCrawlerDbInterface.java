/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author reedvillanueva
 * This class should hold the logic of all functions for any operations that the crawler 
 * may want to call that involve its connection to a database.
 */

public class TestCrawlerDbInterface {
    private static TestCrawlerDbInterface INSTANCE = null;
    private String dbName;
    public Connection conn = null;
    
    // protected to avoid instantiation from outside
    protected TestCrawlerDbInterface(String setDbName) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            
            this.dbName = setDbName;
            String url = "jdbc:mysql://localhost:3306/"+this.dbName;
            String user = "root";
            String password = "root";
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connection built");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            //no driver detected
            e.printStackTrace();
        }
    }

    // implements singleton behavior
    public static TestCrawlerDbInterface getInstance(String setDbName) {
       if(INSTANCE == null) {
          INSTANCE = new TestCrawlerDbInterface(setDbName);
       }
       return INSTANCE;
    }


    /**
     * 
     * @param sql
     * @return
     * @throws SQLException 
     */
    public ResultSet runSql_rs(String sql) throws SQLException {
        Statement sta = conn.createStatement();
        return sta.executeQuery(sql);
    }

    /**
     * 
     * @param sql
     * @return
     * @throws SQLException 
     */
    public boolean runSql_bool(String sql) throws SQLException {
        Statement sta = conn.createStatement();
        return sta.execute(sql);
    }
    
    /**
     * 
     * @param tableName
     * @return true if the given table has no valid row directly after the header row,
     * else returns true.
     * @throws SQLException 
     */
    public boolean tableIsEmpty(String tableName) throws SQLException {
        String sql = "SELECT * FROM "+tableName;
        ResultSet queryUrlSet = this.runSql_rs(sql);
        return !queryUrlSet.next();
    }
    
    /**
     * 
     * @param tableName
     * @param column
     * @param value
     * @return true is table with tableName contains an column with the given value.
     * @throws SQLException 
     */
    public boolean tableContainsColumnValue(String tableName, String column, String value) throws SQLException {
        String sql = "SELECT * FROM "+tableName+" WHERE "+column+" = \""+value+"\"";
        ResultSet queryUrlSet = this.runSql_rs(sql);
        return queryUrlSet.next();
    }
    
    /**
     * 
     * @param tableName
     * @return the total number of rows in the given table
     * @throws SQLException 
     */
    public int tableSize(String tableName) throws SQLException {
        String sql = "SELECT COUNT(*) AS count FROM "+tableName;
        ResultSet rs = this.runSql_rs(sql);
        rs.next();
        return rs.getInt("count");
    }
    
    /**
     * 
     * @param tableName
     * @param link
     * @throws SQLException 
     */
    public void storeUrlVisitedInTable(String tableName, String link) throws SQLException {
        String sql = "INSERT INTO "+tableName+" (`url`) "
                + "VALUES (\""+link+"\")";
        Statement stmt = this.conn.createStatement();
        stmt.executeUpdate(sql);
    }
    
    /**
     * 
     * @param tableName
     * @param rating
     * @param url
     * @throws SQLException 
     */
    public void storeRatingPageInTable(String tableName, double rating, String url) throws SQLException {
        String sql = "INSERT INTO "+tableName+" (`rating`, `url`) "
                + "VALUES (?, ?);";
        PreparedStatement stmt = this.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        stmt.setDouble(1, rating);
        stmt.setString(2, url);
        stmt.executeUpdate();
    }
    
    @Override
    protected void finalize() throws Throwable {
        if (conn != null || !conn.isClosed()) {
                conn.close();
        }
    }
}
