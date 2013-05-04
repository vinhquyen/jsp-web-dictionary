/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author chiron
 */
public class User {

    private String name;

    User(String name) {
        this.name = name;
    }


    public static boolean authenticate(String user, String pass) throws Exception{
        User u = new User(user);
        try {
            MessageDigest algorithm = MessageDigest.getInstance("SHA-256");

            byte[] hashInput = algorithm.digest(pass.getBytes());
            byte[] hashReal = u.getHash();

            return MessageDigest.isEqual(hashInput, hashReal); //FIXME: http://codahale.com/a-lesson-in-timing-attacks/

        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private byte[] getHash() throws Exception {
        Connection co = null;
        PreparedStatement stQuery = null;
        ResultSet rs = null;

        try {
            co = DBManager.initConnection();

            stQuery = co.prepareStatement("SELECT hash FROM user WHERE username = ?");
            stQuery.setString(1, this.name);

            rs = stQuery.executeQuery();

            if (rs.next())
                return rs.getBytes(1);
            else
                return new byte[0]; // User NOT exist in the DB

        } catch (Exception ex) {
            //Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                DBManager.closeConnection(co, stQuery, null);
            } catch (SQLException ex) {
                Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return new byte[0];
    }
    /**
     * Checks if the User module is enabled (all the modify options are included)
     * @return  true    ,if the User module is enabled
     *          false   , otherwise
     */
    public static boolean moduleEnabled() {
        ResourceBundle rConf = ResourceBundle.getBundle("resources/config");
        String enabled = rConf.getString("userModule");
        return enabled.equalsIgnoreCase("enabled");
    }
}
