/*
 [The "BSD licence"]
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
package org.stringtemplate;

import org.stringtemplate.interp.CompiledST;

import java.util.HashMap;
import java.util.Map;

public class STGroup {
    /** What is the group name */
    protected String name;

    /** Maps template name to StringTemplate object */
    protected Map<String, CompiledST> templates = new HashMap<String,CompiledST>();

    public static STGroup defaultGroup = new STGroup();
    
    /** The primary means of getting an instance of a template from this
     *  group.
     */
    public ST getInstanceOf(String name) {
        CompiledST c = lookupTemplate(name);
        if ( c!=null ) {
            ST instanceST = createStringTemplate();
            instanceST.group = this;
            instanceST.name = name;
            instanceST.code = c;
            return instanceST;
        }
        return null;
    }

    public ST getEmbeddedInstanceOf(ST enclosingInstance, String name) {
        ST st = getInstanceOf(name);
        st.enclosingInstance = enclosingInstance;
        return st;
    }

    public CompiledST lookupTemplate(String name) {
        return templates.get(name);        
    }

    public void defineTemplate(String name, String template) {
        if ( name!=null && name.indexOf('.')>=0 ) {
            throw new IllegalArgumentException("cannot have '.' in template names");
        }
        try {
            CompiledST code = new Compiler().compile(template);
            templates.put(name, code);
        }
        catch (Exception e) {
            System.err.println("can't parse template: "+template);
        }
    }
    
    /** StringTemplate object factory; each group can have its own. */
    public ST createStringTemplate() {
        ST st = new ST();
        return st;
    }
}
