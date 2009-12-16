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

import org.stringtemplate.misc.ErrorManager;
import org.stringtemplate.misc.ErrorType;
import org.stringtemplate.misc.Misc;
import org.stringtemplate.misc.ArrayIterator;
import org.stringtemplate.debug.InterpEvent;
import org.stringtemplate.debug.EvalTemplateEvent;
import org.stringtemplate.debug.DebugST;
import org.stringtemplate.compiler.*;
import org.stringtemplate.compiler.Compiler;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;
import java.lang.reflect.Method;
import java.lang.reflect.Field;

public class Interpreter {
    // TODO: enum?
    public static final int OPTION_ANCHOR       = 0;
    public static final int OPTION_FORMAT       = 1;
    public static final int OPTION_NULL         = 2;
    public static final int OPTION_SEPARATOR    = 3;
    public static final int OPTION_WRAP         = 4;

    public static final int DEFAULT_OPERAND_STACK_SIZE = 100;

    public static final Set<String> predefinedAttributes =
        new HashSet<String>() { { add("it"); add("i"); add("i0"); } };

    /** Operand stack, grows upwards */
    Object[] operands = new Object[DEFAULT_OPERAND_STACK_SIZE];
    int sp = -1;  // stack pointer register
    int current_ip = 0;
    int nw = 0;   // how many char written on this template line so far? ("number written" register)

    /** Exec st with respect to this group. Once set in ST.toString(),
     *  it should be fixed. ST has group also.
     */
    STGroup group;
    
    Locale locale;
    
    public boolean trace = false;

    /** Track everything happening in interp if debug */
    protected List<InterpEvent> events;

    public Interpreter(STGroup group) {
        this(group,Locale.getDefault());
    }

    // TODO: remove out and move back to exec; must avoid creating new interp when deugging same st tree 
    public Interpreter(STGroup group, Locale locale) {
        this.group = group;
        this.locale = locale;
        if ( group.debug ) events = new ArrayList<InterpEvent>();
    }

    public int exec(STWriter out, ST self) {
        int start = out.index(); // track char we're about to write
        int prevOpcode = 0;
        int n = 0; // how many char we write out
        int nameIndex = 0;
        int addr = 0;
        String name = null;
        Object o = null, left = null, right = null;
        ST st = null;
        Object[] options = null;
        byte[] code = self.code.instrs;        // which code block are we executing
        int ip = 0;
        while ( ip < self.code.codeSize ) {
            if ( trace ) trace(self, ip);
            short opcode = code[ip];
            current_ip = ip;
            ip++; //jump to next instruction or first byte of operand
            switch (opcode) {
            case Bytecode.INSTR_LOAD_STR :
                int strIndex = getShort(code, ip);
                ip += 2;
                operands[++sp] = self.code.strings[strIndex];
                break;
            case Bytecode.INSTR_LOAD_ATTR :
                nameIndex = getShort(code, ip);
                ip += 2;
                name = self.code.strings[nameIndex];
                o = self.getAttribute(name);
                operands[++sp] = o;
                if ( o==null ) checkNullAttributeAgainstFormalArguments(self, name);
                break;
            case Bytecode.INSTR_LOAD_LOCAL:
                nameIndex = getShort(code, ip);
                ip += 2;
                name = self.code.strings[nameIndex];
                if ( self.attributes!=null ) o = self.attributes.get(name);
                else o = null;
                operands[++sp] = o;
                break;
            case Bytecode.INSTR_LOAD_PROP :
                nameIndex = getShort(code, ip);
                ip += 2;
                o = operands[sp--];
                name = self.code.strings[nameIndex];
                operands[++sp] = getObjectProperty(self, o, name);
                break;
            case Bytecode.INSTR_LOAD_PROP_IND :
                Object propName = operands[sp--];
                o = operands[sp];
                operands[sp] = getObjectProperty(self, o, propName);
                break;
            case Bytecode.INSTR_NEW :
                nameIndex = getShort(code, ip);
                ip += 2;
                name = self.code.strings[nameIndex];
                st = group.getEmbeddedInstanceOf(self, ip, name);
                if ( st == null ) {
                    ErrorManager.runTimeError(self, current_ip, ErrorType.NO_SUCH_TEMPLATE, STGroup.getSimpleName(name));
                    st = ST.BLANK;
                }
                operands[++sp] = st;
                break;
            case Bytecode.INSTR_NEW_IND:
                name = (String)operands[sp--];
                st = group.getEmbeddedInstanceOf(self, ip, name);
                if ( st == null ) {
                    ErrorManager.runTimeError(self, current_ip, ErrorType.NO_SUCH_TEMPLATE, STGroup.getSimpleName(name));
                    st = ST.BLANK;
                }
                operands[++sp] = st;
                break;
            case Bytecode.INSTR_SUPER_NEW :
                nameIndex = getShort(code, ip);
                ip += 2;
                name = self.code.strings[nameIndex];
                CompiledST imported = group.lookupImportedTemplate(name);
                if ( imported==null ) {
                    ErrorManager.runTimeError(self, current_ip, ErrorType.NO_IMPORTED_TEMPLATE, STGroup.getSimpleName(name));
                    operands[++sp] = ST.BLANK;
                    break;
                }
                // TODO: factor into STGroup
                st = imported.nativeGroup.createStringTemplate();
                st.groupThatCreatedThisInstance = group;
                st.code = imported;
                operands[++sp] = st;
                break;
            case Bytecode.INSTR_STORE_ATTR:
                nameIndex = getShort(code, ip);
                name = self.code.strings[nameIndex];
                ip += 2;
                o = operands[sp--];    // value to store
                st = (ST)operands[sp]; // store arg in ST on top of stack
                st.checkAttributeExists(name);
                st.rawSetAttribute(name, o);
                break;
            case Bytecode.INSTR_STORE_SOLE_ARG :
                // unnamed arg, set to sole arg (or first if multiple)
                o = operands[sp--];    // value to store
                st = (ST)operands[sp]; // store arg in ST on top of stack
                setSoleArgument(self, st, o);
                break;
            case Bytecode.INSTR_SET_PASS_THRU :
                st = (ST)operands[sp]; // ST on top of stack
                st.passThroughAttributes = true;
                break;
            case Bytecode.INSTR_STORE_OPTION:
                int optionIndex = getShort(code, ip);
                ip += 2;
                o = operands[sp--];    // value to store
                options = (Object[])operands[sp]; // get options
                options[optionIndex] = o; // store value into options on stack
                break;
            case Bytecode.INSTR_WRITE :
                o = operands[sp--];
                nw = writeObjectNoOptions(out, self, o);
                n += nw;
                break;
			case Bytecode.INSTR_WRITE_OPT :
				options = (Object[])operands[sp--]; // get options
				o = operands[sp--];                 // get option to write
				nw = writeObjectWithOptions(out, self, o, options);
                n += nw;
				break;
            case Bytecode.INSTR_MAP :
                name = (String)operands[sp--];
                o = operands[sp--];
                map(self,o,name);
                break;
            case Bytecode.INSTR_ROT_MAP :
                int nmaps = getShort(code, ip);
                ip += 2;
                List<String> templates = new ArrayList<String>();
                for (int i=nmaps-1; i>=0; i--) templates.add((String)operands[sp-i]);
                sp -= nmaps;
                o = operands[sp--];
                if ( o!=null ) rot_map(self,o,templates);
                break;
            case Bytecode.INSTR_PAR_MAP :
                name = (String)operands[sp--];
                nmaps = getShort(code, ip);
                ip += 2;
                List<Object> exprs = new ArrayList<Object>();
                for (int i=nmaps-1; i>=0; i--) exprs.add(operands[sp-i]);
                sp -= nmaps;
                operands[++sp] = par_map(self,exprs,name);
                break;
            case Bytecode.INSTR_BR :
                ip = getShort(code, ip);
                break;
            case Bytecode.INSTR_BRF :
                addr = getShort(code, ip);
                ip += 2;
                o = operands[sp--]; // <if(expr)>...<endif>
                if ( !testAttributeTrue(o) ) ip = addr; // jump
                break;
            case Bytecode.INSTR_OPTIONS :
                operands[++sp] = new Object[org.stringtemplate.compiler.Compiler.NUM_OPTIONS];
                break;
            case Bytecode.INSTR_LIST :
                operands[++sp] = new ArrayList<Object>();
                break;
            case Bytecode.INSTR_ADD :
                o = operands[sp--];             // pop value
                List<Object> list = (List<Object>)operands[sp]; // don't pop list
                addToList(list, o);
                break;
            case Bytecode.INSTR_TOSTR :
                // replace with string value; early eval
                operands[sp] = toString(self, operands[sp]);
                break;
            case Bytecode.INSTR_FIRST  :
                operands[sp] = first(operands[sp]);
                break;
            case Bytecode.INSTR_LAST   :
                operands[sp] = last(operands[sp]);
                break;
            case Bytecode.INSTR_REST   :
                operands[sp] = rest(operands[sp]);
                break;
            case Bytecode.INSTR_TRUNC  :
                operands[sp] = trunc(operands[sp]);
                break;
            case Bytecode.INSTR_STRIP  :
                operands[sp] = strip(operands[sp]); // TODO: should strip work on strings?
                break;
            case Bytecode.INSTR_TRIM   :
                o = operands[sp--];
                if ( o.getClass() == String.class ) {
                    operands[++sp] = ((String)o).trim();
                }
                else {
                    ErrorManager.runTimeError(self, current_ip, ErrorType.EXPECTING_STRING, "trim", o.getClass().getName());
                    operands[++sp] = o;
                }
                break;
            case Bytecode.INSTR_LENGTH :
                operands[sp] = length(operands[sp]);
                break;
            case Bytecode.INSTR_STRLEN :
                o = operands[sp--];
                if ( o.getClass() == String.class ) {
                    operands[++sp] = ((String)o).length();
                }
                else {
                    ErrorManager.runTimeError(self, current_ip, ErrorType.EXPECTING_STRING, "strlen", o.getClass().getName());
                    operands[++sp] = 0;
                }
                break;
            case Bytecode.INSTR_REVERSE :
				operands[sp] = reverse(operands[sp]);
				break;
			case Bytecode.INSTR_NOT :
				operands[sp] = !testAttributeTrue(operands[sp]);
				break;
			case Bytecode.INSTR_OR :
				right = operands[sp--];
				left = operands[sp--];
				operands[++sp] = testAttributeTrue(left) || testAttributeTrue(right);
				break;
			case Bytecode.INSTR_AND :
				right = operands[sp--];
				left = operands[sp--];
				operands[++sp] = testAttributeTrue(left) && testAttributeTrue(right);
				break;
			case Bytecode.INSTR_INDENT :
				strIndex = getShort(code, ip);
				ip += 2;
				out.pushIndentation(self.code.strings[strIndex]);
				break;
			case Bytecode.INSTR_DEDENT :
				out.popIndentation();
				break;
            case Bytecode.INSTR_NEWLINE :
                try {
                    if ( prevOpcode==Bytecode.INSTR_NEWLINE ||
                         prevOpcode==Bytecode.INSTR_INDENT ||
                         nw>0 )
                    {
                        out.write(Misc.newline);
                    }
                    nw = -1; // indicate nothing written but no WRITE yet
                }
                catch (IOException ioe) {
                    ErrorManager.IOError(self, ErrorType.WRITE_IO_ERROR, ioe);
                }
                break;
            case Bytecode.INSTR_NOOP :
                break;
            case Bytecode.INSTR_POP :
                sp--; // throw away top of stack
                break;
            default :
                ErrorManager.internalError(self, "invalid bytecode @ "+(ip-1)+": "+opcode, null);
                self.code.dump();
            }
            prevOpcode = opcode;            
        }
        if ( group.debug ) {
			int stop = out.index() - 1;
			EvalTemplateEvent e = new EvalTemplateEvent((DebugST)self, start, stop);
			//System.out.println(e);
            events.add(e);
            if ( self.enclosingInstance!=null ) {
                ((DebugST)self.enclosingInstance).interpEvents.add(e);
            }
        }
        return n;
    }

    protected int writeObjectNoOptions(STWriter out, ST self, Object o) {
        int start = out.index(); // track char we're about to write
        int n = writeObject(out, self, o, null);
/*
        if ( group.debug ) {
            Interval templateLocation = self.code.sourceMap[ip];
            int exprStart=templateLocation.a, exprStop=templateLocation.b;
            events.add( new EvalExprEvent((DebugST)self, start, out.index()-1, exprStart, exprStop) );
        }
         */
        return n;
    }

    protected int writeObjectWithOptions(STWriter out, ST self, Object o,
                                         Object[] options)
    {
        int start = out.index(); // track char we're about to write
        // precompute all option values (render all the way to strings)
        String[] optionStrings = null;
        if ( options!=null ) {
            optionStrings = new String[options.length];
            for (int i=0; i< Compiler.NUM_OPTIONS; i++) {
                optionStrings[i] = toString(self, options[i]);
            }
        }
        if ( options!=null && options[OPTION_ANCHOR]!=null ) {
            out.pushAnchorPoint();
        }

        int n = writeObject(out, self, o, optionStrings);
        
        if ( options!=null && options[OPTION_ANCHOR]!=null ) {
            out.popAnchorPoint();
        }
/*
        if ( group.debug ) {
            Interval templateLocation = self.code.sourceMap[ip];
            int exprStart=templateLocation.a, exprStop=templateLocation.b;
            events.add( new EvalExprEvent((DebugST)self, start, out.index()-1, exprStart, exprStop) );
        }
         */
        return n;
    }

    protected int writeObject(STWriter out, ST self, Object o, String[] options) {
        int n = 0;
        if ( o == null ) {
            if ( options!=null && options[OPTION_NULL]!=null ) {
                o = options[OPTION_NULL];
            }
            else return 0;
        }
        if ( o instanceof ST ) {
            ((ST)o).enclosingInstance = self;
            setDefaultArguments((ST)o);
            if ( options!=null && options[OPTION_WRAP]!=null ) {
                // if we have a wrap string, then inform writer it
                // might need to wrap
                try {
                    out.writeWrap(options[OPTION_WRAP]);
                }
                catch (IOException ioe) {
                    ErrorManager.IOError(self, ErrorType.WRITE_IO_ERROR, ioe);
                }
            }
            n = exec(out, (ST)o);
        }
        else {
            o = convertAnythingIteratableToIterator(o); // normalize
            try {
                if ( o instanceof Iterator) n = writeIterator(out, self, o, options);
                else n = writePOJO(out, o, options);
            }
            catch (IOException ioe) {
                ErrorManager.IOError(self, ErrorType.WRITE_IO_ERROR, ioe, o);
            }
        }
        return n;
    }

    protected int writeIterator(STWriter out, ST self, Object o, String[] options) throws IOException {
        if ( o==null ) return 0;
        int n = 0;
        Iterator it = (Iterator)o;
        String separator = null;
        if ( options!=null ) separator = options[OPTION_SEPARATOR];
        boolean seenAValue = false;
        while ( it.hasNext() ) {
            Object iterValue = it.next();
            // Emit separator if we're beyond first value
            boolean needSeparator = seenAValue &&
                separator!=null &&            // we have a separator and
                (iterValue!=null ||           // either we have a value
                 options[OPTION_NULL]!=null); // or no value but null option
            if ( needSeparator ) n += out.writeSeparator(separator);
            int nw = writeObject(out, self, iterValue, options);
            if ( nw > 0 ) seenAValue = true;
            n += nw;
        }
        return n;
    }

    protected int writePOJO(STWriter out, Object o, String[] options) throws IOException {
        String formatString = null;
        if ( options!=null ) formatString = options[OPTION_FORMAT];
        AttributeRenderer r = group.getAttributeRenderer(o.getClass());
        String v = null;
        if ( r!=null ) v = r.toString(o, formatString, locale);
        else v = o.toString();
        int n = 0;
        if ( options!=null && options[OPTION_WRAP]!=null ) {
            n = out.write(v, options[OPTION_WRAP]);
        }
        else {
            n = out.write(v);
        }
        return n;
    }

    protected void map(ST self, Object attr, final String name) {
        rot_map(self, attr, new ArrayList<String>() {{add(name);}});
    }

    // <names:a,b>
    protected void rot_map(ST self, Object attr, List<String> templates) {
        if ( attr==null ) {
            operands[++sp] = null;
            return;
        }
        attr = convertAnythingIteratableToIterator(attr);
        if ( attr instanceof Iterator ) {
            List<ST> mapped = new ArrayList<ST>();
            Iterator iter = (Iterator)attr;
            int i0 = 0;
            int i = 1;
            int ti = 0;
            while ( iter.hasNext() ) {
                Object iterValue = iter.next();
                if ( iterValue == null ) continue;
                int templateIndex = ti % templates.size(); // rotate through
                ti++;
                String name = templates.get(templateIndex);
                ST st = group.getEmbeddedInstanceOf(self, current_ip, name);
                setSoleArgument(self, st, iterValue);
                st.rawSetAttribute("i0", i0);
                st.rawSetAttribute("i", i);
                mapped.add(st);
                i0++;
                i++;
            }
            operands[++sp] = mapped;
            //System.out.println("mapped="+mapped);
        }
        else { // if only single value, just apply first template to attribute
            ST st = group.getInstanceOf(templates.get(0));
            if ( st!=null ) {
                setSoleArgument(self, st, attr);
                st.rawSetAttribute("i0", 0);
                st.rawSetAttribute("i", 1);
                operands[++sp] = st;
            }
            else {
                operands[++sp] = ST.BLANK;
            }
//            map(self, attr, templates.get(1));
        }
    }

    // <names,phones:{n,p | ...}>
    protected ST.AttributeList par_map(ST self, List<Object> exprs, String template) {
        if ( exprs==null || template==null || exprs.size()==0 ) {
            return null; // do not apply if missing templates or empty values
        }
        // make everything iterable
        for (int i = 0; i < exprs.size(); i++) {
            Object attr = exprs.get(i);
            if ( attr!=null ) exprs.set(i, convertAnythingToIterator(attr));
        }

        // ensure arguments line up
        int numAttributes = exprs.size();
        CompiledST code = group.lookupTemplate(template);
        Map formalArguments = code.formalArguments;
        if ( formalArguments==null || formalArguments.size()==0 ) {
            ErrorManager.runTimeError(self, current_ip, ErrorType.MISSING_FORMAL_ARGUMENTS);
            return null;
        }

        Object[] formalArgumentNames = formalArguments.keySet().toArray();
        if ( formalArgumentNames.length != numAttributes ) {
            ErrorManager.runTimeError(self,
                                      current_ip,
                                      ErrorType.MAP_ARGUMENT_COUNT_MISMATCH,
                                      numAttributes,
                                      formalArgumentNames.length);
            // truncate arg list to match smaller size
            int shorterSize = Math.min(formalArgumentNames.length, numAttributes);
            numAttributes = shorterSize;
            Object[] newFormalArgumentNames = new Object[shorterSize];
            System.arraycopy(formalArgumentNames, 0,
                             newFormalArgumentNames, 0,
                             shorterSize);
            formalArgumentNames = newFormalArgumentNames;
        }

        // keep walking while at least one attribute has values

        ST.AttributeList results = new ST.AttributeList();
        int i = 0; // iteration number from 0
        while ( true ) {
            // get a value for each attribute in list; put into ST instance
            int numEmpty = 0;
            ST embedded = group.getEmbeddedInstanceOf(self, current_ip, template);
            embedded.rawSetAttribute("i0", i);
            embedded.rawSetAttribute("i", i+1);
            for (int a = 0; a < numAttributes; a++) {
                Iterator it = (Iterator) exprs.get(a);
                if ( it!=null && it.hasNext() ) {
                    String argName = (String)formalArgumentNames[a];
                    Object iteratedValue = it.next();
                    embedded.checkAttributeExists(argName);
                    embedded.rawSetAttribute(argName, iteratedValue);
                }
                else {
                    numEmpty++;
                }
            }
            if ( numEmpty==numAttributes ) break;
            results.add(embedded);
            i++;
        }
        return results;
    }

    protected void setSoleArgument(ST self, ST st, Object attr) {
        String name = "it";
        int nargs = 0;
        if ( st.code.formalArguments!=null ) {
            nargs = st.code.formalArguments.size();
        }
        if ( nargs > 0 ) {
            if ( nargs != 1 ) {
                ErrorManager.runTimeError(self, current_ip, ErrorType.EXPECTING_SINGLE_ARGUMENT, st, nargs);
            }
            name = st.code.formalArguments.keySet().iterator().next();
        }
        st.rawSetAttribute(name, attr);
    }

    protected void addToList(List<Object> list, Object o) {
        if ( o==null ) return; // [a,b,c] lists ignore null values
        o = Interpreter.convertAnythingIteratableToIterator(o);
        if ( o instanceof Iterator ) {
            // copy of elements into our temp list
            Iterator it = (Iterator)o;
            while (it.hasNext()) list.add(it.next());
        }
        else {
            list.add(o);
        }
    }

    /** Return the first attribute if multiple valued or the attribute
     *  itself if single-valued.  Used in <names:first()>
     */
    public Object first(Object v) {
        if ( v==null ) return null;
        Object r = v;
        v = convertAnythingIteratableToIterator(v);
        if ( v instanceof Iterator ) {
            Iterator it = (Iterator)v;
            if ( it.hasNext() ) {
                r = it.next();
            }
        }
        return r;
    }

    /** Return the last attribute if multiple valued or the attribute
     *  itself if single-valued. Unless it's a list or array, this is pretty
     *  slow as it iterates until the last element.
     */
    public Object last(Object v) {
        if ( v==null ) return null;
        if ( v instanceof List ) return ((List)v).get(((List)v).size()-1);
        else if ( v.getClass().isArray() ) {
            Object[] elems = (Object[])v;
            return elems[elems.length-1];
        }
        Object last = v;
        v = convertAnythingIteratableToIterator(v);
        if ( v instanceof Iterator ) {
            Iterator it = (Iterator)v;
            while ( it.hasNext() ) {
                last = it.next();
            }
        }
        return last;
    }

    /** Return everything but the first attribute if multiple valued
     *  or null if single-valued.
     */
    public Object rest(Object v) {
        if ( v ==null ) return null;
        if ( v instanceof List ) { // optimize list case
            List elems = (List)v;
            if ( elems.size()<=1 ) return null;
            return elems.subList(1, elems.size());
        }
        Object theRest = v; // else iterate and copy 
        v = convertAnythingIteratableToIterator(v);
        if ( v instanceof Iterator ) {
            List a = new ArrayList();
            Iterator it = (Iterator)v;
            if ( !it.hasNext() ) return null; // if not even one value return null
            it.next(); // ignore first value
            while (it.hasNext()) {
                Object o = (Object) it.next();
                if ( o!=null ) a.add(o);
            }
            return a;
        }
        else {
            theRest = null;  // rest of single-valued attribute is null
        }
        return theRest;
    }

    /** Return all but the last element.  trunc(x)=null if x is single-valued. */
    public Object trunc(Object v) {
        if ( v ==null ) return null;
        if ( v instanceof List ) { // optimize list case
            List elems = (List)v;
            if ( elems.size()<=1 ) return null;
            return elems.subList(0, elems.size()-1);
        }
        v = convertAnythingIteratableToIterator(v);
        if ( v instanceof Iterator ) {
            List a = new ArrayList();
            Iterator it = (Iterator) v;
            while (it.hasNext()) {
                Object o = (Object) it.next();
                if ( it.hasNext() ) a.add(o); // only add if not last one
            }
            return a;
        }
        return null; // trunc(x)==null when x single-valued attribute
    }

    /** Return a new list w/o null values. */
    public Object strip(Object v) {
        if ( v ==null ) return null;
        v = convertAnythingIteratableToIterator(v);
        if ( v instanceof Iterator ) {
            List a = new ArrayList();
            Iterator it = (Iterator) v;
            while (it.hasNext()) {
                Object o = (Object) it.next();
                if ( o!=null ) a.add(o);
            }
            return a;
        }
        return v; // strip(x)==x when x single-valued attribute
    }

    /** Return a list with the same elements as v but in reverse order. null
     *  values are NOT stripped out. use reverse(strip(v)) to do that.
     */
    public Object reverse(Object v) {
        if ( v==null ) return null;
        v = convertAnythingIteratableToIterator(v);
        if ( v instanceof Iterator ) {
            List a = new LinkedList();
            Iterator it = (Iterator)v;
            while (it.hasNext()) a.add(0, it.next());
            return a;
        }
        return v;
    }

    /** Return the length of a mult-valued attribute or 1 if it is a
     *  single attribute. If attribute is null return 0.
     *  Special case several common collections and primitive arrays for
     *  speed. This method by Kay Roepke from v3.
     */
    public Object length(Object v) {
        if ( v == null) return 0;
        int i = 1;      // we have at least one of something. Iterator and arrays might be empty.
        if ( v instanceof Map ) i = ((Map)v).size();
        else if ( v instanceof Collection ) i = ((Collection)v).size();
        else if ( v instanceof Object[] ) i = ((Object[])v).length;
        else if ( v instanceof String[] ) i = ((String[])v).length;
        else if ( v instanceof int[] ) i = ((int[])v).length;
        else if ( v instanceof long[] ) i = ((long[])v).length;
        else if ( v instanceof float[] ) i = ((float[])v).length;
        else if ( v instanceof double[] ) i = ((double[])v).length;
        else if ( v instanceof Iterator) {
            Iterator it = (Iterator)v;
            i = 0;
            while ( it.hasNext() ) {
                it.next();
                i++;
            }
        }
        return i;
    }

    public List<InterpEvent> getEvents() { return events; }
    
    protected String toString(ST self, Object value) {
        if ( value!=null ) {
            if ( value.getClass()==String.class ) return (String)value;
            // if ST, make sure it evaluates with enclosing template as self
            if ( value instanceof ST ) ((ST)value).enclosingInstance = self;
            // if not string already, must evaluate it
            StringWriter sw = new StringWriter();
            /*
            Interpreter interp = new Interpreter(group, new NoIndentWriter(sw), locale);
            interp.writeObjectNoOptions(self, value, -1, -1);
            */
            writeObjectNoOptions(new NoIndentWriter(sw), self, value);

            return sw.toString();
        }
        return null;
    }
    
    protected static Object convertAnythingIteratableToIterator(Object o) {
        Iterator iter = null;
        if ( o == null ) return null;
        if ( o instanceof Collection)      iter = ((Collection)o).iterator();
        else if ( o.getClass().isArray() ) iter = new ArrayIterator(o);
        else if ( o instanceof Map)        iter = ((Map)o).values().iterator();
        else if ( o instanceof Iterator )  iter = (Iterator)o;
        if ( iter==null ) return o;
        return iter;
    }

    protected static Iterator convertAnythingToIterator(Object o) {
        o = convertAnythingIteratableToIterator(o);
        if ( o instanceof Iterator ) return (Iterator)o;
        List singleton = new ST.AttributeList(1);
        singleton.add(o);
        return singleton.iterator();
    }
    
    protected boolean testAttributeTrue(Object a) {
        if ( a==null ) return false;
        if ( a instanceof Boolean ) return ((Boolean)a).booleanValue();
        if ( a instanceof Collection ) return ((Collection)a).size()>0;
        if ( a instanceof Map ) return ((Map)a).size()>0;
        if ( a instanceof Iterator ) return ((Iterator)a).hasNext();
        return true; // any other non-null object, return true--it's present
    }

    protected Object getObjectProperty(ST self, Object o, Object property) {
        if ( o==null || property==null ) {
            // TODO: throw Ill arg if they want
            return null;
        }

        Object value = null;

        if (o instanceof Map) {
            Map map = (Map)o;
            if ( value == STGroup.DICT_KEY ) value = property;
            else if ( property.equals("keys") ) value = map.keySet();
            else if ( property.equals("values") ) value = map.values();
            else if ( map.containsKey(property) ) value = map.get(property);
            else if ( map.containsKey(toString(self, property)) ) {
                // if we can't find the key, toString it
                value = map.get(toString(self, property));
            }
            else value = map.get(STGroup.DEFAULT_KEY); // not found, use default
            if ( value == STGroup.DICT_KEY ) {
                value = property;
            }
            return value;
        }

        Class c = o.getClass();

        // try getXXX and isXXX properties

        // look up using reflection
        String propertyName = (String)property;
        String methodSuffix = Character.toUpperCase(propertyName.charAt(0))+
            propertyName.substring(1,propertyName.length());
        Method m = Misc.getMethod(c,"get"+methodSuffix);
        if ( m==null ) {
            m = Misc.getMethod(c, "is"+methodSuffix);
        }
        if ( m != null ) {
            // save to avoid lookup later
            //self.getGroup().cacheClassProperty(c,propertyName,m);
            try {
                value = Misc.invokeMethod(m, o, value);
            }
            catch (Exception e) {
                //ErrorManager.runTimeError(self, ErrorType.NO_SUCH_PROPERTY, e, m);
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
                    //ErrorManager.runTimeError(self, ErrorType.NO_SUCH_PROPERTY, iae, f);
                }
            }
            catch (NoSuchFieldException nsfe) {
                //ErrorManager.runTimeError(self, ErrorType.NO_SUCH_PROPERTY, c, propertyName);
            }
        }

        return value;
    }

    /** Set any default argument values that were not set by the
     *  invoking template or by setAttribute directly.  Note
     *  that the default values may be templates.  Their evaluation
     *  context is the template itself and, hence, can see attributes
     *  within the template, any arguments, and any values inherited
     *  by the template.
     */
    public void setDefaultArguments(ST invokedST) {
        if ( invokedST.code.formalArguments==null || invokedST.code.formalArguments.size()==0 ) return;
        for (FormalArgument arg : invokedST.code.formalArguments.values()) {
            // if no value for attribute and default arg, inject default arg into self
            if ( (invokedST.attributes==null || invokedST.attributes.get(arg.name)==null) &&
                 arg.compiledDefaultValue!=null )
            {
                ST defaultArgST = group.createStringTemplate();
                defaultArgST.groupThatCreatedThisInstance = group;
                defaultArgST.code = arg.compiledDefaultValue;
                System.out.println("setting def arg "+arg.name+" to "+defaultArgST);
                // If default arg is template with single expression
                // wrapped in parens, x={<(...)>}, then eval to string
                // rather than setting x to the template for later
                // eval.
                String defArgTemplate = arg.defaultValueToken.getText();
                if ( defArgTemplate.startsWith("{<(") && defArgTemplate.endsWith(")>}") ) {
                    invokedST.rawSetAttribute(arg.name, toString(invokedST, defaultArgST));
                }
                else {
                    invokedST.rawSetAttribute(arg.name, defaultArgST);
                }
            }
        }
    }

    /** A reference to an attribute with no value must be compared against
     *  the formal parameters up the enclosing chain to see if it exists;
     *  if it exists all is well, but if not, record an error.
     *
     *  Don't generate error if template has no formal arguments.
     */
    protected void checkNullAttributeAgainstFormalArguments(ST self, String name) {
        if ( self.code.formalArguments == FormalArgument.UNKNOWN ) return;
        
        ST p = self;
        while ( p!=null ) {
            if ( p.code.formalArguments!=null && p.code.formalArguments.get(name)!=null ) {
                // found it; no problems, just return
                return;
            }
            p = p.enclosingInstance;
        }

        ErrorManager.runTimeError(self, current_ip, ErrorType.NO_ATTRIBUTE_DEFINITION, name);
    }
    
    protected void trace(ST self, int ip) {
        BytecodeDisassembler dis = new BytecodeDisassembler(self.code);
        StringBuilder buf = new StringBuilder();
        dis.disassembleInstruction(buf,ip);
        String name = self.code.name+":";
        if ( self.code.name==ST.UNKNOWN_NAME) name = "";
        System.out.printf("%-40s",name+buf);
        System.out.print("\tstack=[");
        for (int i = 0; i <= sp; i++) {
            Object o = operands[i];
            printForTrace(o);
        }
        System.out.print(" ], calls=");
        System.out.print(self.getEnclosingInstanceStackString());
        System.out.print(", sp="+sp+", nw="+nw);
        System.out.println();
    }

    protected void printForTrace(Object o) {
        if ( o instanceof ST ) {
            if ( ((ST)o).code==null ) System.out.print("bad-template()");
            else System.out.print(" "+((ST)o).code.name+"()");
            return;
        }
        o = convertAnythingIteratableToIterator(o);
        if ( o instanceof Iterator ) {
            Iterator it = (Iterator)o;
            System.out.print(" [");
            while ( it.hasNext() ) {
                Object iterValue = it.next();
                printForTrace(iterValue);
            }
            System.out.print(" ]");
        }
        else {
            System.out.print(" "+o);
        }
    }

    public static int getInt(byte[] memory, int index) {
        int b1 = memory[index++]&0xFF; // mask off sign-extended bits
        int b2 = memory[index++]&0xFF;
        int b3 = memory[index++]&0xFF;
        int b4 = memory[index++]&0xFF;
        int word = b1<<(8*3) | b2<<(8*2) | b3<<(8*1) | b4;
        return word;
    }

    public static int getShort(byte[] memory, int index) {
        int b1 = memory[index++]&0xFF; // mask off sign-extended bits
        int b2 = memory[index++]&0xFF;
        int word = b1<<(8*1) | b2;
        return word;
    }

}

