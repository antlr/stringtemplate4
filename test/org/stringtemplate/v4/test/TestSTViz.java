package org.stringtemplate.v4.test;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import org.stringtemplate.v4.STGroupString;
import org.stringtemplate.v4.gui.STViz;

import java.io.IOException;

import static org.stringtemplate.v4.test.BaseTest.writeFile;

public class TestSTViz
{
    public static void main(String[] args) throws IOException { // test rig
        if ( args.length>0 && args[0].equals("1") ) {
            test1();
        }
        else if ( args.length>0 && args[0].equals("2") ) {
            test2();
        }
        else if ( args.length>0 && args[0].equals("3") ) {
            test3();
        }
        else if ( args.length>0 && args[0].equals("4") ) {
            test4();
        }
    }

    public static void test1() throws IOException { // test rig
        String templates = "method(type,name,locals,args,stats) ::= <<\n"
                           +"public <type> <name>(<args:{a| int <a>}; separator=\", \">) {\n"
                           +"    <if(locals)>int locals[<locals>];<endif>\n"+"    <stats;separator=\"\\n\">\n"+"}\n"
                           +">>\n"+"assign(a,b) ::= \"<a> = <b>;\"\n"+"return(x) ::= <<return <x>;>>\n"
                           +"paren(x) ::= \"(<x>)\"\n";

        String tmpdir = System.getProperty("java.io.tmpdir");
        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
        ST st = group.getInstanceOf("method");
        st.impl.dump();
        st.add("type", "float");
        st.add("name", "foo");
        st.add("locals", 3);
        st.add("args", new String[] { "x", "y", "z" });
        ST s1 = group.getInstanceOf("assign");
        ST paren = group.getInstanceOf("paren");
        paren.add("x", "x");
        s1.add("a", paren);
        s1.add("b", "y");
        ST s2 = group.getInstanceOf("assign");
        s2.add("a", "y");
        s2.add("b", "z");
        ST s3 = group.getInstanceOf("return");
        s3.add("x", "3.14159");
        st.add("stats", s1);
        st.add("stats", s2);
        st.add("stats", s3);

        STViz viz = st.inspect();
        System.out.println(st.render()); // should not mess up ST event lists
    }

    public static void test2() throws IOException { // test rig
        String templates = "t1(q1=\"Some\\nText\") ::= <<\n"+"<q1>\n"+">>\n"+"\n"+"t2(p1) ::= <<\n"+"<p1>\n"+">>\n"+"\n"
                           +"main() ::= <<\n"+"START-<t1()>-END\n"+"\n"+"START-<t2(p1=\"Some\\nText\")>-END\n"+">>\n";

        String tmpdir = System.getProperty("java.io.tmpdir");
        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
        ST st = group.getInstanceOf("main");
        STViz viz = st.inspect();
    }

    public static void test3() throws IOException {
        String templates = "main() ::= <<\n"+"Foo: <{bar};format=\"lower\">\n"+">>\n";

        String tmpdir = System.getProperty("java.io.tmpdir");
        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
        ST st = group.getInstanceOf("main");
        st.inspect();
    }

    public static void test4() throws IOException {
        String templates =
            "main(t) ::= <<\n"+"hi: <t>\n"+">>\n"+"foo(x,y={hi}) ::= \"<bar(x,y)>\"\n"+"bar(x,y) ::= << <y> >>\n"
            +"ignore(m) ::= \"<m>\"\n";

        STGroup group = new STGroupString(templates);
        ST st = group.getInstanceOf("main");
        ST foo = group.getInstanceOf("foo");
        st.add("t", foo);
        ST ignore = group.getInstanceOf("ignore");
        ignore.add("m", foo); // embed foo twice!
        st.inspect();
        st.render();
    }
}
