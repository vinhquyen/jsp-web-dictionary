package database;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspWriter;

/** 
 * Implements all the InOut operations included in JspWriter and Visitor Stats
 */
public class InOut {

    public final static int ERROR_DB = 20;
    public final static int ERROR_PARAM = 10;
    private static int statusCode;
    private static String statusMsg;

    /**
     * Show the word searched at the browser title bar
     * @param request
     * @return
     */
    public static String generateTitle(HttpServletRequest request) throws UnsupportedEncodingException {
        String szTitle = request.getParameter("word");
        String idWord = request.getParameter("id");
        if (szTitle == null) {
            if (idWord != null) {
                try {
                    int id = Integer.parseInt(idWord);
                    szTitle = "- " + Entry.searchDefinition(id).getWord();
                } catch (Exception e) {
                    szTitle = "";
                }
            } else {
                szTitle = "";
            }
        } else {
            //TODO if(szWord contains caracteres raros) // hacer esto de abajo...
            szTitle = new String(szTitle.getBytes("8859_1"), "utf-8");
            szTitle = "- " + szTitle;
        }
        return szTitle;
    }
    //TODO: create a list of aplication errors (with code and user-friendly message)

    public static void printError(Exception e, JspWriter out) {
        try {
            out.println("<p class='error'>");
            out.println(e.toString());
            out.println("</p>");
        } catch (IOException ex) {
            Logger.getLogger(InOut.class.getName()).log(Level.WARNING, null, ex);
        }
    }

    public static void printError(int errorCode, String szMsg, JspWriter out) {
        try {
            out.println("<p class='error'>");
            out.println("ERROR " + errorCode + ": " + szMsg);
            out.println("</p>");
        } catch (IOException ex) {
            Logger.getLogger(InOut.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void printInfoMsg(String szMsg, JspWriter out) {
        try {
            out.println("<p id='msg' class='ok'>\n" + szMsg + "\n</p>");
        } catch (IOException ex) {
            Logger.getLogger(InOut.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Print an Entry in a html human readable format
     * @param e : Entry to be printed
     * @param out : The JspWriter output
     * @param szHighLight : The keywords to highlight (only in ContextSearch mode)
     * @param r : ResourceBundle (internationalization question)
     */
    public static void printWordDef(Entry e, JspWriter out, String szHighLight, ResourceBundle r) {
        try {
            ArrayList<String> aDef = e.getDefinition();
            String szMorf = e.getMorfology();

            szMorf = "<abbr class='morf' title='" + e.longMorf(r.getLocale()) + "'>" + szMorf + "</abbr> ";

            /* Highlight the results when the words have been found by context search */
            if (szHighLight != null) {
                String szDef, tagBeg, tagEnd;
                tagBeg = "<span class='highlight'>";
                tagEnd = "</span>";

                for (int i = 0; i < aDef.size(); i++) {
                    // Regex --> \b == word boundary
                    Pattern re = Pattern.compile("\\b" + szHighLight + "\\b", Pattern.CASE_INSENSITIVE);
                    Matcher m = re.matcher(aDef.get(i));
                    String[] pieces = re.split(aDef.get(i));

                    szDef = "";
                    int j = 0;
                    while (m.find()) {
                        szDef += pieces[j] + tagBeg + m.group() + tagEnd;
                        j++;
                    }

                    if (pieces.length >= j) {
                        szDef += pieces[pieces.length - 1];
                    }

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
            } else if (aDef.size() > 0) { /* Check if there are any DB inconsistency */
                out.print("<p>" + szMorf + aDef.get(0) + "</p>");
                InOut.printWordMultiLang(e, 0, out, r);
            } else {
                printError(new Exception("Check the DB consistency of word " + e.getId()), out);
            }

        } catch (IOException ex) {
            Logger.getLogger(InOut.class.getName()).log(Level.WARNING, null, ex);
        }
    }

    public static void printWordMultiLang(Entry e, int nDef, JspWriter out, ResourceBundle r) {
        String szAr = e.getAr(nDef);
        String szCa = e.getCa(nDef);
        String szEs = e.getEs(nDef);
        String szFr = e.getFr(nDef);

        if (szAr == null && szCa == null && szEs == null && szFr == null) {
            return;
        }

        try {
            out.println("<dl>");
            if (szAr != null) {
                out.println("<dt title='" + r.getString("lng_ar") + "'>ar.</dt><dd>" + szAr + "</dd>");
            }
            if (szCa != null) {
                out.println("<dt title='" + r.getString("lng_ca") + "'>ca.</dt><dd>" + szCa + "</dd>");
            }
            if (szEs != null) {
                out.println("<dt title='" + r.getString("lng_es") + "'>es.</dt><dd>" + szEs + "</dd>");
            }
            if (szFr != null) {
                out.println("<dt title='" + r.getString("lng_fr") + "'>fr.</dt><dd>" + szFr + "</dd>");
            }
            out.println("</dl>");
        } catch (IOException ex) {
            Logger.getLogger(InOut.class.getName()).log(Level.WARNING, null, ex);
        }
    }

    static void setStatus(int code, String msg) {
        statusCode = code;
        statusMsg = msg;
    }

    public static void statusInfo(JspWriter out) {
        String szClass;
        if (statusCode == 0) {
            szClass = "info";
        } else {
            szClass = "error";
        }
        try {
            out.println("<p class='" + szClass + "'>" + statusMsg + "</p>");
            statusCode = 0;
            statusMsg = "";
        } catch (IOException ex) {
            Logger.getLogger(InOut.class.getName()).log(Level.WARNING, null, ex);
        }
    }

    /**
     *
     * @param request
     * @param response
     * @return
    // NOT USED --> if the user wants to leave the secure mode, it must do manually
    public static String leaveSecureMode(HttpServletRequest request, HttpServletResponse response) {
    ResourceBundle rConf = ResourceBundle.getBundle("resources/config");
    String unsecure = String.valueOf(rConf.getString("tomcat_standar_port"));
    String secure = String.valueOf(rConf.getString("tomcat_secure_port"));

    String path = request.getRequestURL().toString();
    path = path.replace("https://", "http://");
    path = path.replace(secure, unsecure);
    path = path + "?" + request.getQueryString();

    try {
    response.sendRedirect(path);
    } catch (IOException ex) {
    Logger.getLogger(InOut.class.getName()).log(Level.SEVERE, null, ex);
    }
    return path;
    } */
    /**
     * Force a request to switch to a HTTPSecure conection.
     * WORKS usin a sendRedirect.
     * IMPORTANT: NOT works if invoked from an included page due to JSP specification.
     * @param request: the request
     * @param response: the generated response
     */
    public static void switchSecureMode(HttpServletRequest request, HttpServletResponse response) {
        ResourceBundle rConf = ResourceBundle.getBundle("resources/config");
        String unsecure = String.valueOf(rConf.getString("tomcat_standar_port"));
        String secure = String.valueOf(rConf.getString("tomcat_secure_port"));

        String path = request.getRequestURL().toString();
        path = path.replace("http://", "https://");
        path = path.replace(unsecure, secure);
        path = path + "?" + request.getQueryString();

        try {
            response.sendRedirect(path);
        } catch (IOException ex) {
            Logger.getLogger(InOut.class.getName()).log(Level.SEVERE, null, ex);
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

    /**
     * Replace non ASCII chars with the HTML entity
     * @param str
     * @return  String with non ASCII chars html-encoded
     */
    static String replaceHTML(String str) {
        str = str.replace("à", "&agrave;");
        str = str.replace("á", "&aacute;");
        str = str.replace("è", "&egrave;");
        str = str.replace("é", "&eacute;");
        str = str.replace("ì", "&igrave;");
        str = str.replace("í", "&iacute;");
        str = str.replace("ï", "&iuml;");
        str = str.replace("ò", "&ograve;");
        str = str.replace("ó", "&oacute;");
        str = str.replace("ù", "&ugrave;");
        str = str.replace("ú", "&uacute;");
        str = str.replace("ü", "&uuml;");
        str = str.replace("ç", "&ccedil;");
        str = str.replace("·", "&middot;");

        str = str.replace("À", "&Agrave;");
        str = str.replace("Á", "&Aacute;");
        str = str.replace("È", "&Egrave;");
        str = str.replace("É", "&Eacute;");
        str = str.replace("Ì", "&Igrave;");
        str = str.replace("Í", "&Iacute;");
        str = str.replace("Ï", "&Iuml;");
        str = str.replace("Ò", "&Ograve;");
        str = str.replace("Ó", "&Oacute;");
        str = str.replace("Ù", "&Ugrave;");
        str = str.replace("Ú", "&Uacute;");
        str = str.replace("Ü", "&Uuml;");
        str = str.replace("Ç", "&Ccedil;");

        return str;
    }
}