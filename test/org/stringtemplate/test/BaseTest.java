package org.stringtemplate.test;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.Token;
import org.antlr.runtime.UnbufferedTokenStream;
import org.antlr.runtime.CommonTokenStream;
import org.junit.Before;
import org.stringtemplate.misc.Misc;
import org.stringtemplate.STGroup;
import org.stringtemplate.compiler.STLexer;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class BaseTest {
    public static final String tmpdir = System.getProperty("java.io.tmpdir");
    public static final String newline = Misc.newline;

    @Before
    public void setUp() {
        STGroup.defaultGroup = new STGroup();
        org.stringtemplate.compiler.Compiler.subtemplateCount = 0;
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
            new STLexer(new ANTLRStringStream(template),
                        delimiterStartChar, delimiterStopChar);
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
    
    protected String getRandomDir() {
        String randomDir = tmpdir+"dir"+String.valueOf((int)(Math.random()*100000));
        File f = new File(randomDir);
        f.mkdirs();
        return randomDir;
    }
}
