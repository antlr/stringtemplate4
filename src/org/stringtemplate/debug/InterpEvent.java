package org.stringtemplate.debug;

public class InterpEvent {
    public DebugST self;
    public int start, stop; // output location
    public InterpEvent(DebugST self, int start, int stop) {
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
