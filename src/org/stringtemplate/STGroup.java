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

import org.antlr.runtime.*;

import java.util.*;
import java.io.File;

public class STGroup {
    /** When we use key as a value in a dictionary, this is how we signify. */
    public static final String DICT_KEY = "key";
    public static final String DEFAULT_KEY = "default";

    public static STErrorListener DEFAULT_ERROR_LISTENER =
        new STErrorListener() {
            public void error(String s, Throwable e) {
                System.err.println(s);
                if ( e!=null ) {
                    e.printStackTrace(System.err);
                }
            }
            public void warning(String s) {
                System.out.println(s);
            }
        };

    /** What is the group name */
    public String name;

    public String supergroup;

    public List<String> interfaces;

    public char delimiterStartChar = '<'; // Use <expr> by default
    public char delimiterStopChar = '>';

    /** Maps template name to StringTemplate object */
    protected LinkedHashMap<String, CompiledST> templates =
        new LinkedHashMap<String,CompiledST>();

    public static STGroup defaultGroup = new STGroup();

    /** Maps dict names to HashMap objects.  This is the list of dictionaries
     *  defined by the user like typeInitMap ::= ["int":"0"]
     */
    protected Map<String, Map<String,Object>> dictionaries =
        new HashMap<String, Map<String,Object>>();

    public String fileName = null; // !=null if loaded from file
    public String dirName = null;  // !=null if rooted at a dir
    protected boolean alreadyLoaded = false;
    
    /** Where to report errors.  All string templates in this group
     *  use this error handler by default.
     */
    public STErrorListener listener = DEFAULT_ERROR_LISTENER;

    public STGroup() {
        ;
    }

    public STGroup(String groupFileOrDir) {
        File f = new File(groupFileOrDir);
        if ( f.isDirectory() ) dirName = groupFileOrDir;
        else fileName = groupFileOrDir;
    }

    public void load() {
        if ( fileName==null ) return; // do nothing if no file or it's a dir
        if ( alreadyLoaded ) return;

        try {
            ANTLRFileStream fs = new ANTLRFileStream(fileName);
            GroupLexer lexer = new GroupLexer(fs);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            GroupParser parser = new GroupParser(tokens);
            parser.group(this);
            alreadyLoaded = true;
        }
        catch (Exception e) {
            listener.error("can't load group file", e);
        }
    }
    
    /** The primary means of getting an instance of a template from this
     *  group.
     */
    public ST getInstanceOf(String name) {
        if ( !alreadyLoaded ) load();
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
        if ( st==null ) {
            System.err.println("no such template: "+name);
            return ST.BLANK;
        }
        st.enclosingInstance = enclosingInstance;
        return st;
    }

    public CompiledST lookupTemplate(String name) {
        if ( !alreadyLoaded ) load();
        return templates.get(name);
    }

    // TODO: send in start/stop char or line/col so errors can be relative
    public CompiledST defineTemplate(String name, String template) {
        return defineTemplate(name, (LinkedHashMap<String,FormalArgument>)null, template);
    }

    public CompiledST defineTemplate(String name,
                                     List<String> args,
                                     String template)
    {
        LinkedHashMap<String,FormalArgument> margs =
            new LinkedHashMap<String,FormalArgument>();
        for (String a : args) margs.put(a, new FormalArgument(a));
        return defineTemplate(name, margs, template);
    }

    public CompiledST defineTemplate(String name,
                                     String[] args,
                                     String template)
    {
        LinkedHashMap<String,FormalArgument> margs =
            new LinkedHashMap<String,FormalArgument>();
        for (String a : args) margs.put(a, new FormalArgument(a));
        return defineTemplate(name, margs, template);
    }

    public CompiledST defineTemplate(String name,
                                     LinkedHashMap<String,FormalArgument> args,
                                     String template)
    {
        if ( name!=null && (name.length()==0 || name.indexOf('.')>=0) ) {
            throw new IllegalArgumentException("cannot have '.' in template names");
        }
        template = template.trim();
        Compiler c = new Compiler();
        CompiledST code = c.compile(template);
        code.name = name;
        code.formalArguments = args;
        templates.put(name, code);
        if ( args!=null ) { // compile any default args
            for (String a : args.keySet()) {
                FormalArgument fa = args.get(a);
                if ( fa.defaultValue!=null ) {
                    Compiler c2 = new Compiler();
                    fa.compiledDefaultValue = c2.compile(template);
                }
            }
        }
        // define any anonymous subtemplates
        defineAnonSubtemplates(code);

        return code;
    }

    public void defineAnonSubtemplates(CompiledST code) {
        if ( code.compiledSubtemplates!=null ) {
            for (CompiledST sub : code.compiledSubtemplates) {
                templates.put(sub.name, sub);
                defineAnonSubtemplates(sub);
            }
        }
    }
    
    /** Define a map for this group; not thread safe...do not keep adding
     *  these while you reference them.
     */
    public void defineDictionary(String name, Map<String,Object> mapping) {
        dictionaries.put(name, mapping);
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
        if ( !alreadyLoaded ) load();
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

    public void setErrorListener(STErrorListener listener) {
        this.listener = listener;
    }

    public int getCharPositionInLine(Token errToken, STRecognitionException e) {
        RecognitionException re = (RecognitionException)e.getCause();
        int tokenIndex = re.index;
        System.out.println("token index = "+tokenIndex);
        CommonTokenStream tokens = (CommonTokenStream)re.input;
        CommonToken t = (CommonToken)tokens.get(tokenIndex);
        System.out.println("token="+t);
        int charIndex = t.getStartIndex();
        System.out.println("char "+charIndex);
        System.out.println("template starts at "+e.chunk.code.start);

        int i = charIndex; // index into this chunk

        Chunk c = e.chunk;

        while ( c!=null ) {
            i += c.start;
            i += c.code.start;
            c = c.code.enclosingChunk;
        }

        if ( errToken!=null ) {
            System.out.println("error token="+errToken);
            System.out.println("absolute char index in outer template="+i);
            i += errToken.getCharPositionInLine();
            if ( errToken.getType()==GroupParser.STRING ) i++;
            else if ( errToken.getType()==GroupParser.STRING ) i+=2;
        }
        
        System.out.println("char position in line: "+i);
        return i;
    }
    
    // Temp / testing
    public static STGroup loadGroup(String filename) throws Exception {
        STGroup group = new STGroup(filename);
        return group;
    }

}
