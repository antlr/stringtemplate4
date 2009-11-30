package org.stringtemplate.debug;

import org.stringtemplate.ST;

public class InterpEvent {
    public ST self;
    public int start, stop; // output location
    public InterpEvent(ST self, int start, int stop) {
        this.self = self;
        this.start = start;
        this.stop = stop;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()+"{" +
               "self=" + self +
               //", attr=" + self.attributes +
               ", start=" + start +
               ", stop=" + stop +
               '}';
    }
}
