package org.stringtemplate.debug;

import org.stringtemplate.ST;
import org.stringtemplate.ErrorTolerance;

import java.util.List;
import java.util.ArrayList;

public class DebugST extends ST {
    public static class AddAttributeEvent extends Event {
        String name;
        Object value;
        public AddAttributeEvent(String name, Object value) {
            this.name = name;
            this.value = value;
        }
    }

	/** Track add attribute "events"; used for ST user-level debugging;
     *  Avoid polluting ST with this field when not debugging.
     */
	List<AddAttributeEvent> addEvents;
	
    public void add(String name, Object value) {
        if ( name==null ) return; // allow null value
        super.add(name, value);

		if ( code.nativeGroup.detects(ErrorTolerance.DETECT_ADD_ATTR) ) {
			if ( addEvents == null ) addEvents = new ArrayList<AddAttributeEvent>();
			addEvents.add(new AddAttributeEvent(name, value));
		}
    }
}
