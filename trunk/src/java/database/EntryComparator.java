/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package database;

import java.util.Comparator;

/**
 *
 * @author chiron
 */
public class EntryComparator implements Comparator<Entry> {

    public int compare(Entry e1, Entry e2) {
        return e1.getWord().compareToIgnoreCase(e2.getWord());
    }

}
