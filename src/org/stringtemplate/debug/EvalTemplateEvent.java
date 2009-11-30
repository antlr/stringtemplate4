package org.stringtemplate.debug;

import org.stringtemplate.ST;

public class EvalTemplateEvent extends InterpEvent {
    public EvalTemplateEvent(ST self, int start, int stop) {
        super(self, start, stop);
    }
}
