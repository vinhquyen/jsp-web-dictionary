package database;

import java.sql.Connection;
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
    private ArrayList<String> examples;
    private static String aMorf[] = {"adj.", "adv.", "interfj.", "f.", "m.", "prep.", "pron.", "v."};

    public Entry(int id, String w) {
	this.id = id;
	this.word = w;
    }

    public Entry(String w, String m, String def, ArrayList<String> ex) {
	this.word = w;
	this.morfology = m;
	this.definition = def;
	this.examples = ex;
    }

    /** Add a new word to the database 
     * @return null: if everything go fine, in another situation returb a 
     * String which describes the problem
     */
    public static String addWord(String w, String m, String def, String exs) throws Exception {
	// TODO: test addWord
	
	Connection co = null;
	ResultSet rs = null;
	Statement st = null;

	try {
	    //TODO --> comprobaciones?? (A nivel de PRESENTACION)
	    if (w == null || def == null)
		throw new Exception("You must insert the word and its def");

	    List<String> aux = Arrays.asList(aMorf);
	    if(!aux.contains(m))
		throw new Exception("The morfology must be: [" + aux.toString() + "]");
	    
	    // FIXME Eliminar comillas simples (') de los strings (sustituir por [\'] )
	    def = def.replaceAll("'", "\'");
	    exs = exs.replaceAll("'", "\'");
	    /*------------ EOF VALIDATION -------------------*/
	    
	    co = initConnection();
	    st = co.createStatement();
	    st.execute("START TRANSACTION");

	    String szSQL = "INSERT INTO word(term, morf) VALUES('" + w + "', '" + m + "')";
	    int n = st.executeUpdate(szSQL);

	    if (n > 0) {
		szSQL = "SELECT LAST_INSERT_ID() FROM word";
		rs = st.executeQuery(szSQL);
		if (rs.next()) {
		    int idWord = rs.getInt(1);
		    szSQL = "INSERT INTO entry(idWord, definition) VALUES(" + idWord + ", '" + def + "')";
		    st = co.createStatement();
		    n = st.executeUpdate(szSQL);
		    if (n == 0)
			throw new SQLException("ERROR en el INSERT" + st.getWarnings());

		    szSQL = "SELECT LAST_INSERT_ID() FROM entry";
		    st = co.createStatement();
		    rs = st.executeQuery(szSQL);

		    if (rs.next()) {
			int idEntry = rs.getInt(1);
			for (String example : processExamples(exs)) {
			    szSQL = "INSERT INTO examples VALUES (" + idWord + ", " + idEntry + ", '" + example + "')";
			    st = co.createStatement();
			    n = st.executeUpdate(szSQL);
			}
		    }
		}
	    }
	    st.execute("COMMIT");
	} catch (SQLException ex) {
	    // Undo operations to maintain DB consistency
	    st.execute("ROLLBACK");
	    Logger.getLogger(Entry.class.getName()).log(Level.SEVERE, null, ex);
	    return ("INFO: All the operations have been rollbacked <br/>\n" + ex.toString());
	} finally {
	    closeConnection(co, st, rs);
	}
	return null;
    }
    
    public static String updateWord(String w, String m, String def, String exs) throws Exception {
     /* FIXME: use PreparedStatements
      szSQL = SELECT id FROM word WHERE  term = ?  // ?=w
      szSQL.setString(1, w);
      idW = szSQL.executeQuery(); //FIXME: getRow().getCol(1)¿?
      
      szSQL=UPDATE entry SET definition = ? WHERE idWord = ?
      szSQL.sestString(1, def); //?1=def;?2=idW
      szSQL.setInt(2, idW);
      szSQL.executeUpdate();
     */
      return null;
    }

    public static ArrayList<LinkedList<Entry>> getDefinition(String szWord) throws Exception {
	ArrayList<LinkedList<Entry>> aRes = new ArrayList<LinkedList<Entry>>();
	Connection co = null;
	ResultSet rs = null;
	Statement st = null;

	/*  TODO --> control "szSQL" --> SQL Injection
	 *[ as' OR true OR 'as' = 'as ] <-- muestra todas las palabras
	 * szWord = szWord.replace("'", ""); (eliminar comillas simples?)
	 */

	String szSQL;
	szSQL = "SELECT id FROM word WHERE term = '" + szWord + "'";

	try {
	    co = initConnection();
	    st = co.createStatement();
	    rs = st.executeQuery(szSQL);

	    // Get the ID of the searched WORD
	    while (rs.next())
		aRes.add(getDefinition(rs.getInt("id")));

	} finally {
	    closeConnection(co, st, rs);
	}

	return aRes;

    }

    /** Get the definition of  a Word 
     * @param id identifier of the word
     * @param w (opt) term of the word -- can be null ---- optimization purpose
     */
    public static LinkedList<Entry> getDefinition(int id) throws Exception {
	Connection co = null;
	ResultSet rs = null;
	Statement st = null;
	String w, m;
	String szSQL;
	LinkedList<Entry> l = new LinkedList<Entry>();

	try {
	    co = initConnection();
	    st = co.createStatement();

	    szSQL = "SELECT term, morf FROM word WHERE id = " + id;
	    rs = st.executeQuery(szSQL);
	    if (rs.next()) {
		w = rs.getString("term");
		m = rs.getString("morf");
	    }
	    else // Control manually modified ID
		throw new Exception("This word does not exist in our DB");

	    /** Get and process the multiple definitions */
	    szSQL = "SELECT * FROM entry WHERE idWord = " + id;
	    rs = st.executeQuery(szSQL);

	    while (rs.next()) {
		int idEntry = rs.getInt("id");
		String def = rs.getString("definition");
		/** Get examples of use */
		szSQL = "SELECT sentence FROM examples WHERE (idWord = " + id +
			" AND idEntry = " + idEntry + ")";
		Statement stEX = co.createStatement();
		ResultSet rsEX = stEX.executeQuery(szSQL);
		ArrayList<String> ex = new ArrayList<String>();

		while (rsEX.next())
		    ex.add(rsEX.getString("sentence"));

		l.add(new Entry(w, m, def, ex));

		rsEX.close();
		stEX.close();
	    }
	} finally {
	    closeConnection(co, st, rs);
	}
	return l;
    }

    /** Get the nearest words lexicographycally */
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

    public ArrayList<String> getExamples() {
	return examples;
    }

    public void setExamples(ArrayList<String> examples) {
	this.examples = examples;
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
