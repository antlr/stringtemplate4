package org.stringtemplate.misc;

import org.stringtemplate.STErrorListener;
import org.stringtemplate.misc.STMessage;
import java.util.List;
import java.util.ArrayList;

public class ErrorBuffer implements STErrorListener {
    public List<STMessage> errors = new ArrayList<STMessage>();

    public void compileTimeError(STMessage msg) {
        errors.add(msg);
    }

    public void runTimeError(STMessage msg) {
        errors.add(msg);
    }

    public void IOError(STMessage msg) {
        errors.add(msg);
    }

    public void internalError(STMessage msg) {
        errors.add(msg);
    }
    public String toString() {
        StringBuilder buf = new StringBuilder();
        for (STMessage m : errors) {
            buf.append(m.toString()+Misc.newline);
        }
        return buf.toString();
    }
}
