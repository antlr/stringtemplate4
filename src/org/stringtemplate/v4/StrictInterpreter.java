package org.stringtemplate.v4;

import java.io.IOException;
import java.util.Locale;

import org.stringtemplate.v4.debug.EvalExprEvent;
import org.stringtemplate.v4.misc.ErrorManager;
import org.stringtemplate.v4.misc.ErrorType;

public class StrictInterpreter extends Interpreter {
    public StrictInterpreter(STGroup group, boolean debug) {
        super(group, debug);
    }

    public StrictInterpreter(STGroup group, Locale locale, boolean debug) {
        super(group, locale, debug);
    }

    public StrictInterpreter(STGroup group, ErrorManager errMgr, boolean debug) {
        super(group, errMgr, debug);
    }

    public StrictInterpreter(STGroup group, Locale locale, ErrorManager errMgr, boolean debug) {
        super(group, locale, errMgr, debug);
    }

    @Override
    protected int writeText(STWriter out, InstanceScope scope, String o) {
        int start = out.index(); // track char we're about to write
        int n = writeTextObject(out, scope, o);
        if ( debug ) {
            EvalExprEvent e = new EvalExprEvent(scope,
                                                start, out.index() - 1,
                                                getExprStartChar(scope),
                                                getExprStopChar(scope));
            trackDebugEvent(scope, e);
        }
        return n;
    }

    protected int writeTextObject(STWriter out, InstanceScope scope, String v) {
        try {
            return out.write(v);
        }
        catch (IOException ioe) {
            errMgr.IOError(scope.st, ErrorType.WRITE_IO_ERROR, ioe, v);
            return 0;
        }
    }
}
