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

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.CommonTokenStream;

import java.util.*;

public class STGroup {
    /** What is the group name */
    public String name;

    public String supergroup;

    public List<String> interfaces;


    /** Maps template name to StringTemplate object */
    protected LinkedHashMap<String, CompiledST> templates = new LinkedHashMap<String,CompiledST>();

    public static STGroup defaultGroup = new STGroup();

    public STGroup() {
        ;
    }
    
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

    public CompiledST defineTemplate(String name, String template) {
        return defineTemplate(name, null, template);
    }

    public CompiledST defineTemplate(String name,
                                     LinkedHashMap<String,FormalArgument> args,
                                     String template)
    {
        if ( name!=null && (name.length()==0 || name.indexOf('.')>=0) ) {
            throw new IllegalArgumentException("cannot have '.' in template names");
        }
        template = template.trim();
        try {
            Compiler c = new Compiler();
            CompiledST code = c.compile(template);
            code.formalArguments = args;
            templates.put(name, code);
            // compile any default args
            if ( args!=null ) {
                for (String a : args.keySet()) {
                    FormalArgument fa = args.get(a);
                    if ( fa.defaultValue!=null ) {
                        Compiler c2 = new Compiler();
                        fa.compiledDefaultValue = c2.compile(template);
                    }
                }
            }
            // compile any anonymous subtemplates
            for (String subname : c.subtemplates.keySet()) {
                String block = c.subtemplates.get(subname);
                defineAnonSubtemplate(subname, block);
            }
            return code;
        }
        catch (Exception e) {
            System.err.println("can't parse template: "+template);
            e.printStackTrace(System.err);
        }
        return null;
    }

    public CompiledST defineAnonSubtemplate(String subname, String block) {
        // look for argument in "{n | actual template}"
        // only allow one
        LinkedHashMap<String,FormalArgument> args =
            Compiler.parseSubtemplateArg(block);
        String t = block;
        if ( args!=null ) {
            int pipe = block.indexOf('|');
            t = block.substring(pipe+1);
        }
        CompiledST compiledSub = defineTemplate(subname, t);
        compiledSub.formalArguments = args;
        return compiledSub;
    }

    /** StringTemplate object factory; each group can have its own. */
    public ST createStringTemplate() {
        ST st = new ST();
        return st;
    }

    public String toString() {
        return show();
    }

    public String show() {
        StringBuilder buf = new StringBuilder();
        buf.append("group "+name);
        if ( supergroup!=null ) buf.append(" : "+supergroup);
        buf.append(";"+Misc.newline);
        for (String name : templates.keySet()) {
            CompiledST c = templates.get(name);
            buf.append(name);
            buf.append('(');
            if ( c.formalArguments!=null ) {
                buf.append( Misc.join(c.formalArguments.values().iterator(), ",") );
            }
            buf.append(')');
            buf.append(" ::= <<"+Misc.newline);
            buf.append(c.template+Misc.newline);
            buf.append(">>"+Misc.newline);
        }
        return buf.toString();
    }

    // Temp / testing
    public static STGroup load(String filename) throws Exception {
        ANTLRFileStream fs = new ANTLRFileStream(filename);
        GroupLexer lexer = new GroupLexer(fs);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        GroupParser parser = new GroupParser(tokens);
        STGroup g = parser.group();
        return g;
    }

}
