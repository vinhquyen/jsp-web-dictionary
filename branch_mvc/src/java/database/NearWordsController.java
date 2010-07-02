/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedList;
import javax.naming.NamingException;

/**
 *
 * @author chiron
 */
public class NearWordsController {
    /** Near word and random auxiliar structures */
    private static Hashtable<String, ArrayList<Entry>> htWords;

    /**
     * Get the nearest words lexicographycally
     * @param szWord Word searched (user input)
     * @param lang Word language (to search near words)
     * @return List of Entry near lexicographyally
     * @throws java.lang.Exception
     */
    public static LinkedList<Entry> getNearWords(String szWord, String lang) throws NamingException, SQLException  {
        if (szWord.isEmpty())
            return null;

        int index, inf, sup;
        LinkedList<Entry> l = new LinkedList<Entry>();

        if (htWords == null)
            initAllWordsStructs();

        ArrayList<Entry> aux = htWords.get(lang);

        Entry e = new Entry(-1, szWord);
        aux.add(e);

        Collections.sort(aux, new EntryComparator());

        index = aux.indexOf(e);
        aux.remove(index); // Delete the word inserted artificial to make the lookup but it does not exist

        inf = Math.max(0, index - 15);
        sup = Math.min(index + 15, aux.size());

        for (index = inf; index < sup; index++)
            l.add(aux.get(index));

        return l;
    }

    /**
     * Initialites the structures that allows find lexicographically near words
     * @param allWords Hashtable that contains all the pair <term, id>
     * @param orderedWords SortedSet that contains the term's ordered
     */
    static void initAllWordsStructs() throws NamingException, SQLException  {
        Connection co = null;
        ResultSet rs = null;
        Statement st = null;

        if (htWords == null)
            htWords = new Hashtable<String, ArrayList<Entry>>();
        else
            htWords.clear();

        String aLang[] = {"ar", "be", "ca", "es", "fr"};
        for (String lng : aLang) {
            ArrayList<Entry> aux = new ArrayList<Entry>();
            String szSQL = "SELECT DISTINCT(id), term FROM multilang_" + lng; //+" ORDER BY upper(term)";

            try {
                co = DBManager.initConnection();
                st = co.createStatement();
                rs = st.executeQuery(szSQL);

                while (rs.next())
                    aux.add(new Entry(rs.getInt("id"), rs.getString("term")));

            } finally {
                DBManager.closeConnection(co, st, rs);
            }

            htWords.put(lng, aux);
        }
    }
}
