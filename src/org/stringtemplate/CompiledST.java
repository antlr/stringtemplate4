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

import java.util.LinkedHashMap;
import java.util.List;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;

public class CompiledST {
    protected String name;

    /** The original, immutable pattern (not really used again after
     *  initial "compilation"). Useful for debugging.
     */
    public String template;

    public int embeddedStart=-1, embeddedStop=-1; // if subtemplate

    protected LinkedHashMap<String,FormalArgument> formalArguments;

    protected List<CompiledST> implicitlyDefinedTemplates;

    /** The group that holds this ST definition.  We use it to initiate
     *  interpretation via ST.toString().  From there, it becomes field 'group'
     *  in interpreter and is fixed until rendering completes.
     */
    public STGroup nativeGroup = STGroup.defaultGroup;

    /** Does this template come from a <@region>...<@end> embedded in
     *  another template?
     */
    protected boolean isRegion;    

    /** If someone refs <@r()> in template t, an implicit
     *
     *   @t.r() ::= ""
     *
     *  is defined, but you can overwrite this def by defining your
     *  own.  We need to prevent more than one manual def though.  Between
     *  this var and isEmbeddedRegion we can determine these cases.
     */
    protected ST.RegionType regionDefType;

    public String[] strings;
    public byte[] instrs;        // byte-addressable code memory.
    public int codeSize;

    public String getTemplate() { return template; }
/*        if ( embeddedStart>=0 ) return template.substring(embeddedStart, embeddedStop);
        return template;
    }
    */

	public boolean isSubtemplate() { return name.startsWith("/"+ST.SUBTEMPLATE_PREFIX); } 

    public String instrs() {
        BytecodeDisassembler dis = new BytecodeDisassembler(instrs,
                                                            codeSize,
                                                            strings);
        return dis.instrs();
    }

    public void dump() {
        BytecodeDisassembler dis = new BytecodeDisassembler(instrs,
                                                            codeSize,
                                                            strings);
        System.out.println(dis.disassemble());
        System.out.println("Strings:");
        System.out.println(dis.strings());
    }

    public String disasm() {
        BytecodeDisassembler dis = new BytecodeDisassembler(instrs,
                                                            codeSize,
                                                            strings);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        pw.println(dis.disassemble());
        pw.println("Strings:");
        pw.println(dis.strings());
        pw.close();
        return sw.toString();
    }
}
