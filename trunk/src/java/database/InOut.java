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
            Logger.getLogger(InOut.class.getName()).log(Level.WARNING, null, ex);
        }
    }

    public static void printWordDef(Entry e, JspWriter out) {
        try {
            out.println("<span class='morf'>" + e.getMorfology() +
                    "</span> " + e.getDefinition());
        } catch (IOException ex) {
            Logger.getLogger(InOut.class.getName()).log(Level.WARNING, null, ex);
        }
    }

    public static void printWordMultiLang(Entry e, JspWriter out) {
        try {
            out.println("<dl>");
                if(e.getAr() != null && e.getAr().length() > 0)
                    out.println("<dt title='aragon&eacute;s'>ar.<dd>"+e.getAr()+"</dd></dt>");
                if(e.getCa() != null && e.getCa().length() > 0)
                    out.println("<dt title='catal&agrave;'>ca.<dd>"+e.getCa()+"</dd></dt>");
                if(e.getEs() != null && e.getEs().length() > 0)
                    out.println("<dt title='castellano'>es.<dd>"+e.getEs()+"</dd></dt>");
                if(e.getFr() != null && e.getFr().length() > 0)
                    out.println("<dt title='fran&ccedil;ais'>fr.<dd>"+e.getFr()+"</dd></dt>");
            out.println("</dl>");
        } catch (IOException ex) {
            Logger.getLogger(InOut.class.getName()).log(Level.WARNING, null, ex);
        }
    }

    /** Parses the user input to avoid Cross-Scripting and HTML injection
     *  - Replace "<" and ">" by the HTML code (for a RAW output)
     * http://fnr.sourceforge.net/docs/net/sourceforge/java/util/text/StringUtils.html
     * http://www.stringutils.com/
     */
    public static String userInputParser(String input) {
        String output;
       /* Pattern p = Pattern.compile("<*>*</>");
        Matcher m = p.matcher(input);
        boolean b = m.matches(); */

        output = input.replaceAll("<", "&lt;").replaceAll(">", "&gt;");

        return output;
    }
}