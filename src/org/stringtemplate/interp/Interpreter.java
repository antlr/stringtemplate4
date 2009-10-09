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
package org.stringtemplate.interp;

import org.stringtemplate.interp.BytecodeDefinition;
import org.stringtemplate.interp.BytecodeDisassembler;
import org.stringtemplate.STGroup;
import org.stringtemplate.ST;
import org.stringtemplate.ArrayIterator;

import java.io.Writer;
import java.io.IOException;
import java.util.*;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class Interpreter {
    public static final int DEFAULT_OPERAND_STACK_SIZE = 100;
    public static final int DEFAULT_CALL_SIZE = 100;

    /** Operand stack, grows upwards */
    Object[] operands = new Object[DEFAULT_OPERAND_STACK_SIZE];
    int sp = -1;        // stack pointer register
    
    /** Exec st with respect to this group. Once set in ST.toString(),
     *  it should be fixed.  ST has group also.
     */
    STGroup group;
    
    Writer out;

    public boolean trace = false;

    public void exec(ST self) {
        int nameIndex = 0;
        int addr = 0;
        String name = null;
        Object o = null;
        ST st = null;
        int ip = 0;
        byte[] code = self.code.instrs;        // which code block are we executing
        while ( ip < self.code.codeSize ) {
            if ( trace ) trace(self, ip);
            short opcode = code[ip];
            ip++; //jump to next instruction or first byte of operand
            switch (opcode) {
            case BytecodeDefinition.INSTR_LOAD_STR :
                int strIndex = getShort(code, ip);
                ip += 2;
                operands[++sp] = self.code.strings[strIndex];
                break;
            case BytecodeDefinition.INSTR_LOAD_ATTR :
                nameIndex = getShort(code, ip);
                ip += 2;
                name = self.code.strings[nameIndex];
                operands[++sp] = self.getAttribute(name);
                break;
            case BytecodeDefinition.INSTR_LOAD_PROP :
                nameIndex = getShort(code, ip);
                ip += 2;
                o = operands[sp--];
                name = self.code.strings[nameIndex];
                operands[++sp] = rawGetObjectProperty(o, name);
                break;
            case BytecodeDefinition.INSTR_NEW :
                nameIndex = getShort(code, ip);
                ip += 2;
                name = self.code.strings[nameIndex];
                st = group.getInstanceOf(name);
                if ( st == null ) System.err.println("no such template "+name);
                operands[++sp] = st;
                break;
            case BytecodeDefinition.INSTR_STORE_ATTR:
                nameIndex = getShort(code, ip);
                name = self.code.strings[nameIndex];
                ip += 2;
                o = operands[sp--];    // value to store
                st = (ST)operands[sp]; // store arg in ST on top of stack
                st.setAttribute(name, o);
                break;
            case BytecodeDefinition.INSTR_WRITE :
                o = operands[sp--];
                writeObject(o);
                break;
            case BytecodeDefinition.INSTR_MAP :
                name = (String)operands[sp--];
                o = operands[sp--];
                if ( o!=null ) map(self,o,name);
                break;
            case BytecodeDefinition.INSTR_ROT_MAP :
                int n = getShort(code, ip);
                ip += 2;
                List<String> templates = new ArrayList<String>();
                for (int i=n-1; i>=0; i--) templates.add((String)operands[sp-i]);
                sp -= n;
                o = operands[sp--];
                if ( o!=null ) rot_map(self,o,templates);
                break;
            case BytecodeDefinition.INSTR_BR :
                ip = getShort(code, ip);
                break;
            case BytecodeDefinition.INSTR_BRF :
                addr = getShort(code, ip);
                ip += 2;
                o = (String)operands[sp--]; // <if(expr)>...<endif>
                if ( !testAttributeTrue(o) ) ip = addr; // jump
                break;
            case BytecodeDefinition.INSTR_BRT :
                addr = getShort(code, ip);
                ip += 2;
                o = (String)operands[sp--]; // <if(expr)>...<endif>
                if ( testAttributeTrue(o) ) ip = addr; // jump
                break;
            default :
                System.err.println("Invalid bytecode: "+opcode+" @ ip="+(ip-1));
                self.code.dump();
            }
        }
    }

    /** I'd prefer to avoid recursion here but we must write out templates
     *  in order; can't push them all and then continue (gets reverse order).
     */
    protected void writeObject(Object o) {
        if ( o == null ) return;
        if ( o instanceof ST ) {
            exec((ST)o);
            return;
        }
        o = convertAnythingIteratableToIterator(o); // normalize
        try {
            if ( o instanceof Iterator) {
                Iterator it = (Iterator)o;
                while ( it.hasNext() ) {
                    Object iterValue = it.next();
                    if ( iterValue!=null ) {
                        if ( iterValue instanceof ST ) {
                            exec((ST)iterValue);
                        }
                        else {
                            out.write(iterValue.toString());
                        }
                    }
                }
            }
            else {
                out.write(o.toString());
            }
        }
        catch (IOException ioe) {
            System.err.println("can't write "+o);
        }
    }

    protected void map(ST self, Object attr, String name) {
        attr = convertAnythingIteratableToIterator(attr);
        if ( attr instanceof Iterator ) {
            List<ST> mapped = new ArrayList<ST>();
            Iterator iter = (Iterator)attr;
            while ( iter.hasNext() ) {
                Object iterValue = iter.next();
                ST st = group.getEmbeddedInstanceOf(self, name);
                st.setAttribute("it", iterValue);
                mapped.add(st);
            }
            operands[++sp] = mapped;
            //System.out.println("mapped="+mapped);
        }
        else { // map template to single value
            ST st = group.getInstanceOf(name);
            st.setAttribute("it", attr);
            operands[++sp] = st;
        }
    }

    // <names:a,b>
    protected void rot_map(ST self, Object attr, List<String> templates) {
        attr = convertAnythingIteratableToIterator(attr);
        if ( attr instanceof Iterator ) {
            List<ST> mapped = new ArrayList<ST>();
            Iterator iter = (Iterator)attr;
            int i = 0;
            while ( iter.hasNext() ) {
                Object iterValue = iter.next();
                int templateIndex = i % templates.size(); // rotate through
                String name = templates.get(templateIndex);
                ST st = group.getEmbeddedInstanceOf(self, name);
                st.setAttribute("it", iterValue);
                mapped.add(st);
                i++;
            }
            operands[++sp] = mapped;
            //System.out.println("mapped="+mapped);
        }
        else { // if only single value, just apply first template to attribute
            ST st = group.getInstanceOf(templates.get(1));
            st.setAttribute("it", attr);
            operands[++sp] = st;
        }
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

    protected boolean testAttributeTrue(Object a) {
        if ( a==null ) return false;
        if ( a instanceof Boolean ) return ((Boolean)a).booleanValue();
        if ( a instanceof Collection ) return ((Collection)a).size()>0;
        if ( a instanceof Map ) return ((Map)a).size()>0;
        if ( a instanceof Iterator ) return ((Iterator)a).hasNext();
        return true; // any other non-null object, return true--it's present
    }

    protected Object rawGetObjectProperty(Object o, Object property) {
        Class c = o.getClass();
        Object value = null;

        // try getXXX and isXXX properties

        // look up using reflection
        String propertyName = (String)property;
        String methodSuffix = Character.toUpperCase(propertyName.charAt(0))+
            propertyName.substring(1,propertyName.length());
        Method m = getMethod(c,"get"+methodSuffix);
        if ( m==null ) {
            m = getMethod(c, "is"+methodSuffix);
        }
        if ( m != null ) {
            // save to avoid lookup later
            //self.getGroup().cacheClassProperty(c,propertyName,m);
            try {
                value = invokeMethod(m, o, value);
            }
            catch (Exception e) {
                System.err.println("Can't get property "+propertyName+" using method get/is"+methodSuffix+
                    " from "+c.getName()+" instance");
            }
        }
        else {
            // try for a visible field
            try {
                Field f = c.getField(propertyName);
                //self.getGroup().cacheClassProperty(c,propertyName,f);
                try {
                    value = accessField(f, o, value);
                }
                catch (IllegalAccessException iae) {
                    System.err.println("Can't access property "+propertyName+" using method get/is"+methodSuffix+
                        " or direct field access from "+c.getName()+" instance");
                }
            }
            catch (NoSuchFieldException nsfe) {
                System.err.println("Class "+c.getName()+" has no such attribute: "+propertyName+
                    " in template context "+"PUT CALLSTACK HERE");
            }
        }

        return value;
    }

    protected Object accessField(Field f, Object o, Object value) throws IllegalAccessException {
        try {
            // make sure it's accessible (stupid java)
            f.setAccessible(true);
        }
        catch (SecurityException se) {
            ; // oh well; security won't let us
        }
        value = f.get(o);
        return value;
    }

    protected Object invokeMethod(Method m, Object o, Object value) throws IllegalAccessException, InvocationTargetException {
        try {
            // make sure it's accessible (stupid java)
            m.setAccessible(true);
        }
        catch (SecurityException se) {
            ; // oh well; security won't let us
        }
        value = m.invoke(o,(Object[])null);
        return value;
    }

    protected Method getMethod(Class c, String methodName) {
        Method m;
        try {
            m = c.getMethod(methodName, (Class[])null);
        }
        catch (NoSuchMethodException nsme) {
            m = null;
        }
        return m;
    }
    
    protected void trace(ST self, int ip) {
        BytecodeDisassembler dis = new BytecodeDisassembler(self.code.instrs,
                                                            self.code.instrs.length,
                                                            self.code.strings);
        StringBuilder buf = new StringBuilder();
        dis.disassembleInstruction(buf,ip);
        String name = self.name+":";
        if ( self.name==ST.ANON_NAME ) name = "";
        System.out.printf("%-40s",name+buf);
        System.out.print("\tstack=[");
        for (int i = 0; i <= sp; i++) {
            Object o = operands[i];
            printForTrace(o);
        }
        System.out.print(" ], calls=");
        System.out.print(self.getEnclosingInstanceStackString());
        System.out.println();
    }

    protected void printForTrace(Object o) {
        if ( o==null ) return;
        if ( o instanceof ST ) {
            System.out.print(" <<"+((ST)o).name+">>");
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

