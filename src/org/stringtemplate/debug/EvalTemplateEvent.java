package org.stringtemplate.debug;

public class EvalTemplateEvent extends InterpEvent {
    public EvalTemplateEvent(DebugST self, int start, int stop) {
        super(self, start, stop);
    }
}
