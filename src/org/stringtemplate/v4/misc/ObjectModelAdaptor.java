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

import org.stringtemplate.v4.Interpreter;
import org.stringtemplate.v4.ModelAdaptor;
import org.stringtemplate.v4.ST;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ObjectModelAdaptor implements ModelAdaptor {
	protected static final Member INVALID_MEMBER;
	static {
		Member invalidMember;
		try {
			invalidMember = ObjectModelAdaptor.class.getDeclaredField("INVALID_MEMBER");
		} catch (NoSuchFieldException ex) {
			invalidMember = null;
		} catch (SecurityException ex) {
			invalidMember = null;
		}

		INVALID_MEMBER = invalidMember;
	}

	protected static final Map<Class<?>, Map<String, Member>> membersCache =
		new HashMap<Class<?>, Map<String, Member>>();

	@Override
	public synchronized Object getProperty(Interpreter interp, ST self, Object o, Object property, String propertyName)
		throws STNoSuchPropertyException
	{
		if (o == null) {
			throw new NullPointerException("o");
		}

		Class<?> c = o.getClass();

		if ( property==null ) {
			return throwNoSuchProperty(c, propertyName, null);
		}

		Member member = findMember(c, propertyName);
		if ( member!=null ) {
			try {
				if (member instanceof Method) {
					return ((Method)member).invoke(o);
				}
				else if (member instanceof Field) {
					return ((Field)member).get(o);
				}
			}
			catch (Exception e) {
				throwNoSuchProperty(c, propertyName, e);
			}
		}

		return throwNoSuchProperty(c, propertyName, null);
	}

	protected static Member findMember(Class<?> clazz, String memberName) {
		if (clazz == null) {
			throw new NullPointerException("clazz");
		}
		if (memberName == null) {
			throw new NullPointerException("memberName");
		}

		synchronized (membersCache) {
			Map<String, Member> members = membersCache.get(clazz);
			Member member;
			if (members != null) {
				member = members.get(memberName);
				if (member != null) {
					return member != INVALID_MEMBER ? member : null;
				}
			}
			else {
				members = new HashMap<String, Member>();
				membersCache.put(clazz, members);
			}

			// try getXXX and isXXX properties, look up using reflection
			String methodSuffix = Character.toUpperCase(memberName.charAt(0)) +
				memberName.substring(1, memberName.length());
			
			member = tryGetMethod(clazz, "get" + methodSuffix);
			if (member == null) {
				member = tryGetMethod(clazz, "is" + methodSuffix);
				if (member == null) {
					member = tryGetMethod(clazz, "has" + methodSuffix);
				}
			}

			if (member == null) {
				// try for a visible field
				member = tryGetField(clazz, memberName);
			}

			members.put(memberName, member != null ? member : INVALID_MEMBER);
			return member;
		}
	}

	protected static Method tryGetMethod(Class<?> clazz, String methodName) {
		try {
			Method method = clazz.getMethod(methodName);
			if (method != null) {
				method.setAccessible(true);
			}

			return method;
		} catch (NoSuchMethodException ex) {
		} catch (SecurityException ex) {
		}

		return null;
	}

	protected static Field tryGetField(Class<?> clazz, String fieldName) {
		try {
			Field field = clazz.getField(fieldName);
			if (field != null) {
				field.setAccessible(true);
			}

			return field;
		} catch (NoSuchFieldException ex) {
		} catch (SecurityException ex) {
		}

		return null;
	}

	protected Object throwNoSuchProperty(Class<?> clazz, String propertyName, Exception cause) {
		throw new STNoSuchPropertyException(cause, null, clazz.getName() + "." + propertyName);
	}
}
