/*
 [The "BSD license"]
 Copyright (c) 2009 Terence Parr
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 1. Redistributions of source code must retain the above copyright
     notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
     notice, this list of conditions and the following disclaimer in the
     documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
     derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.stringtemplate.misc;

import org.stringtemplate.ST;

/** Used to track errors that occur in the ST interpreter. */
public class STRuntimeMessage extends STMessage {
    /** Where error occurred in bytecode memory */
    public int ip = -1;
    
    public STRuntimeMessage(ErrorType error, int ip) { this(error, ip, null); }
    public STRuntimeMessage(ErrorType error, int ip, ST self) { this(error,ip,self,null); }
    public STRuntimeMessage(ErrorType error, int ip, ST self, Object arg) {
        this(error, ip, self, null, arg, null);
    }
    public STRuntimeMessage(ErrorType error, int ip, ST self, Throwable e, Object arg) {
        this(error, ip, self, e, arg, null);
    }
    public STRuntimeMessage(ErrorType error, int ip, ST self, Throwable e, Object arg, Object arg2) {
        super(error, self, e, arg, arg2);
        this.ip = ip;
    }


    /** Given an ip (code location), get it's range in source template then
     *  return it's template line:col.
     */
    public String getSourceLocation() {
        if ( ip<0 ) return null;
        Interval I = self.impl.sourceMap[ip];
        if ( I==null ) return null;
        // get left edge and get line/col
        int i = I.a;
        Coordinate loc = Misc.getLineCharPosition(self.impl.template, i);
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
