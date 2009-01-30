package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Random;

public class Entry {

    /** Process the examples introduced by the user 
     * TODO: define the examples format........
     */
    private static String[] processExamples(String exs) {
        return exs.split(";");
    }
    private int id;
    private String word;
    private String morfology;
    private String definition;
    private static String aMorf[] = {"adj.", "adv.", "interfj.", "f.", "m.", "prep.", "pron.", "v."};

    public Entry(int id, String w) {
        this.id = id;
        this.word = w;
    }

    public Entry(String w, String m, String def) {
        this.word = w;
        this.morfology = m;
        this.definition = def;
    }

    /** Add a new word to the database 
     * @return null: if everything go fine, in another situation returb a 
     * String which describes the problem
     */
    public static String addWord(String w,String m,String def)throws Exception {
        // TODO: test addWord
        Connection co = null;
        PreparedStatement stInsert = null;

        try {
            //TODO --> comprobaciones?? (A nivel de PRESENTACION)
            if (w == null || def == null)
                throw new Exception("You must insert the word and its def");

            List<String> aux = Arrays.asList(aMorf);
            if (!aux.contains(m))
                throw new Exception("The morfology must be: [" + aux.toString() + "]");

            // FIXME Eliminar comillas simples (') de los strings (sustituir por [\'] )
            def = def.replaceAll("'", "\'");
            /*------------ EOF VALIDATION -------------------*/

            co = initConnection();

            stInsert = co.prepareStatement("INSERT INTO word(term, morf, definition) VALUES(?,?,?)");
            stInsert.setString(1, w);
            stInsert.setString(1, m);
            stInsert.setString(1, def);

            int n = stInsert.executeUpdate();
            if(n<1) throw new SQLException("Entry.java:76, NOT Insert "+w);

        } catch (SQLException ex) {
            return ("ERROR: There are a problem inserting the word<br/>\n"
                        + ex.toString());
        } finally {
            closeConnection(co, stInsert, null);
        }
        return null;
    }

    public static String updateWord(String w, String m, String def) throws Exception {
        Connection co = null;
        ResultSet rs = null;
        PreparedStatement stInsert = null;
        int idW;
        
        // FIXME: use PreparedStatements
        stInsert = co.prepareStatement("SELECT id FROM word WHERE  term = ?");
        stInsert.setString(1, w);
        
        rs = stInsert.executeQuery();
        if(rs.next()) { //FIXME: getRow().getCol(1)?
            idW = rs.getInt("id");
        } else throw new Exception("UPDATE ERROR: the word "+w+"notExists");
        
        stInsert=co.prepareStatement("UPDATE entry SET term=?, morf=?, " +
                                        "definition = ? WHERE idWord = ?");
        stInsert.setString(1, w);
        stInsert.setString(2, m);
        stInsert.setString(3, def);
        stInsert.setInt(4, idW);
        stInsert.executeUpdate();
                 
        /**************************/
   
     
        try {
            //TODO --> comprobaciones?? (A nivel de PRESENTACION)
            if (w == null || def == null)
                throw new Exception("You must insert the word and its def");

            List<String> aux = Arrays.asList(aMorf);
            if (!aux.contains(m))
                throw new Exception("The morfology must be: [" + aux.toString() + "]");

            // FIXME Eliminar comillas simples (') de los strings (sustituir por [\'] )
            def = def.replaceAll("'", "\'");
            /*------------ EOF VALIDATION -------------------*/

            co = initConnection();

            stInsert = co.prepareStatement("INSERT INTO word(term, morf, definition) VALUES(?,?,?)");
            stInsert.setString(1, w);
            stInsert.setString(1, m);
            stInsert.setString(1, def);

            int n = stInsert.executeUpdate();
            if(n<1) throw new SQLException("Entry.java:76, NOT Insert "+w);

        } catch (SQLException ex) {
            return ("ERROR: There are a problem inserting the word<br/>\n"
                        + ex.toString());
        } finally {
            closeConnection(co, stInsert, rs);
        }
        return null;
    }

    public static Entry getDefinition(String szWord) throws Exception {
        Connection co = null;
        ResultSet rs = null;
        Statement st = null;

        /*  TODO --> control "stInsert" --> SQL Injection
         *[ as' OR true OR 'as' = 'as ] <-- muestra todas las palabras
         * szWord = szWord.replace("'", ""); (eliminar comillas simples?)
         */
        String szSQL = "SELECT id FROM word WHERE term = '" + szWord + "'";

        try {
            co = initConnection();
            st = co.createStatement();
            rs = st.executeQuery(szSQL);

            // Get the ID of the searched WORD
            if (rs.next())
                return getDefinition(rs.getInt("id"));

        } finally {
            closeConnection(co, st, rs);
        }

        return null;

    }

    /** Get the definition of  a Word 
     * @param id identifier of the word
     * @param w (opt) term of the word -- can be null ---- optimization purpose
     */
    public static Entry getDefinition(int id) throws Exception {
        Connection co = null;
        ResultSet rs = null;
        Statement st = null;
        String w, m, def, szSQL;

        try {
            co = initConnection();
            st = co.createStatement();

            szSQL = "SELECT term, morf, definition FROM word WHERE id = " + id;
            rs = st.executeQuery(szSQL);
            if (rs.next()) {
                w = rs.getString("term");
                m = rs.getString("morf");
                def = rs.getString("definition");
            }
            else // Control manually modified ID (Cross-scripting)

                throw new Exception("This word does not exist in our DB");

        } finally {
            closeConnection(co, st, rs);
        }
        return new Entry(w, m, def);
    }

    /** Get the nearest words lexicographycally */ //TODO: use JQuery

    public static LinkedList<Entry> getNearWords(String szWord) throws Exception {
        Connection co = null;
        ResultSet rs = null;
        Statement st = null;
        LinkedList<Entry> l = new LinkedList<Entry>();

        int endIndex = (3 > szWord.length()) ? szWord.length() : 3;
        szWord = szWord.substring(0, endIndex);
        String szSQL = "SELECT * FROM word WHERE term LIKE '" + szWord + "%' LIMIT 10";

        try {
            co = initConnection();
            st = co.createStatement();
            rs = st.executeQuery(szSQL);

            while (rs.next()) {
                int id = rs.getInt("id");
                String word = rs.getString("term");
                l.add(new Entry(id, word));
            }
        } finally {
            closeConnection(co, st, rs);
        }
        return l;
    }

    /** Generate a random word id (accessing to DB) */
    public static int getRandom() throws SQLException {
        int i;
        Connection co = null;
        ResultSet rs = null;
        Statement st = null;

        try {
            co = initConnection();
            st = co.createStatement();
            rs = st.executeQuery("SELECT id FROM word");

            ArrayList<Integer> a = new ArrayList<Integer>();
            while (rs.next())
                a.add(rs.getInt("id"));

            Random r = new Random();
            i = a.get(r.nextInt(a.size()));
        } catch (Exception ex) {
            i = 0;
            Logger.getLogger(Entry.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            closeConnection(co, st, rs);
        }
        return i;
    }

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

    public String getMorfology() {
        return morfology;
    }

    public void setMorfology(String morf) {
        this.morfology = morf;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String def) {
        this.definition = def;
    }

    public int getId() {
        return id;
    }

    public static String[] getMorfologies() {
        return aMorf;
    }

    public String getWord() {
        return word;
    }
}
