package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Random;
import java.util.SortedSet;

public class Entry {

    private int id;
    private String word;
    private String morfology;
    private String definition;
    private static String aMorf[] = {"adj.", "adv.", "interfj.", "f.", "m.", "prep.", "pron.", "v."};

    /** Constructor used to return the list of nearest word */
    public Entry(int id, String w) {
        this.id = id;
        this.word = w;
    }
    
    /** Standar constructor */
    public Entry(int id, String w, String m, String def) {
        this.id = id;
        this.word = w;
        this.morfology = m;
        this.definition = def;
    }

    /** Add a new word to the database 
     * @return null: if everything go fine, in another situation return a
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

            /*------------ EOF VALIDATION -------------------*/

            co = initConnection();

            stInsert = co.prepareStatement("INSERT INTO word(term, morf, definition) VALUES(?,?,?)");
            stInsert.setString(1, w);
            stInsert.setString(2, m);
            stInsert.setString(3, def);

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
    /* TODO --> update the SortedSet and the Hashtable of words
     * 1. Insert term and id into the Hashtable
     * 2. Insert term into SortedSet
    */
    public static String updateWord(String w, String m, String def) throws Exception {
        Connection co = null;
        ResultSet rs = null;
        PreparedStatement stUpdate = null;
        int idW;
        
        try {
            //TODO --> comprobaciones?? (A nivel de PRESENTACION)
            if (w == null || def == null)
                throw new Exception("You must insert the word and its def");

            List<String> aux = Arrays.asList(aMorf);
            if (!aux.contains(m))
                throw new Exception("The morfology must be: [" + aux.toString() + "]");

            /*------------ EOF VALIDATION -------------------*/

            co = initConnection();
            
            /* Get the Word IDentifier */
            stUpdate = co.prepareStatement("SELECT id FROM word WHERE  term = ?");
            stUpdate.setString(1, w);

            rs = stUpdate.executeQuery();
            if(rs.next()) {
                idW = rs.getInt("id");
            } else throw new Exception("UPDATE ERROR: the word "+w+"notExists");

            /* Update the word values */
            stUpdate=co.prepareStatement("UPDATE word SET term=?, morf=?, " +
                                            "definition = ? WHERE id = ?");
            stUpdate.setString(1, w);
            stUpdate.setString(2, m);
            stUpdate.setString(3, def);
            stUpdate.setInt(4, idW);
      
            int n = stUpdate.executeUpdate();
            if(n<1) throw new SQLException("Entry.java:125, NOT Update "+w);

        } catch (SQLException ex) {
            return ("ERROR: There are a problem updating the word<br/>\n"
                        + ex.toString());
        } finally {
            closeConnection(co, stUpdate, rs);
        }
        return null;
    }

    public static Entry getDefinition(String szWord) throws Exception {
        Connection co = null;
        ResultSet rs = null;
        Statement st = null;

        /*  [DONE] --> control "stInsert" --> SQL Injection
         *[ as' OR true OR 'as' = 'as ] <-- muestra todas las palabras
         * szWord = szWord.replace("'", "");
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
        return new Entry(id, w, m, def);
    }


    private static Hashtable<String, Integer> allWords;
    private static ArrayList<String> orderedWords;

    /** Get the nearest words lexicographycally */ 
    // FIXME: use ¿JQuery?
    public static LinkedList<Entry> getNearWords(String szWord) throws Exception {
        int index, inf, sup;
        LinkedList<Entry> l = new LinkedList<Entry>();
        String aux;

        if(allWords == null || orderedWords == null) {
            allWords = new Hashtable<String, Integer>();
            orderedWords = new ArrayList<String>();
            initAllWordsStructs(allWords, orderedWords);
        }
        
        
        orderedWords.add(szWord);
        Collections.sort(orderedWords);
        index = orderedWords.indexOf(szWord);
        orderedWords.remove(index); // Delete the word inserted artificial to make the lookup but it does not exist

        inf = Math.max(0, index-5);
        sup = Math.min(index+5, orderedWords.size());

        for(index = inf; index < sup; index++) {
            aux = orderedWords.get(index);
            l.add(new Entry(allWords.get(aux), aux));
        }
        
        return l;
    }

    /**
     * Initialites the structures that allows find lexicographically near words
     * @param allWords Hashtable that contains all the pair <term, id>
     * @param orderedWords SortedSet that contains the term's ordered
    */
    private static void initAllWordsStructs(Hashtable<String, Integer> allWords, ArrayList<String> orderedWords) throws Exception {
        Connection co = null;
        ResultSet rs = null;
        Statement st = null;

        String szSQL = "SELECT term, id FROM word WHERE 1=1 ORDER BY term";

        try {
            co = initConnection();
            st = co.createStatement();
            rs = st.executeQuery(szSQL);

            while (rs.next()) {
                int id = rs.getInt("id");
                String word = rs.getString("term");
                allWords.put(word, id);
                orderedWords.add(word);
            }
        } finally {
            closeConnection(co, st, rs);
        }
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
