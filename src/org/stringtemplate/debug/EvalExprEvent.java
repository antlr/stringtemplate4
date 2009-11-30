package org.stringtemplate.debug;

import org.stringtemplate.ST;

public class EvalExprEvent extends InterpEvent {
    int exprStart, exprStop; // template pattern location
    String expr;
    public EvalExprEvent(ST self, int start, int stop,
                         int exprStart, int exprStop)
    {
        super(self, start, stop);
        this.exprStart = exprStart;
        this.exprStop = exprStop;
        expr = self.code.template.substring(exprStart, exprStop+1);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()+"{" +
               "self=" + self +
               //", attr=" + self.attributes +
               ", start=" + start +
               ", stop=" + stop +
               ", expr=" + expr +
               '}';
    }
}
