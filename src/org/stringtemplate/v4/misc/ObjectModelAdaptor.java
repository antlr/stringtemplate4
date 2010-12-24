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
package org.stringtemplate.v4.misc;

import org.stringtemplate.v4.ModelAdaptor;
import org.stringtemplate.v4.ST;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ObjectModelAdaptor implements ModelAdaptor {
	public Object getProperty(ST self, Object o, Object property, String propertyName)
		throws STNoSuchPropertyException
	{
		Object value = null;
        Class c = o.getClass();

		if ( property==null ) {
			throw new STNoSuchPropertyException(null, c.getName() + "." + propertyName);
		}

        // try getXXX and isXXX properties

        // look up using reflection
        String methodSuffix = Character.toUpperCase(propertyName.charAt(0))+
            propertyName.substring(1, propertyName.length());
        Method m = Misc.getMethod(c, "get" + methodSuffix);
        if ( m==null ) {
            m = Misc.getMethod(c, "is"+methodSuffix);
        }
        if ( m != null ) {
            // TODO: save to avoid lookup later
            try {
                value = Misc.invokeMethod(m, o, value);
            }
            catch (Exception e) {
				throw new STNoSuchPropertyException(e, c.getName() + "." + propertyName);
            }
        }
        else {
            // try for a visible field
            try {
                Field f = c.getField(propertyName);
                //self.getGroup().cacheClassProperty(c,propertyName,f);
                try {
                    value = Misc.accessField(f, o, value);
                }
                catch (IllegalAccessException iae) {
					throw new STNoSuchPropertyException(iae, c.getName() + "." + propertyName);
                }
            }
            catch (NoSuchFieldException nsfe) {
				throw new STNoSuchPropertyException(nsfe, c.getName() + "." + propertyName);
            }
        }

        return value;
	}
}
