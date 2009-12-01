package org.stringtemplate.compiler;

import java.util.LinkedHashMap;

/** A unique set of strings where we can get a string's index.
 *  We can also get them back out in original order. 
 */
public class StringTable {
    LinkedHashMap<String,Integer> table = new LinkedHashMap<String,Integer>();
    int i = -1;

    public int add(String s) {
        Integer I = table.get(s);
        if ( I!=null ) return I;
        i++;
        table.put(s, i);
        return i;
    }

    public int getIndex(String s) { return table.get(s); }

    public String[] toArray() {
        String[] a = new String[table.size()];
        int i = 0;
        for (String s : table.keySet()) a[i++] = s;
        return a;
    }
}
