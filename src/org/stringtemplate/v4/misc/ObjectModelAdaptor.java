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

import org.antlr.runtime.misc.DoubleKeyMap;
import org.stringtemplate.v4.Interpreter;
import org.stringtemplate.v4.ModelAdaptor;
import org.stringtemplate.v4.ST;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

public class ObjectModelAdaptor implements ModelAdaptor {
	/** Cache exact attribute type and property name reflection Member object */
	protected DoubleKeyMap<Class, String, Member> classAndPropertyToMemberCache =
		new DoubleKeyMap<Class, String, Member>();

	/** Cached exception to reuse since creation is expensive part.
	 *  Just in case people use "missing" to mean boolean false not error.
	 */
	static STNoSuchPropertyException cachedException;

	public Object getProperty(Interpreter interp, ST self, Object o, Object property, String propertyName)
		throws STNoSuchPropertyException
	{
		Object value = null;
        Class c = o.getClass();

		if ( property==null ) {
			return throwNoSuchProperty(c.getName() + "." + propertyName);
		}

		// Look in cache for Member first
		Member member = classAndPropertyToMemberCache.get(c, propertyName);
		if ( member!=null ) {
			try {
				Class memberClass = member.getClass();
				if ( memberClass == Method.class ) return ((Method)member).invoke(o);
				if ( memberClass == Field.class ) return ((Field)member).get(o);
			}
			catch (Exception e) {
				throwNoSuchProperty(c.getName() + "." + propertyName);
			}
		}
		return lookupMethod(o, propertyName, value, c);
	}

	public Object lookupMethod(Object o, String propertyName, Object value, Class c) {
		// try getXXX and isXXX properties, look up using reflection
		String methodSuffix = Character.toUpperCase(propertyName.charAt(0))+
			propertyName.substring(1, propertyName.length());
		Method m = Misc.getMethod(c, "get" + methodSuffix);
		if ( m==null ) {
			m = Misc.getMethod(c, "is"+methodSuffix);
			if ( m==null ) {
				m = Misc.getMethod(c, "has"+methodSuffix);
			}
		}
		try {
			if ( m != null ) {
				classAndPropertyToMemberCache.put(c, propertyName, m);
				value = Misc.invokeMethod(m, o, value);
			}
			else {
				// try for a visible field
				Field f = c.getField(propertyName);
				classAndPropertyToMemberCache.put(c, propertyName, f);
				try {
					value = Misc.accessField(f, o, value);
				}
				catch (IllegalAccessException iae) {
					throwNoSuchProperty(c.getName() + "." + propertyName);
				}
			}
		}
		catch (Exception e) {
			throwNoSuchProperty(c.getName() + "." + propertyName);
		}

		return value;
	}

	protected Object throwNoSuchProperty(String propertyName) {
		if ( cachedException==null ) cachedException = new STNoSuchPropertyException();
		cachedException.propertyName = propertyName;
		throw cachedException;
	}
}
