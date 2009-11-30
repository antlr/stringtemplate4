package org.stringtemplate.debug;

import org.stringtemplate.misc.MultiMap;

import java.util.List;
import java.util.ArrayList;

public class STDebugInfo {
    /** Track all events that occur during rendering.  Create room
     *  for each new ST, but make sure to wipe this list
     *  upon creation of interpreter to reset.  The construction-time
     *  events like "new ST" and "add attribute" can stay, of course.
     */
    public List<InterpEvent> interpEvents = new ArrayList<InterpEvent>();
    /** Track add attribute "events"; used for ST user-level debugging;
     *  Avoid polluting ST with this field when not debugging.
     */
    public MultiMap<String, AddAttributeEvent> addAttrEvents = new MultiMap<String, AddAttributeEvent>();
    public ConstructionEvent newSTEvent = new ConstructionEvent();
}