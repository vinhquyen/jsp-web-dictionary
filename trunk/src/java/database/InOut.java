package database;

import java.io.IOException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

    public static void printWordDef(Entry e, JspWriter out, String szHighLight, ResourceBundle r) {
        try {
            ArrayList<String> aDef = e.getDefinition();
            String szMorf = e.getMorfology();

            szMorf = "<abbr class='morf' title='" + Entry.longMorf(szMorf) + "'>" + szMorf + "</abbr> ";

            /* Hightlight the results when the words have been found by context search */
            if (szHighLight != null) {
                String szDef, tagBeg, tagEnd;
                tagBeg = "<span style='background:yellow;'>";
                tagEnd = "</span>";
                
                for (int i = 0; i<aDef.size(); i++) {
                    Pattern re = Pattern.compile(szHighLight, Pattern.CASE_INSENSITIVE);
                    Matcher m = re.matcher(aDef.get(i));
                    String[] pieces = re.split(aDef.get(i));

                    szDef = "";
                    int j = 0;
                    while(m.find()) {
                        szDef += pieces[j] + tagBeg + m.group() + tagEnd;
                        j++;
                    }
                    
                    if( pieces.length >= j )
                        szDef += pieces[pieces.length - 1];

                    aDef.set(i, szDef);
                }
            }
            
            /* Print the definition and multilingual info (if any) */
            if (aDef.size() > 1) {
                out.print("<ol>");
                int i = 0;
                for (String szDef : aDef) {
                    out.println("<li>" + szMorf + szDef);
                    InOut.printWordMultiLang(e, i, out, r);
                    out.println("</li>");
                    i++;
                }
                out.print("</ol>");
            }
            else if (!aDef.isEmpty()) { /* Check if there are any DB inconsistency */
                out.print("<p>" + szMorf + aDef.get(0) + "</p>");
                InOut.printWordMultiLang(e, 0, out, r);
            }
            else
                printError(new Exception("Check the DB consistency of word " + e.getId()), out);

        } catch (IOException ex) {
            Logger.getLogger(InOut.class.getName()).log(Level.WARNING, null, ex);
        }
    }

    public static void printWordMultiLang(Entry e, int nDef, JspWriter out, ResourceBundle r) {
        String szAr = e.getAr(nDef);
        String szCa = e.getCa(nDef);
        String szEs = e.getEs(nDef);
        String szFr = e.getFr(nDef);

        if (szAr == null && szCa == null && szEs == null && szFr == null)
            return;

        try {
            out.println("<dl>");
            if (szAr != null)
                out.println("<dt title='" + r.getString("lng_ar") + "'>ar.</dt><dd>" + szAr + "</dd><br/>");
            if (szCa != null)
                out.println("<dt title='" + r.getString("lng_ca") + "'>ca.</dt><dd>" + szCa + "</dd><br/>");
            if (szEs != null)
                out.println("<dt title='" + r.getString("lng_es") + "'>es.</dt><dd>" + szEs + "</dd><br/>");
            if (szFr != null)
                out.println("<dt title='" + r.getString("lng_fr") + "'>fr.</dt><dd>" + szFr + "</dd><br/>");
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