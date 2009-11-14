package org.stringtemplate;

import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.*;

public class DateRenderer implements AttributeRenderer {
    public static final Map<String,Integer> formatToInt =
        new HashMap<String,Integer>() {
            {
                put("short", DateFormat.SHORT);
                put("medium", DateFormat.MEDIUM);
                put("long", DateFormat.LONG);
                put("full", DateFormat.FULL);

                put("date:short", DateFormat.SHORT);
                put("date:medium", DateFormat.MEDIUM);
                put("date:long", DateFormat.LONG);
                put("date:full", DateFormat.FULL);

                put("time:short", DateFormat.SHORT);
                put("time:medium", DateFormat.MEDIUM);
                put("time:long", DateFormat.LONG);
                put("time:full", DateFormat.FULL);
            }
        };

    public String toString(Object o, String formatString, Locale locale) {
        Date d = null;
        if ( formatString==null ) formatString = "short";
        if ( o instanceof Calendar ) d = ((Calendar)o).getTime();
        else d = (Date)o;
        Integer styleI = formatToInt.get(formatString);
        DateFormat f = null;
        if ( styleI==null ) f = new SimpleDateFormat(formatString);
        else {
            int style = styleI.intValue();
            if ( formatString.startsWith("date:") ) f = DateFormat.getDateInstance(style);
            else if ( formatString.startsWith("time:") ) f = DateFormat.getTimeInstance(style);
            else f = DateFormat.getDateTimeInstance(style, style);
        }
        return f.format(d);
    }
}
