/*
 * [The "BSD license"]
 *  Copyright (c) 2011 Terence Parr
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.stringtemplate.v4.misc;

import org.stringtemplate.v4.InstanceScope;
import org.stringtemplate.v4.Interpreter;

/** Used to track errors that occur in the ST interpreter. */
public class STRuntimeMessage extends STMessage {
	/** Which interpreter was executing?  If {@code null}, can be IO error or
	 *  bad URL etc...
	 */
	final Interpreter interp;
    /** Where error occurred in bytecode memory. */
    public final int ip;
	public final InstanceScope scope;
	//List<ST> enclosingStack;

    public STRuntimeMessage(Interpreter interp, ErrorType error, int ip) {
		this(interp, error, ip, null);
	}
    public STRuntimeMessage(Interpreter interp, ErrorType error, int ip, InstanceScope scope) {
		this(interp, error,ip,scope,null);
	}
    public STRuntimeMessage(Interpreter interp, ErrorType error, int ip, InstanceScope scope, Object arg) {
        this(interp, error, ip, scope, null, arg, null);
    }
    public STRuntimeMessage(Interpreter interp, ErrorType error, int ip, InstanceScope scope, Throwable e, Object arg) {
        this(interp, error, ip, scope, e, arg, null);
    }
	public STRuntimeMessage(Interpreter interp, ErrorType error, int ip, InstanceScope scope, Throwable e, Object arg, Object arg2) {
		this(interp, error, ip, scope, e, arg, arg2, null);
	}
	public STRuntimeMessage(Interpreter interp, ErrorType error, int ip, InstanceScope scope, Throwable e, Object arg, Object arg2, Object arg3) {
		super(error, scope != null ? scope.st : null, e, arg, arg2, arg3);
		this.interp = interp;
		this.ip = ip;
		this.scope = scope;
	}

    /** Given an IP (code location), get it's range in source template then
     *  return it's template line:col.
     */
    public String getSourceLocation() {
        if ( ip<0 || self==null || self.impl==null ) return null;
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
        String loc = null;
        if ( self!=null ) {
            loc = getSourceLocation();
            buf.append("context [");
            if ( interp!=null ) {
				buf.append( Interpreter.getEnclosingInstanceStackString(scope) );
			}
            buf.append("]");
        }
        if ( loc!=null ) buf.append(" "+loc);
        buf.append(" "+super.toString());
        return buf.toString();
    }
}
