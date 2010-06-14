package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Entry {

    private int id;
    private String word;
    private String morfology;
    private ArrayList<String> definition; // Support of polisemic words (multiple definitions)
    /** Multilingual attributes  TODO: add ArrayList to support polisemic words */
    private ArrayList<String> ar;    // @ar aragones
    private ArrayList<String> ca;   // @ca catalan
    private ArrayList<String> es;  // @es spanish
    private ArrayList<String> fr; // @fr french
    /** Morfology */
    private static String aMorf[] = {"adj.", "adv.", "art.", "conj.", "interj.", "f.", 
        "loc. adv.", "m.", "prep.", "pref.", "pron.", "suf.", "v.", "tr.", "int.",
        "aux.", "imp.", "unip.", "rec.", "ref.", "indet.", "dem.", " r.", "pl.", 
        "loc.", "expr."};
    private static String aLongMorf[] = {"adjetivo", "adverbio", "artículo", "conjunción",
        "interjección", "femenino", "locución adverbial", "masculino", "preposición", 
        "prefijo", "pronombre", "sufijo", "verbo", "transitivo", "intransitivo",
        "auxiliar", "impersonal", "unipersonal", "recíproco", "reflexivo",
        "indeterminado", "demostrativo", " reflexivo", "plural", "locución", "expresión"};
    private static Hashtable<String, String> longMorf;
    /** Near word and random auxiliar structures */
    private static Hashtable<String, ArrayList<Entry>> htWords;

    /***********************
     *   CONSTRUCTORS      *
     ***********************/

    /** Fake constructor used to have an empty Entry (used when adding a new word) */
    public Entry() {
        //this.id = -1;
        this.word = "";
        this.morfology = "";
        this.definition = new ArrayList<String>();
        this.definition.add("");
    }

    /** Constructor used to return the list of nearest word */
    public Entry(int id, String w) {
        this.id = id;
        this.word = w;
    }

    /** Standar constructor */
    public Entry(int id, String w, String m, ArrayList<String> def) {
        this.id = id;
        this.word = w;
        this.morfology = m;
        this.definition = def;
    }

    /***************************************
     * MODIFICATION DEFINITION OPERATIONS  *
     **************************************/
    /** Add a new word to the database
     * @param word the new word to create
     * @param morf morfology (verb, noun, adjetive...)
     * @param aDef definition(s)
     * @return null: if everything go fine, in another situation return a
     * String which describes the problem
     * -- VALIDATION in PRESENTATION TIER -- (JQuery Plugin)
     *  Precondition: word not null && aDef.length > 0 )
     */
    public static String addWord(String word, String morf, String[] aDef) throws Exception {
        // TODO: ADD MULTILANG QUESTIONS!!!!!!!!!!!!!!!!!!
        Connection co = null;
        PreparedStatement stMultiLang, stInsert = null;
        String szSQL;
        int id;

        try {
            co = initConnection();

            stInsert = co.prepareStatement("INSERT INTO word(term, morf) VALUES(?,?)");
            stInsert.setString(1, word);
            stInsert.setString(2, morf);

            if (stInsert.executeUpdate() < 1)
                throw new SQLException("dict.word: " + word + " NOT Inserted");

            /** get the last id (AUTO_INCREMENT) */
            szSQL = "SELECT MAX(id) FROM word";
            ResultSet res = co.createStatement().executeQuery(szSQL);
            if(res.next())
                id = res.getInt(1);
            else throw new SQLException("Entry.java:, Unnespected error " + word);

            int numDef = 0;
            stInsert = co.prepareStatement("INSERT INTO word_definition(id, n_def, definition) VALUES(?,?,?)");
            stMultiLang = co.prepareStatement("INSERT INTO multilang_be(id, n_def, term) VALUES(?,?,?)");

            for(String definition:aDef) {
                stInsert.setInt(1, id);
                stInsert.setInt(2, numDef);
                stInsert.setString(3, definition);
                if (stInsert.executeUpdate() < 1)
                     throw new SQLException("dict.word_definition: Definition from " + id + " NOT Inserted");

                stMultiLang.setInt(1, id);
                stMultiLang.setInt(2, numDef);
                stMultiLang.setString(3, word);
                if (stMultiLang.executeUpdate() < 1)
                    throw new SQLException("dict.multilang_be: Definition from " + id + " NOT Inserted");

                numDef++;
            }

        } catch (SQLException ex) {
            return ("ERROR: There are problems inserting the word<br/>\n" + ex.toString());
        } finally {
            closeConnection(co, stInsert, null);
        }

        initAllWordsStructs(); // Rebuild structures for random search
        return "";
    }
    /** Modify a word from the database
     * @param word the new word to create
     * @param morf morfology (verb, noun, adjetive...)
     * @param aDef definition(s)
     * @return empty String,  if everything go fine
     *         Description of the problem, otherwise
     * -- VALIDATION in PRESENTATION TIER -- (JQuery Plugin)
     *  Precondition: word not null && aDef.length > 0 )
     */
    public static String updateWord(String id, String word, String morf, String[] aDef) throws Exception {
        Connection co = null;
        ResultSet rs = null;
        PreparedStatement stMultiLang, stUpdate = null;
        int idW;
        String res = "";

        try {
            /* FIXME: Check the morphology? Perhaps its better don't do in favor of flexibility
            List<String> aux = Arrays.asList(aMorf);
            if (!aux.contains(morf))
            throw new Exception("The morfology must be: [" + aux.toString() + "]"); */

            /*------------ EOF VALIDATION -------------------*/

            co = initConnection();

            /* Get the Word Identifier */
            try {
                idW = Integer.valueOf(id);
            } catch (Exception ex) {
                return ("ERROR: There are a problem updating the word<br/>\n" +
                        "The identifier is not valid<br/>\n" + ex.toString());
            }

            /* Update the word values */
            stUpdate = co.prepareStatement("UPDATE word SET term=?, morf=? WHERE id = ?");
            stUpdate.setString(1, word);
            stUpdate.setString(2, morf);
            stUpdate.setInt(3, idW);

            if (stUpdate.executeUpdate() < 1)
                res += "ERROR in " + stUpdate.toString() + "<br/>";

            /** UPDATE =  DELETE old definition(s) +  INSERT new one(s) */
            stUpdate = co.prepareStatement("DELETE FROM word_definition WHERE id = ?");
            stUpdate.setInt(1, idW);
            stUpdate.executeUpdate();
            
            stMultiLang = co.prepareStatement("DELETE FROM multilang_be WHERE id = ?");
            stMultiLang.setInt(1, idW);
            stMultiLang.executeUpdate();

            int numDef = 0;
            stUpdate = co.prepareStatement("INSERT INTO word_definition(id, n_def, definition) VALUES(?,?,?)");
            stMultiLang = co.prepareStatement("INSERT INTO multilang_be(id, n_def, term) VALUES(?,?,?)");

            for(String definition:aDef) {
                stUpdate.setInt(1, idW);
                stUpdate.setInt(2, numDef);
                stUpdate.setString(3, definition);
                if (stUpdate.executeUpdate() < 1)
                    res+="ERROR in " + stUpdate.toString() + "<br/>";

                stMultiLang.setInt(1, idW);
                stMultiLang.setInt(2, numDef);
                stMultiLang.setString(3, word);
                if (stMultiLang.executeUpdate() < 1)
                    res += "dict.multilang_be: Definition from " + id + " NOT Inserted";

                numDef++;
            }

        } catch (SQLException ex) {
            return ("ERROR: There are a problem updating the word<br/>\n" + ex.toString());
        } finally {
            closeConnection(co, stUpdate, rs);
        }

        initAllWordsStructs(); // Rebuild structures for random search
        return res;
    }

    public static String deleteWord(String id) throws Exception {
        Connection co = null;
        ResultSet rs = null;
        PreparedStatement stDelete = null;
        int idW;
        String res = "";
        try {
            co = initConnection();

            /* Get the Word Identifier */
            try {
                idW = Integer.valueOf(id);
            } catch (Exception ex) {
                return ("ERROR: There are a problem updating the word<br/>\n" +
                        "The identifier is not valid<br/>\n" + ex.toString());
            }

            stDelete = co.prepareStatement("DELETE FROM word WHERE id = ?");
            stDelete.setInt(1, idW);
            stDelete.executeUpdate();

            stDelete = co.prepareStatement("DELETE FROM word_definition WHERE id = ?");
            stDelete.setInt(1, idW);
            stDelete.executeUpdate();

            stDelete = co.prepareStatement("DELETE FROM multilang_be WHERE id = ?");
            stDelete.setInt(1, idW);
            stDelete.executeUpdate();

        } catch (SQLException ex) {
            String error = "";
            error += "SQLState:  " + ex.getSQLState() + "<br/>";
            error += "Message:  " + ex.getMessage() + "<br/>";
            error += "Vendor:  " + ex.getErrorCode() + "<br/>";

            return ("ERROR: There are a problem deleting the word<br/>\n" + error);
        } finally {
            closeConnection(co, stDelete, rs);
        }

        initAllWordsStructs(); // Rebuild structures for random search
        return res;
    }

    /******************************
     *   SEARCH DEFINITIONS OP    *
     ******************************/
    public static ArrayList<Entry> getDefinition(String szWord, String lang) throws Exception {
        if (szWord.isEmpty())
            return null;

        ArrayList<Entry> res = new ArrayList<Entry>();
        Connection co = null;
        ResultSet rs = null;
        PreparedStatement st = null;

        /*  [DONE] --> control "stInsert" --> SQL Injection
         *[ as' OR true OR 'as' = 'as ] <-- muestra todas las palabras
         * szWord = szWord.replace("'", "");
         */
        Pattern p = Pattern.compile("ar|be|ca|es|fr");
        Matcher m = p.matcher(lang);

        /* Input  for avoid injection with invalid params */
        if (!m.matches())
            throw new SQLException("The lang " + lang + " doesn't exist in our database");

        String szSQL; // = "SELECT id FROM word WHERE term = ?";
        szSQL = "SELECT DISTINCT(id) FROM multilang_" + lang + " WHERE term = ?";

        try {
            co = initConnection();
            st = co.prepareStatement(szSQL);
            st.setString(1, szWord);
            rs = st.executeQuery();

            // Get the ID of the searched WORD
            while (rs.next())
                res.add(getDefinition(rs.getInt("id")));

        } finally {
            closeConnection(co, st, rs);
        }

        return res;
    }

    /**
     * Get the definition of  a Word
     * @param id identifier of the word
     * @return Entry with matching identifer
     * @throws java.sql.SQLException
     * @throws javax.naming.NamingException
     */
    public static Entry getDefinition(int id) throws SQLException, NamingException {
        Entry result = null;
        Connection co = null;
        ResultSet rs = null;
        PreparedStatement st = null;
        String szSQL;

        try {
            szSQL = "SELECT term, morf FROM word WHERE id = ?";

            co = initConnection();
            st = co.prepareStatement(szSQL);
            st.setInt(1, id);

            rs = st.executeQuery();
            if (rs.next()) {
                result = new Entry();
                result.id = id;
                result.word = rs.getString("term");
                result.morfology = rs.getString("morf");
            }
            else // Control manually modified ID (Cross-scripting)
                throw new SQLException("This word does not exist in our DB");

            szSQL = "SELECT n_def, definition FROM word_definition WHERE id = ?";
            st = co.prepareStatement(szSQL);
            st.setInt(1, id);
            rs = st.executeQuery();

            result.definition = new ArrayList<String>();
            result.ar = new ArrayList<String>();
            result.ca = new ArrayList<String>();
            result.es = new ArrayList<String>();
            result.fr = new ArrayList<String>();
            while (rs.next()) {
                result.definition.add(rs.getString("definition"));
                getMultiLangDef(result, rs.getInt("n_def"));
            }

        } finally {
            closeConnection(co, st, rs);
        }
        return result;
    }

    /**
     * Update the Entry @e with the translation of the @word in a set of langs
     * @param e Entry to get the translation
     * @throws java.sql.SQLException
     * @throws javax.naming.NamingException
     */
    private static void getMultiLangDef(Entry e, int n) throws SQLException, NamingException {
        Connection co = null;
        ResultSet rs = null;
        PreparedStatement st = null;
        String szSQL;

        try {
            szSQL = "SELECT ar, ca, es, fr FROM multilang WHERE id = ? AND n_def = ?";
            co = initConnection();
            st = co.prepareStatement(szSQL);
            st.setInt(1, e.id);
            st.setInt(2, n);

            rs = st.executeQuery();
            if (rs.next()) {
                e.ar.add(rs.getString("ar"));
                e.ca.add(rs.getString("ca"));
                e.es.add(rs.getString("es"));
                e.fr.add(rs.getString("fr"));
            }

        } finally {
            closeConnection(co, st, rs);
        }
    }

    /**
     * Get the nearest words lexicographycally
     * @param szWord Word searched (user input)
     * @param lang Word language (to search near words)
     * @return List of Entry near lexicographyally
     * @throws java.lang.Exception
     */
    public static LinkedList<Entry> getNearWords(String szWord, String lang) throws Exception {
        if (szWord.isEmpty())
            return null;

        int index, inf, sup;
        LinkedList<Entry> l = new LinkedList<Entry>();

        if (htWords == null)
            initAllWordsStructs();

        ArrayList<Entry> aux = htWords.get(lang);

        Entry e = new Entry(-1, szWord);
        aux.add(e);

        Collections.sort(aux, new EntryComparator());

        index = aux.indexOf(e);
        aux.remove(index); // Delete the word inserted artificial to make the lookup but it does not exist

        inf = Math.max(0, index - 15);
        sup = Math.min(index + 15, aux.size());

        for (index = inf; index < sup; index++)
            l.add(aux.get(index));

        return l;
    }

    /**
     * Search the word by context, in other words, given a word it looks for it
     * in each definition and then return those ones that match.
     * @param szWord Word to find (in context)
     * @return Array of Entries that match its definition with the given word
     * @throws javax.naming.NamingException
     * @throws java.sql.SQLException
     */
    public static ArrayList<Entry> getWordInContext(String szWord) throws NamingException, SQLException {
        if (szWord.isEmpty())
            return null;

        ArrayList<Entry> res = new ArrayList<Entry>();
        Connection co = null;
        ResultSet rs = null;
        PreparedStatement st = null;

        /** TODO: search only in the examples: LIKE «?» **/
        String szSQL = "SELECT id FROM word_definition WHERE LOWER(definition) REGEXP ? LIMIT 0 , 10";

        try {
            co = initConnection();
            st = co.prepareStatement(szSQL);
            st.setString(1, "[[:<:]]"+ szWord.toLowerCase() + "[[:>:]]" );
            rs = st.executeQuery();

            // Get the ID of the searched WORD
            while (rs.next())
                res.add(getDefinition(rs.getInt("id")));

        } finally {
            closeConnection(co, st, rs);
        }

        return res;
    }

    /***********************
     *      AUXILIAR OP     *
     ***********************/
    /**
     * Initialites the structures that allows find lexicographically near words
     * @param allWords Hashtable that contains all the pair <term, id>
     * @param orderedWords SortedSet that contains the term's ordered
     */
    private static void initAllWordsStructs() throws Exception {
        Connection co = null;
        ResultSet rs = null;
        Statement st = null;

        if (htWords == null)
            htWords = new Hashtable<String, ArrayList<Entry>>();
        else
            htWords.clear();

        String aLang[] = {"ar", "be", "ca", "es", "fr"};
        for (String lng : aLang) {
            ArrayList<Entry> aux = new ArrayList<Entry>();
            String szSQL = "SELECT DISTINCT(id), term FROM multilang_" + lng; //+" ORDER BY upper(term)";

            try {
                co = initConnection();
                st = co.createStatement();
                rs = st.executeQuery(szSQL);

                while (rs.next())
                    aux.add(new Entry(rs.getInt("id"), rs.getString("term")));

            } finally {
                closeConnection(co, st, rs);
            }

            htWords.put(lng, aux);
        }


    //allWords = new Hashtable<String, Integer>();
    //orderedWords = new ArrayList<String>();

    // SELECT DISTINCT term  FROM `multilang_be`
    // Get the terms and IDs for each language (creating new structure)¿?
    //String szSQL = "SELECT term, id FROM word WHERE 1=1 ORDER BY upper(term)";
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
            rs = st.executeQuery("SELECT id FROM word WHERE 1=1");

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

    /********************
     *   MORPHOLOGY     *
     ********************/
    private static void initMorphology() {
        longMorf = new Hashtable<String, String>();
        for (int i = 0; i < aMorf.length; i++)
            longMorf.put(aMorf[i], aLongMorf[i]);
    }

    static String longMorf(String morf) {
        if (longMorf == null)
            initMorphology();

        Enumeration<String> eKeys = longMorf.keys();
        while(eKeys.hasMoreElements()) {
            String shortM = eKeys.nextElement();
            morf = morf.replace(shortM, longMorf.get(shortM));
        }
		
        return replaceHTML(morf);
    }

    static String replaceHTML(String str)
    {
        str = str.replace("à" ,"&agrave;");
        str = str.replace("á" ,"&aacute;");
        str = str.replace("è" ,"&egrave;");
        str = str.replace("é" ,"&eacute;");
        str = str.replace("ì" ,"&igrave;");
        str = str.replace("í" ,"&iacute;");
        str = str.replace("ï" ,"&iuml;");
        str = str.replace("ò" ,"&ograve;");
        str = str.replace("ó" ,"&oacute;");
        str = str.replace("ù" ,"&ugrave;");
        str = str.replace("ú" ,"&uacute;");
        str = str.replace("ü" ,"&uuml;");
        str = str.replace("ç" ,"&ccedil;");
        str = str.replace("·" ,"&middot;");
        
        str = str.replace("À" ,"&Agrave;");
        str = str.replace("Á" ,"&Aacute;");
        str = str.replace("È" ,"&Egrave;");
        str = str.replace("É" ,"&Eacute;");
        str = str.replace("Ì" ,"&Igrave;");
        str = str.replace("Í" ,"&Iacute;");
        str = str.replace("Ï" ,"&Iuml;");
        str = str.replace("Ò" ,"&Ograve;");
        str = str.replace("Ó" ,"&Oacute;");
        str = str.replace("Ù" ,"&Ugrave;");
        str = str.replace("Ú" ,"&Uacute;");
        str = str.replace("Ü" ,"&Uuml;");
        str = str.replace("Ç" ,"&Ccedil;");

        return str;
    }

    /***********************
     *      GETTERS        *
     ***********************/
    public String getMorfology() {
        return morfology;
    }

    public ArrayList<String> getDefinition() {
        return definition;
    }

    /*    public void setDefinition(String aDef) {
    this.definition = aDef;
    } */
    public int getId() {
        return id;
    }

    public static String[] getMorfologies() {
        return aMorf;
    }

    public String getWord() {
        return word;
    }

    /**
     * @return the ar
     */
    public String getAr(int n) {
        String res = null;
        if (n < ar.size() && !ar.get(n).isEmpty())
            res = ar.get(n);
        return res;
    }

    /**
     * @return the ca
     */
    public String getCa(int n) {
        String res = null;
        if (n < ca.size() && !ca.get(n).isEmpty())
            res = ca.get(n);
        return res;
    }

    /**
     * @return the es
     */
    public String getEs(int n) {
        String res = null;
        if (n < es.size() && !es.get(n).isEmpty())
            res = es.get(n);
        return res;
    }

    /**
     * @return the fr
     */
    public String getFr(int n) {
        String res = null;
        if (n < fr.size() && !fr.get(n).isEmpty())
            res = fr.get(n);
        return res;
    }
}
