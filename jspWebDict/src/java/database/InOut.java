package database;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.jsp.JspWriter;

/** 
 * Implements all the InOut operations included in JspWriter and Visitor Stats
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

    public static void printWordDef(Entry e, JspWriter out) {
        try {
            out.println("<ol>");
            out.print("<li>");
            out.println("<span class='morf'>" + e.getMorfology() +
                    "</span> " + e.getDefinition());
            out.print("</li>");
            out.println("</ol>");
        } catch (IOException ex) {
            Logger.getLogger(InOut.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}