package org.stringtemplate.debug;

import org.stringtemplate.misc.ErrorManager;
import org.stringtemplate.misc.MultiMap;
import org.stringtemplate.misc.ErrorBuffer;
import org.stringtemplate.*;
import org.stringtemplate.gui.STViz;

import java.util.List;
import java.util.ArrayList;
import java.util.Locale;
import java.io.StringWriter;

/** To avoid polluting ST instances with debug info when not debugging. */
public class DebugST extends ST {
    /** Track all events that occur during rendering. */
    public List<InterpEvent> interpEvents = new ArrayList<InterpEvent>();

    /** Track construction-time add attribute "events"; used for ST user-level debugging */
    public MultiMap<String, AddAttributeEvent> addAttrEvents = new MultiMap<String, AddAttributeEvent>();

    public ConstructionEvent newSTEvent = new ConstructionEvent();

    @Override
    public void add(String name, Object value) {
        if ( groupThatCreatedThisInstance.debug ) {
            addAttrEvents.map(name, new AddAttributeEvent(name, value));
        }
        super.add(name, value);
    }

    // LAUNCH A WINDOW TO INSPECT TEMPLATE HIERARCHY

    public List<InterpEvent> inspect() { return inspect(Locale.getDefault()); }

    public List<InterpEvent> inspect(int lineWidth) { return inspect(Locale.getDefault(), lineWidth); }

    public List<InterpEvent> inspect(Locale locale) { return inspect(locale, STWriter.NO_WRAP); }

    public List<InterpEvent> inspect(Locale locale, int lineWidth) {
        ErrorBuffer errors = new ErrorBuffer();
        ErrorManager.setErrorListener(errors);
        StringWriter out = new StringWriter();
        STWriter wr = new AutoIndentWriter(out);
        wr.setLineWidth(lineWidth);
        Interpreter interp = new Interpreter(groupThatCreatedThisInstance, locale);
        interp.exec(wr, this); // render and track events
        new STViz(this, out.toString(), interp.getEvents(), errors.errors);
        return interp.getEvents();
    }

    // TESTING SUPPORT
    
    public List<InterpEvent> getEvents() { return getEvents(Locale.getDefault()); }

    public List<InterpEvent> getEvents(int lineWidth) { return getEvents(Locale.getDefault(), lineWidth); }

    public List<InterpEvent> getEvents(Locale locale) { return getEvents(locale, STWriter.NO_WRAP); }

    public List<InterpEvent> getEvents(Locale locale, int lineWidth) {
        StringWriter out = new StringWriter();
        STWriter wr = new AutoIndentWriter(out);
        wr.setLineWidth(lineWidth);
        Interpreter interp = new Interpreter(groupThatCreatedThisInstance, locale);
        interp.exec(wr, this); // render and track events
        return interp.getEvents();
    }

}
