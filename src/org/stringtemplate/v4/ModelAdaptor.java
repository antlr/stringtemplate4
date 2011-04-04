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
package org.stringtemplate.v4;

import org.stringtemplate.v4.misc.STNoSuchPropertyException;

/** An object that knows how to convert property references to appropriate
 *  actions on a model object.  Some models, like JDBC, are interface based
 *  (we aren't supposed to care about implementation classes). Some other
 *  models don't follow getter method naming convention.  So, if we have
 *  an object of type M with property method foo() (not getFoo()), we
 *  register a model adaptor object, adap, that converts foo lookup to foo().
 *
 *  Given <a.foo>, we look up foo via the adaptor if "a instanceof(M)".
 *
 *  See unit tests.
 */
public interface ModelAdaptor {
	/** Lookup property name in o and return its value.  It's a good
	 *  idea to cache a Method or Field reflection object to make
	 *  this fast after the first look up.
	 *
	 *  property is normally a String but doesn't have to be. E.g.,
	 *  if o is Map, property could be any key type.  If we need to convert
	 *  to string, then it's done by ST and passed in here.
	 */
	public Object getProperty(Interpreter interp, ST self, Object o, Object property, String propertyName)
		throws STNoSuchPropertyException;
}
