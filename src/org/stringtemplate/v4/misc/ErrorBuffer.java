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

import org.stringtemplate.v4.STErrorListener;

import java.util.ArrayList;
import java.util.List;

/** Used during tests to track all errors. */
public class ErrorBuffer implements STErrorListener {
    public List<STMessage> errors = new ArrayList<STMessage>();

	@Override
    public void compileTimeError(STMessage msg) {
        errors.add(msg);
    }

	@Override
    public void runTimeError(STMessage msg) {
        if ( msg.error != ErrorType.NO_SUCH_PROPERTY ) { // ignore these
            errors.add(msg);
        }
    }

	@Override
    public void IOError(STMessage msg) {
        errors.add(msg);
    }

	@Override
    public void internalError(STMessage msg) {
        errors.add(msg);
    }
	@Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        for (STMessage m : errors) {
            buf.append(m.toString()+Misc.newline);
        }
        return buf.toString();
    }
}
