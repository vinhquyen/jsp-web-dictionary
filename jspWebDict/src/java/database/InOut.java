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
 * Implements all the InOut operations included in JspWriter and Visitor Stats
 */
public class InOut {

    private static String[] _aBrowser = {"Firefox", "IE", "Opera", "Chrome"};
    private static String[] _aOS = {"Linux", "Windows", "MacOS"};
    private static int _Visitors = 1;

    public static int getVisitors() {
	return _Visitors++;
    }

    public static int addStat(String host, String userAgent, String cookie, JspWriter out) {
	String browser, os, szSQL;
	int iVisitors = -1;

	browser = getBrowser(userAgent);
	os = getOperatingSystem(userAgent);

	String dateFormat = "yyyy-MM-dd HH:mm:ss";
	Calendar cal = Calendar.getInstance();
	SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
	String date = sdf.format(cal.getTime()).toString();

	Connection co = null;
	ResultSet rs = null;
	Statement st = null;

	try {
	    co = Entry.initConnection();
	    st = co.createStatement();

	    /* Insert new visitor */ //TODO: improve SELECT COUNT(cookie) WHERE cookie = cookie
	    szSQL = "SELECT COUNT(session) FROM stats WHERE session = '" + cookie + "'";
	    rs = st.executeQuery(szSQL);

	    if (!rs.next() || rs.getInt("COUNT(session)") == 0) {
		szSQL = "INSERT INTO stats(ip, date, browser, os, session) VALUES ('" + host + "','" + date + "', '" + browser + "', '" + os + "', '" + cookie + "')";
		st.executeUpdate(szSQL);
	    }

	    szSQL = "SELECT COUNT(DISTINCT(session)) FROM stats";
	    rs = st.executeQuery(szSQL);
	    rs.next();
	    iVisitors = rs.getInt("COUNT(DISTINCT(session))");
	} catch (Exception ex) {
	    Logger.getLogger(Entry.class.getName()).log(Level.SEVERE, null, ex);
	    try {
		out.print(ex.getMessage());
	    } catch (Exception e) {
	    } finally {
	    }
	} finally {
	    try {
		Entry.closeConnection(co, st, rs);
	    } catch (SQLException ex) {
		Logger.getLogger(InOut.class.getName()).log(Level.SEVERE, null, ex);
	    }
	}

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
			"</span> " + e.getDefinition());

		ArrayList<String> aEx = e.getExamples();
		if (aEx != null && !aEx.isEmpty()) {
		    out.println("<ul>");
		    for (String szExample : aEx)
			out.println("<li>" + szExample + "</li>");
		    out.println("</ul>");
		}
		out.print("</li>");
	    }
	    out.println("</ol>");
	} catch (IOException ex) {
	    Logger.getLogger(InOut.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    private static String getBrowser(String userAgent) {
	int i = 0;
	while (!userAgent.contains(_aBrowser[i]) && i < _aBrowser.length)
	    i++;
	if (i < _aBrowser.length)
	    return _aBrowser[i];
	else
	    return "Other";
    }

    private static String getOperatingSystem(String userAgent) {
	int i = 0;
	while (!userAgent.contains(_aOS[i]) && i < _aOS.length)
	    i++;
	if (i < _aOS.length)
	    return _aOS[i];
	else
	    return "Other";
    }
}
