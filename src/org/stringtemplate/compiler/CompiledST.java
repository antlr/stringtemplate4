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
package org.stringtemplate.compiler;

import org.stringtemplate.compiler.FormalArgument;
import org.stringtemplate.STGroup;
import org.stringtemplate.ST;
import org.stringtemplate.misc.Interval;

import java.util.LinkedHashMap;
import java.util.List;
import java.io.PrintWriter;
import java.io.StringWriter;

public class CompiledST {
    public String name;

    /** The original, immutable pattern (not really used again after
     *  initial "compilation"). Useful for debugging.
     */
    public String template;

    public int embeddedStart=-1, embeddedStop=-1; // if subtemplate

    public LinkedHashMap<String, FormalArgument> formalArguments = FormalArgument.UNKNOWN;

    public List<CompiledST> implicitlyDefinedTemplates;

    /** The group that holds this ST definition.  We use it to initiate
     *  interpretation via ST.toString().  From there, it becomes field 'group'
     *  in interpreter and is fixed until rendering completes.
     */
    public STGroup nativeGroup = STGroup.defaultGroup;

    /** Does this template come from a <@region>...<@end> embedded in
     *  another template?
     */
    public boolean isRegion;

    public boolean isSubtemplate;

    /** If someone refs <@r()> in template t, an implicit
     *
     *   @t.r() ::= ""
     *
     *  is defined, but you can overwrite this def by defining your
     *  own.  We need to prevent more than one manual def though.  Between
     *  this var and isEmbeddedRegion we can determine these cases.
     */
    public ST.RegionType regionDefType;

    public String[] strings;
    public byte[] instrs;        // byte-addressable code memory.
    public int codeSize;
    public Interval[] sourceMap; // maps IP to range in template pattern

    public String getTemplate() { return template; }

	public boolean isSubtemplate() { return isSubtemplate; } 

    public String instrs() {
        BytecodeDisassembler dis = new BytecodeDisassembler(this);
        return dis.instrs();
    }

    public void dump() {
        BytecodeDisassembler dis = new BytecodeDisassembler(this);
        System.out.println(dis.disassemble());
        System.out.println("Strings:");
        System.out.println(dis.strings());
        System.out.println("Bytecode to template map:");
        System.out.println(dis.sourceMap());
    }

    public String disasm() {
        BytecodeDisassembler dis = new BytecodeDisassembler(this);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        pw.println(dis.disassemble());
        pw.println("Strings:");
        pw.println(dis.strings());
        pw.println("Bytecode to template map:");
        pw.println(dis.sourceMap());
        pw.close();
        return sw.toString();
    }
}
