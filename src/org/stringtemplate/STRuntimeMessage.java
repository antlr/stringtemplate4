package org.stringtemplate;

import org.stringtemplate.misc.Interval;
import org.stringtemplate.misc.Coordinate;
import org.stringtemplate.misc.Misc;

import java.io.StringWriter;
import java.io.PrintWriter;

public class STRuntimeMessage extends STMessage {
    /** Where error occurred in bytecode memory */
    public int ip;
    
    public STRuntimeMessage(ErrorType error, int ip) { this(error, ip, null); }
    public STRuntimeMessage(ErrorType error, int ip, ST self) { this(error,ip,self,null); }
    public STRuntimeMessage(ErrorType error, int ip, ST self, Throwable cause) {
        this(error, ip, self, cause, null);
    }
    public STRuntimeMessage(ErrorType error, int ip, ST self, Throwable cause, Object arg) {
        this(error, ip, self, cause, arg, null);
    }
    public STRuntimeMessage(ErrorType error, int ip, ST self, Throwable cause, Object arg, Object arg2) {
        super(error, self, cause, arg, arg2);
        this.ip = ip;
    }

    /** Given an ip (code location), get it's range in source template then
     *  return it's template line:col.
     */
    public String getSourceLocation() {
        Interval I = self.code.sourceMap[ip];
        if ( I==null ) return null;
        // get left edge and get line/col
        int i = I.a;
        Coordinate loc = Misc.getLineCharPosition(self.code.template, i);
        return loc.toString();
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        String loc = getSourceLocation();
        if ( self!=null ) {
            buf.append("context [");
            buf.append(self.getEnclosingInstanceStackString());
            buf.append("]");
        }
        if ( loc!=null ) buf.append(" "+loc);
        buf.append(" "+super.toString());
        return buf.toString();
    }
}
