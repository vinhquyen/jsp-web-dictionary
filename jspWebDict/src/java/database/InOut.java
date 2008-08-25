/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.jsp.JspWriter;

/**
 *
 * @author chiron
 */
public class InOut {

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
		out.println("<span class='morf'>" + e.getMorfologia() + "</span> " + e.getDefinicion() + "</li>");

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
