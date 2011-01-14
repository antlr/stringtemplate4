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
package org.stringtemplate.v4.test;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.Token;
import org.junit.Before;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.compiler.Compiler;
import org.stringtemplate.v4.compiler.STLexer;
import org.stringtemplate.v4.misc.Misc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class BaseTest {
    public static final String tmpdir = System.getProperty("java.io.tmpdir");
    public static final String newline = Misc.newline;

    @Before
    public void setUp() {
        STGroup.defaultGroup = new STGroup();
        Compiler.subtemplateCount = 0;
		STGroup.debug = false;
    }

    public static void writeFile(String dir, String fileName, String content) {
		try {
			File f = new File(dir, fileName);
            if ( !f.getParentFile().exists() ) f.getParentFile().mkdirs();
			FileWriter w = new FileWriter(f);
			BufferedWriter bw = new BufferedWriter(w);
			bw.write(content);
			bw.close();
			w.close();
		}
		catch (IOException ioe) {
			System.err.println("can't write file");
			ioe.printStackTrace(System.err);
		}
	}

    public void checkTokens(String template, String expected) {
        checkTokens(template, expected, '<', '>');
    }


    public void checkTokens(String template, String expected,
                            char delimiterStartChar, char delimiterStopChar)
    {
        STLexer lexer =
            new STLexer(STGroup.DEFAULT_ERR_MGR,
						new ANTLRStringStream(template),
						null,
						delimiterStartChar,
						delimiterStopChar);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		StringBuffer buf = new StringBuffer();
		buf.append("[");
		int i = 1;
        Token t = tokens.LT(i);
        while ( t.getType()!=Token.EOF ) {
            if ( i>1 ) buf.append(", ");
            buf.append(t);
            i++;
            t = tokens.LT(i);
        }
        buf.append("]");
        String result = buf.toString();
        assertEquals(expected, result);
    }

    public static class User {
        public int id;
        public String name;
        public User(int id, String name) { this.id = id; this.name = name; }
		public boolean isManager() { return true; }
		public boolean hasParkingSpot() { return true; }
        public String getName() { return name; }
    }

    public static class HashableUser extends User {
        public HashableUser(int id, String name) { super(id, name); }
        public int hashCode() {
            return id;
        }

        public boolean equals(Object o) {
            if ( o instanceof HashableUser ) {
                HashableUser hu = (HashableUser)o;
                return this.id == hu.id && this.name.equals(hu.name);
            }
            return false;
        }
	}

    public static String getRandomDir() {
        String randomDir = tmpdir+"dir"+String.valueOf((int)(Math.random()*100000));
        File f = new File(randomDir);
        f.mkdirs();
        return randomDir;
    }
}
