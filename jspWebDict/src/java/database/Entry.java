package database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Random;

public class Entry {

    private int id;
    private String word;
    private String morfologia;
    private String definition;
    private ArrayList<String> examples;

    public Entry(int id, String w) {
	this.id = id;
	this.word = w;
    }

    public Entry(String w, String m, String def, ArrayList<String> ex) {
	this.word = w;
	//TODO test if m is a valid morfology
	this.morfologia = m;
	this.definition = def;
	this.examples = ex;
    }

    /** Add a new word to the database 
     * @return null: if everything go fine, in another situation returb a 
     * String which describes the problem
     */
    public static String addWord(String w, String m, String def) throws Exception {
	if (true)
	    throw new Exception("Operation not yet implemented"); //TODO
	Connection co = null;
	ResultSet rs = null;
	Statement st = null;
	try {
	    //TODO --> comprobaciones?? ++ EJEMPLOS de USO
	    if (w == null || def == null)
		throw new Exception("You must insert the word and its def");
	    //TODO --> Control SQL Exception --> Eliminar entrada si hay algun problema: mantener consistencia BD

	    String szSQL = "INSERT INTO word(term) VALUES('" + w + "')";
	    co = initConnection();
	    st = co.createStatement();
	    int n = st.executeUpdate(szSQL);

	    if (n > 0) {
		szSQL = "SELECT id FROM word WHERE term = '" + w + "'";
		rs = st.executeQuery(szSQL);
		while (rs.next()) {
		    int id = rs.getInt("id");
		    szSQL = "INSERT INTO entry VALUES(" + id + ", '" + m + "', '" + def + "')";
		    st = co.createStatement();
		    n = st.executeUpdate(szSQL);
		    if (n == 0)
			throw new SQLException("ERROR en el INSERT" + st.getWarnings()); //TODO -- DESHACER inserciones--> ROLLBACK?
		}
	    }
	} catch (SQLException ex) {
	    Logger.getLogger(Entry.class.getName()).log(Level.SEVERE, null, ex);
	    return (ex.toString());
	} finally {
	    closeConnection(co, st, rs);
	}
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
	String szSQL = "SELECT * FROM word WHERE term LIKE '" + szWord + "%'";

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

    /** Establish a connection with Database throw JDBC*/
    private static Connection initConnection() throws NamingException, SQLException {
	Context initCtx = new InitialContext();
	Context envCtx = (Context) initCtx.lookup("java:comp/env");
	DataSource ds = (DataSource) envCtx.lookup("jdbc/jspWebDict");

	return ds.getConnection();
    }

    /** Try to close cleanly a DB connection releasing Statement and 
     * ResulSet resources.      */
    private static void closeConnection(Connection co, Statement st, ResultSet rs) throws SQLException {
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

    public String getMorfologia() {
	return morfologia;
    }

    public void setMorfologia(String morfologia) {
	this.morfologia = morfologia;
    }

    public String getDefinicion() {
	return definition;
    }

    public void setDefinicion(String definicion) {
	this.definition = definicion;
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

    public String getWord() {
	return word;
    }
}
