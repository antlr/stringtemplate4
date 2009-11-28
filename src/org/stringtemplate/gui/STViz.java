package org.stringtemplate.gui;

import org.stringtemplate.*;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/** */
public class STViz {
    public static ST currentST;

    public static void main(String[] args) throws IOException {
        String templates =
            "method(type,name,args,stats) ::= <<\n" +
            "public <type> <name>(<args:{a| int <a>}; separator=\", \">) {\n" +
            "    <stats;separator=\"\\n\">\n" +
            "}\n" +
            ">>\n"+
            "assign(a,b) ::= \"<a> = <b>;\"\n"+
            "return(x) ::= <<return <x>;>>\n";

        String tmpdir = System.getProperty("java.io.tmpdir");
        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
        ST st = group.getInstanceOf("method");
        st.code.dump();
        st.add("type", "float");
        st.add("name", "foo");
        st.add("args", new String[] {"x", "y", "z"});
        ST s1 = group.getInstanceOf("assign");
        s1.add("a", "x");
        s1.add("b", "y");
        ST s2 = group.getInstanceOf("assign");
        s2.add("a", "y");
        s2.add("b", "z");
        ST s3 = group.getInstanceOf("return");
        s3.add("x", "3.14159");
        st.add("stats", s1);
        st.add("stats", s2);
        st.add("stats", s3);

        currentST = st;

        StringWriter sw = new StringWriter();
        Interpreter interp = new Interpreter(group, new AutoIndentWriter(sw));
        interp.setDebug(true);
        interp.exec(st);
        List<Interpreter.DebugEvent> events = interp.getEvents();

        final STViewFrame m = new STViewFrame();
        updateStack(currentST, m);
        updateAttributes(currentST, m);

        final JTreeSTModel tmodel = new JTreeSTModel(st);
        m.tree.setModel(tmodel);
        m.tree.addTreeSelectionListener(
            new TreeSelectionListener() {
                public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
                    currentST = (ST)m.tree.getLastSelectedPathComponent();
                    updateStack(currentST, m);
                    updateAttributes(currentST, m);
                    if ( currentST.enclosingInstance!=null ) {
                        int i = tmodel.getIndexOfChild(currentST.enclosingInstance, currentST);
                        Interpreter.DebugEvent e = currentST.enclosingInstance.events.get(i);
                        //m.template.setText(currentST.code.template.substring(e.start, e.stop));
                        m.template.setText(currentST.code.getTemplate());
                    }
                    else {
                        m.template.setText(currentST.code.getTemplate());
                    }
                }
            }
        );

        m.output.setText(sw.toString());
        m.template.setText(st.code.getTemplate());
        m.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        m.pack();
        m.setVisible(true);
    }

    protected static void updateAttributes(ST st, STViewFrame m) {
        DefaultListModel attrModel = new DefaultListModel();
        Map<String,Object> attrs = st.getAttributes();
        for (String a : attrs.keySet()) {
            attrModel.addElement(a+" = "+attrs.get(a));
        }
        m.attributes.setModel(attrModel);
    }

    protected static void updateStack(ST st, STViewFrame m) {
        DefaultListModel stackModel = new DefaultListModel();
        List<ST> stack = st.getEnclosingInstanceStack(false);
        for (ST s : stack) stackModel.addElement(s.getName());
        m.stack.setModel(stackModel);
        m.stack.addListSelectionListener(
            new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    System.out.println("touched "+e.getFirstIndex());
                }
            }
        );
    }

    public static List<String> readLines(String file) throws IOException {
        Reader r = new FileReader(file);
        BufferedReader br = new BufferedReader(r);
        final List<String> lines = new ArrayList<String>();
        String line = br.readLine();
        while (line != null) {
            lines.add(line);
            line = br.readLine();
        }
        br.close();
        return lines;
    }

    // Seriously: why isn't this built in to java?
    public static String join(Iterator iter, String separator) {
        StringBuilder buf = new StringBuilder();
        while ( iter.hasNext() ) {
            buf.append(iter.next());
            if ( iter.hasNext() ) {
                buf.append(separator);
            }
        }
        return buf.toString();
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
    
}
