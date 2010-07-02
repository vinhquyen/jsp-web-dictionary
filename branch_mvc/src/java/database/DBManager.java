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
 * @author chiron

 *************************
 *  DATABASE OPERATIONS  *
 ************************/

public class DBManager {
    private static int iCacheSizeDB = -1;

    /**
     * Get the number of words contained in the DB
     * @return DB.word.size()
     * @throws java.sql.SQLException
     */
    public static int getSizeDB() throws SQLException {
        if(iCacheSizeDB != -1) {
            return iCacheSizeDB;
        }
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

        iCacheSizeDB = iSize;
        return iSize;
    }

    /**
     * Establish a connection with Database throw JDBC
     * @return established connection
     * @throws javax.naming.NamingException
     * @throws java.sql.SQLException
     */
    public static Connection initConnection() throws NamingException, SQLException {
        Context initCtx = new InitialContext();
        Context envCtx = (Context) initCtx.lookup("java:comp/env");
        DataSource ds = (DataSource) envCtx.lookup("jdbc/jspWebDict");

        return ds.getConnection();
    }

    /**
     * Try to close cleanly a DB connection releasing Statement and ResulSet resources.
     * @param co : connection to close
     * @param st : statement to close
     * @param rs : resultset to close
     * @throws java.sql.SQLException
     */
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
