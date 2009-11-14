package org.stringtemplate;

import java.util.List;
import java.util.ArrayList;

public class DebugST extends ST {
    public static class AddEvent {
		String name;
		Object value;
		Throwable source;
		public AddEvent(String name, Object value) {
			this.name = name;
			this.value = value;
			this.source = new Throwable();
		}
	}

	/** Track add attribute "events"; used for ST user-level debugging */
	List<AddEvent> addEvents; // TODO: put this in a subclass; alter factor in STGroup

    public void add(String name, Object value) {
        if ( name==null ) return; // allow null value
        super.add(name, value);

		if ( code.nativeGroup.detects(ErrorTolerance.DETECT_ADD_ATTR) ) {
			if ( addEvents == null ) addEvents = new ArrayList<AddEvent>();
			addEvents.add(new AddEvent(name, value));
		}
    }
}
