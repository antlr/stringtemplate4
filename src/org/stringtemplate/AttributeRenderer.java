package org.stringtemplate;

import java.util.Locale;

/** This interface describes an object that knows how to format or otherwise
 *  render an object appropriately.  Usually this is used for locale changes
 *  for objects such as Date and floating point numbers...  You can either
 *  have an object that is sensitive to the locale or have a different object
 *  per locale.
 *
 *  Each template may have a renderer for each object type or can default
 *  to the group's renderer or the super group's renderer if the group doesn't
 *  have one.
 *
 *  The toString(Object,String) method is used when the user uses the
 *  format option: $o; format="f"$.  It checks the formatName and applies the
 *  appropriate formatting.  If the format string passed to the renderer is
 *  not recognized then simply call toString().
 *
 *  formatString can be null but locale will at least be Locale.getDefault()
 */
public interface AttributeRenderer {
/*
    public String toString(Object o);
    public String toString(Object o, String formatString);
     */
    public String toString(Object o, String formatString, Locale locale);
}
