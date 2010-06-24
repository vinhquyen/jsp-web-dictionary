/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package database;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 *
 * @author chiron
 */
public class DBManager {
    /*************************
     *  DATABASE OPERATIONS  *
     ************************/
    public static int getSizeDB() throws SQLException {
        int iSize = -1;
        Connection co = null;
        ResultSet rs = null;
        Statement st = null;

        try {
            co = initConnection();
            st = co.createStatement();
            rs = st.executeQuery("SELECT COUNT(id) FROM word");
            rs.first();
            iSize = rs.getInt("COUNT(id)");

        } catch (Exception ex) {
            Logger.getLogger(Entry.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            closeConnection(co, st, rs);
        }
        return iSize;
    }

    /** Establish a connection with Database throw JDBC*/
    public static Connection initConnection() throws NamingException, SQLException {
        Context initCtx = new InitialContext();
        Context envCtx = (Context) initCtx.lookup("java:comp/env");
        DataSource ds = (DataSource) envCtx.lookup("jdbc/jspWebDict");

        return ds.getConnection();
    }

    /** Try to close cleanly a DB connection releasing Statement and
     * ResulSet resources.      */
    public static void closeConnection(Connection co, Statement st, ResultSet rs) throws SQLException {
        try {
            if (rs != null)
                rs.close();
        } finally {
            try {
                if (st != null)
                    st.close();
            } finally {
                if (co != null)
                    co.close();
            }
        }
    }
}
