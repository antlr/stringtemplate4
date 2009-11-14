package org.stringtemplate;

import java.util.Locale;

/** This interface describes an object that knows how to format or otherwise
 *  render an object appropriately.  There is one renderer registered per
 *  group for a given Java type.
 *
 *  If the format string passed to the renderer is not recognized then simply
 *  call toString().
 *
 *  formatString can be null but locale will at least be Locale.getDefault()
 */
public interface AttributeRenderer {
    public String toString(Object o, String formatString, Locale locale);
}
