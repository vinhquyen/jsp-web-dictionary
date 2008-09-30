/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.jsp.JspWriter;

/**
 *
 * @author chiron
 */
public class InOut {
    private static String lastCookie;
    
    public static int addStat(String host, String browser, String os, String cookie) throws SQLException {
	int iVisitors = -1;
	
	String dateFormat = "yyyy-MM-dd HH:mm:ss";
	Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
	String date = sdf.format(cal.getTime()).toString();

	String szSQL;
	Connection co = null;
	ResultSet rs = null;
	Statement st = null;
	
	try {
	    co = Entry.initConnection();
	    st = co.createStatement();
	    
	    /* Insert new visitor */ //TODO: improve SELECT COUNT(cookie) WHERE cookie = cookie
	    if(lastCookie == null || lastCookie.compareTo(cookie) != 0){
		szSQL = "INSERT INTO stats(ip, date, browser, os) VALUES ('"+ host 
			+"','" + date +"', '" + browser + "', '" + os + "')";
		st.executeUpdate(szSQL);
	    }
	    
	    szSQL = "SELECT COUNT(DISTINCT(ip)) FROM stats";
	    rs = st.executeQuery(szSQL);
	    rs.next();
	    
	    iVisitors = rs.getInt("COUNT(DISTINCT(ip))");
	} catch (Exception ex) {
	    Logger.getLogger(Entry.class.getName()).log(Level.SEVERE, null, ex);
	} finally {
	    Entry.closeConnection(co, st, rs);
	}
	lastCookie = cookie;
	return iVisitors;
    }

    public static void printError(Exception e, JspWriter out) {
	try {
	    out.print("<p class='error'>");
	    out.print(e.toString());
	    out.print("</p>");
	} catch (IOException ex) {
	    Logger.getLogger(InOut.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    public static void printWordDef(LinkedList<Entry> laux, JspWriter out) {
	try {
	    out.println("<ol>");
	    for (Entry e : laux) {
		out.print("<li>");
		out.println("<span class='morf'>" + e.getMorfology() + 
				"</span> " + e.getDefinition() + "</li>");

		ArrayList<String> aEx = e.getExamples();
		if (aEx != null && !aEx.isEmpty()) {
		    out.println("<ul>");
		    for (String szExample : aEx)
			out.println("<li>" + szExample + "</li>");
		    out.println("</ul>");
		}
	    }
	    out.println("</ol>");
	} catch (IOException ex) {
	    Logger.getLogger(InOut.class.getName()).log(Level.SEVERE, null, ex);
	}
    }
}
