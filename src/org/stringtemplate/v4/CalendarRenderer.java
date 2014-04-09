package org.stringtemplate.v4;

import java.util.Calendar;
import java.util.Locale;

public class CalendarRenderer implements AttributeRenderer<Calendar> {
    private final DateRenderer delegate = new DateRenderer();
    
    @Override
    public String toString(Calendar o, String formatString, Locale locale) {
        return delegate.toString(o.getTime(), formatString, locale);
    }
}
