package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import javax.naming.NamingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Entry {

    private int id;
    private String word;
    private String morfology;
    private ArrayList<String> definition; //~ ArrayList to support polisemic words

    /** Multilingual attributes */
    private ArrayList<String> ar;    // @ar aragones
    private ArrayList<String> ca;   // @ca catalan
    private ArrayList<String> es;  // @es spanish
    private ArrayList<String> fr; // @fr french

    /** Morfology */
    private static String aMorf[] = {"adj.", "adv.", "art.", "conj.", "interj.", "f.", 
        "loc. adv.", "m.", "prep.", "pref.", "pron.", "suf.", "v.", "tr.", "int.",
        "aux.", "imp.", "unip.", "rec.", "ref.", "indet.", "dem.", " r.", "pl.", 
        "loc.", "expr.", "neg."};
    private static String esLongMorf[] = {"adjetivo", "adverbio", "artículo", "conjunción",
        "interjección", "femenino", "locución adverbial", "masculino", "preposición", 
        "prefijo", "pronombre", "sufijo", "verbo", "transitivo", "intransitivo",
        "auxiliar", "impersonal", "unipersonal", "recíproco", "reflexivo",
        "indeterminado", "demostrativo", " reflexivo", "plural", "locución", "expresión",
        "negación"};
    private static String enLongMorf[] = {"adjective", "adverb", "article", "conjunction",
        "interjection", "female", "adverb", "masculine", "preposition",
        "prefix", "pronoun", "suffix", "verb", "transitive", "intransitive",
        "auxiliary", "impersonal", "unipersonal", "reciprocal", "reflective",
        "indefinite", "demonstration", "reflective", "plural", "phrase", "expression",
        "negative"};
    private static String caLongMorf[] = {"adjectiu", "adverbi", "article", "conjunció",
        "interjecció", "femení", "locució adverbial", "masculí", "preposició",
        "prefix", "pronom", "sufix", "verb", "transitiu", "intransitiu",
        "auxiliar", "impersonal", "unipersonal", "recíproc", "reflexiu",
        "indeterminat", "demostratiu", "reflexiu", "plural", "locució", "expressió",
        "negació"};

    private static Hashtable<String, Hashtable<String, String>> intTan;

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
     * @return id inserted word,  if everything go fine
     *         -1, otherwise
     * -- VALIDATION in PRESENTATION TIER -- (JQuery Plugin)
     *  Precondition: word not null && aDef.length > 0 )
     */
    public static int addWord(String word, String morf, String[] aDef) throws NamingException, SQLException {
        // TODO: ADD MULTILANG QUESTIONS!!!!!!!!!!!!!!!!!!
        Connection co = null;
        PreparedStatement stMultiLang, stInsert = null;
        String szSQL;
        int id;

        try {
            co = DBManager.initConnection();

            stInsert = co.prepareStatement("INSERT INTO word(term, morf) VALUES(?,?)");
            stInsert.setString(1, word);
            stInsert.setString(2, morf);

            if (stInsert.executeUpdate() < 1)
                throw new SQLException("dict.word: " + word + " NOT Inserted");

            /** get the last id (AUTO_INCREMENT) */
            szSQL = "SELECT MAX(id) FROM word";
            ResultSet res = co.createStatement().executeQuery(szSQL);
            if(res.next()) {
                id = res.getInt(1);
            } else {
                InOut.setStatus(InOut.ERROR_DB, "ERROR: No word.id returned after the INSERT INTO word" + word);
                return -1;
            }
            
            int numDef = 0;
            stInsert = co.prepareStatement("INSERT INTO word_definition(id, n_def, definition) VALUES(?,?,?)");
            stMultiLang = co.prepareStatement("INSERT INTO multilang_be(id, n_def, term) VALUES(?,?,?)");

            for(String definition:aDef) {
                if(definition.length()>0) {
                    stInsert.setInt(1, id);
                    stInsert.setInt(2, numDef);
                    stInsert.setString(3, definition);
                    if (stInsert.executeUpdate() < 1) {
                         InOut.setStatus(InOut.ERROR_DB, "ERROR: dict.word_definition: Definition from " + id + " NOT Inserted");
                         return -1;
                    }

                    stMultiLang.setInt(1, id);
                    stMultiLang.setInt(2, numDef);
                    stMultiLang.setString(3, word);
                    if (stMultiLang.executeUpdate() < 1) {
                        InOut.setStatus(InOut.ERROR_DB, "ERROR: dict..multilang_be: Definition from " + id + " NOT Inserted");
                         return -1;
                    }
                    numDef++;
                }
            }

        } catch (SQLException ex) {
            InOut.setStatus(ex.getErrorCode(), "ERROR: There are problems inserting the word<br/>\n" + ex.toString());
            id = -1;
        } finally {
            DBManager.closeConnection(co, stInsert, null);
        }
        if(id > 0) NearWordsController.initAllWordsStructs(); // Rebuild structures for random search
        return id;
    }
    /** Modify a word from the database
     * @param word the new word to create
     * @param morf morfology (verb, noun, adjetive...)
     * @param aDef definition(s)
     * @return id updated word,  if everything go fine
     *         -1, otherwise
     * -- VALIDATION in PRESENTATION TIER -- (JQuery Plugin)
     *  Precondition: word not null && aDef.length > 0 )
     */
    public static int modifyWord(String id, String word, String morf, String[] aDef) throws NamingException, SQLException {
        Connection co = null;
        ResultSet rs = null;
        PreparedStatement stMultiLang, stUpdate = null;
        int idW;

        try {
            /* FIXME: Check the morphology? Perhaps its better don't do in favor of flexibility
            List<String> aux = Arrays.asList(aMorf);
            if (!aux.contains(morf))
            throw new Exception("The morfology must be: [" + aux.toString() + "]"); */

            /*------------ EOF VALIDATION -------------------*/

            co = DBManager.initConnection();

            /* Get the Word Identifier */
            try {
                idW = Integer.valueOf(id);
            } catch (NumberFormatException numberFormatException) {
                InOut.setStatus(InOut.ERROR_PARAM, "ERROR: There are a problem " +
                        "updating the word. The identifier must be an integer<br/>\n");
                return -1;
            }

            /* Update the word values */
            stUpdate = co.prepareStatement("UPDATE word SET term=?, morf=? WHERE id = ?");
            stUpdate.setString(1, word);
            stUpdate.setString(2, morf);
            stUpdate.setInt(3, idW);

            if (stUpdate.executeUpdate() < 1) {
                InOut.setStatus(InOut.ERROR_DB, "ERROR in '"+ stUpdate.toString() +"'");
                return -1;
            }
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
                if (stUpdate.executeUpdate() < 1) {
                    InOut.setStatus(InOut.ERROR_DB, "ERROR in '"+ stUpdate.toString() +"'");
                    return -1;
                }

                stMultiLang.setInt(1, idW);
                stMultiLang.setInt(2, numDef);
                stMultiLang.setString(3, word);
                if (stMultiLang.executeUpdate() < 1) {
                    InOut.setStatus(InOut.ERROR_DB, "dict.multilang_be: Definition from " + id + " NOT updated");
                    return -1;
                }

                numDef++;
            }

        } catch (SQLException ex) {
            InOut.setStatus(InOut.ERROR_DB, "ERROR: There are a problem updating the word<br/>\n" + ex.toString());
            idW = -1;
        } finally {
            DBManager.closeConnection(co, stUpdate, rs);
        }

        NearWordsController.initAllWordsStructs(); // Rebuild structures for random search
        return idW;
    }
/**
 * Delete a word from the database
 * @param id : identifier of the word
 * @return id deleted word,  if everything go fine
     *         -1, otherwise
 * @throws java.lang.Exception
 */
    public static int deleteWord(String id) throws NamingException, SQLException {
        Connection co = null;
        ResultSet rs = null;
        PreparedStatement stDelete = null;
        int idW;
        int[] iRes = {0,0,0};

        try {
            co = DBManager.initConnection();

            /* Get the Word Identifier */
            try {
                idW = Integer.valueOf(id);
            } catch (NumberFormatException numberFormatException) {
                throw new IllegalArgumentException("The identifier '" + id + "' must be an integer");
            }

            stDelete = co.prepareStatement("DELETE FROM word WHERE id = ?");
            stDelete.setInt(1, idW);
            iRes[0] = stDelete.executeUpdate();

            stDelete = co.prepareStatement("DELETE FROM word_definition WHERE id = ?");
            stDelete.setInt(1, idW);
            iRes[1] = stDelete.executeUpdate();

            stDelete = co.prepareStatement("DELETE FROM multilang_be WHERE id = ?");
            stDelete.setInt(1, idW);
            iRes[2] = stDelete.executeUpdate();

            for(int aux : iRes) {
                if(aux == 0) {
                    InOut.setStatus(InOut.ERROR_DB, "The word cannot be delete. Posible cause: it doesn't exist.");
                    iRes[0] = -1;
                    break;
                }
            }

        } catch (SQLException ex) {
            StringBuffer error = new StringBuffer();
            error.append("ERROR: There are a problem deleting the word<br/>\n");
            error.append("SQLState: ").append(ex.getSQLState()).append("<br/>");
            error.append("Message: ").append(ex.getMessage()).append("<br/>");
            error.append("Vendor: ").append(ex.getErrorCode()).append("<br/>");

            InOut.setStatus(InOut.ERROR_DB, error.toString());
            iRes[0] = -1;
        } finally {
            DBManager.closeConnection(co, stDelete, rs);
        }

        NearWordsController.initAllWordsStructs(); // Rebuild structures for random search
        return iRes[0];
    }

    /******************************
     *   SEARCH DEFINITIONS OP    *
     ******************************/

    /** TODO: complete javadoc
     * @param szWord
     * @param lang
     * @return
     * @throws java.lang.Exception
     */
    public static ArrayList<Entry> searchDefinition(String szWord, String lang, String accentInsensitive) throws SQLException, NamingException {
        if (szWord.length() == 0)
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
        if (!m.matches()) {
            throw new IllegalArgumentException("The lang " + lang + " doesn't exist in our database");
        }

        String szSQL; // = "SELECT id FROM word WHERE term = ?";
        szSQL = "SELECT DISTINCT(id) FROM multilang_" + lang + " WHERE term = ?";

        if(accentInsensitive != null && accentInsensitive.equalsIgnoreCase("on")) {
            szSQL = "SELECT DISTINCT(id) FROM multilang_"+lang+" WHERE term = CONVERT(? USING utf8) COLLATE utf8_unicode_ci";
        }


        try {
            co = DBManager.initConnection();
            st = co.prepareStatement(szSQL);
            st.setString(1, szWord);
            rs = st.executeQuery();

            // Get the ID of the searched WORD
            while (rs.next())
                res.add(searchDefinition(rs.getInt("id")));

        } finally {
            DBManager.closeConnection(co, st, rs);
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
    public static Entry searchDefinition(int id) throws SQLException, NamingException {
        Entry result = null;
        Connection co = null;
        ResultSet rs = null;
        PreparedStatement st = null;
        String szSQL;

        try {
            szSQL = "SELECT term, morf FROM word WHERE id = ?";

            co = DBManager.initConnection();
            st = co.prepareStatement(szSQL);
            st.setInt(1, id);

            rs = st.executeQuery();
            if (rs.next()) {
                result = new Entry();
                result.id = id;
                result.word = rs.getString("term");
                result.morfology = rs.getString("morf");
            }
            else { // Control manually modified ID (Cross-scripting)
                if(DBManager.getSizeDB() == 0)
                    throw new IllegalArgumentException("Our DB is empty. This action cannot be performed!");
                throw new IllegalArgumentException("This word does not exist in our DB");
            }

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
            DBManager.closeConnection(co, st, rs);
        }
        return result;
    }

    /**
     * Update the Entry @e with the translation of the @word in a set of langs
     * @param e Entry to get the translation
     * @throws java.sql.SQLException
     * @throws javax.naming.NamingException
     */
    private static void getMultiLangDef(Entry e, int n) throws NamingException, SQLException {
        Connection co = null;
        ResultSet rs = null;
        PreparedStatement st = null;
        String szSQL;

        try {
            szSQL = "SELECT ar, ca, es, fr FROM multilang WHERE id = ? AND n_def = ?";
            co = DBManager.initConnection();
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
            DBManager.closeConnection(co, st, rs);
        }
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
        if (szWord == null || szWord.trim().length() == 0)
            return null;

        String szRegExpr;
        ArrayList<Entry> res = new ArrayList<Entry>();
        Connection co = null;
        ResultSet rs = null;
        PreparedStatement st = null;

        // [[:<:]], [[:>:]]  These markers stand for word boundaries (match beginning and end words, respectively
        szRegExpr = "([[:<:]]|«)"; //Begin
        szRegExpr += szWord.toLowerCase();
        szRegExpr += "(»|[[:>:]])"; //End

        String szSQL = "SELECT id FROM word_definition WHERE LOWER(definition) REGEXP ? LIMIT 0 , 10";

        try {
            co = DBManager.initConnection();
            st = co.prepareStatement(szSQL);
            st.setString(1, szRegExpr);
            rs = st.executeQuery();

            // Get the ID of the searched WORD
            while (rs.next())
                res.add(searchDefinition(rs.getInt("id")));

        } finally {
            DBManager.closeConnection(co, st, rs);
        }

        return res;
    }

    /***********************
     *      AUXILIAR OP     *
     ***********************/
    /**
     * Checks if exist a definition with w_term = szWord AND w_morf = szMorf
     * and w_id <> szId (cause its an UPDATE situtation)
     * @param szId  an w_id (UPDATE) or null (ADD/INSERT)
     * @param szWord The word term
     * @param szMorf The word morfology
     * @return word ID , if exist
     *         0 (zero), otherwise
     * @throws java.lang.Exception
     */
    public static int existDefinition(String szId, String szWord, String szMorf) throws Exception {
        int id;
        String szSel, szSQL;
        Connection co = null;
        ResultSet rs = null;
        PreparedStatement st = null;
        try {
            co = DBManager.initConnection();
            if (szId == null) //Add word checks
                szId = "0";

            szSel = "SELECT id FROM word WHERE term = ? AND morf = ?";
            szSQL = "SELECT id FROM ("+szSel+") AS w WHERE NOT w.id = ? OR w.id IS NULL";

            st = co.prepareStatement(szSQL);
            st.setString(1, szWord);
            st.setString(2, szMorf);
            st.setString(3, szId);
            rs = st.executeQuery();

            if(rs.first())
                id = rs.getInt(1);
            else id = 0;

        } finally {
            DBManager.closeConnection(co, st, rs);
        }
        return id;
    }
    
    /**
     * Generate a random word id (accessing to DB)
     * @return random entry identifier
     * @throws java.sql.SQLException
     */
    public static int getRandom() throws SQLException {
        int i;
        Connection co = null;
        ResultSet rs = null;
        Statement st = null;

        try {
            co = DBManager.initConnection();
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
            DBManager.closeConnection(co, st, rs);
        }
        return i;
    }

    /********************
     *   MORPHOLOGY     *
     ********************/
    private static void initMorphology() {
        Hashtable<String, String> longMorf;
        intTan = new Hashtable<String, Hashtable<String, String>>();

        longMorf = new Hashtable<String, String>();
        intTan.put("ca", longMorf);
        for (int i = 0; i < aMorf.length; i++) {
            longMorf.put(aMorf[i], caLongMorf[i]);
        }

        longMorf = new Hashtable<String, String>();
        intTan.put("es", longMorf);
        for (int i = 0; i < aMorf.length; i++) {
            longMorf.put(aMorf[i], esLongMorf[i]);
        }
        
        longMorf = new Hashtable<String, String>();
        intTan.put("en", longMorf);
        for (int i = 0; i < aMorf.length; i++) {
            longMorf.put(aMorf[i], enLongMorf[i]);
        }
    }

    String longMorf(Locale loc) {
        Hashtable<String, String> longMorf;
        String lang, morf;

        if (intTan == null)
            initMorphology();

        lang = loc.getLanguage();

        Matcher m = Pattern.compile("ca|es|en").matcher(lang); // {an}
        // TODO loc.getCountry == "BE" (benasques)
        // TODO implementar con Resources en vez de arrays de cada lengua
        // mantener el array the "keys" == shortMorf
        if (!m.matches()) { lang = "es"; }  //Default language = spanish

        morf = this.getMorfology();
        longMorf = intTan.get(lang);
        Enumeration<String> eKeys = longMorf.keys();
        while(eKeys.hasMoreElements()) { //TODO: improve efficiency
            String shortM = eKeys.nextElement();
            morf = morf.replace(shortM, longMorf.get(shortM));
        }
		
        return InOut.replaceHTML(morf);
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

    public int getId() {
        return id;
    }

    public String getWord() {
        return word;
    }

    /**
     * @return the ar
     */
    public String getAr(int n) {
        String res = null;
        if (n < ar.size() && ar.get(n).length() > 0)
            res = ar.get(n);
        return res;
    }

    /**
     * @return the ca
     */
    public String getCa(int n) {
        String res = null;
        if (n < ca.size() && ca.get(n).length() > 0)
            res = ca.get(n);
        return res;
    }

    /**
     * @return the es
     */
    public String getEs(int n) {
        String res = null;
        if (n < es.size() && es.get(n).length() > 0)
            res = es.get(n);
        return res;
    }

    /**
     * @return the fr
     */
    public String getFr(int n) {
        String res = null;
        if (n < fr.size() && fr.get(n).length() > 0)
            res = fr.get(n);
        return res;
    }
}
