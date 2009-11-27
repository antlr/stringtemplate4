package org.stringtemplate.gui;

import org.stringtemplate.*;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/** */
public class STViz {
    public static void main(String[] args) throws IOException {
        String templates =
            "t(x) ::= <<[<u()>]>>\n"+
            "u() ::= << <x> >>\n";

        String tmpdir = System.getProperty("java.io.tmpdir");
        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
        ST st = group.getInstanceOf("t");
        st.code.dump();
        st.add("x", "foo");
        st.add("y", "bar");

        StringWriter sw = new StringWriter();
        Interpreter interp = new Interpreter(group, new AutoIndentWriter(sw));
        interp.setDebug(true);
        interp.exec(st);
        List<Interpreter.DebugEvent> events = interp.getEvents();

        STViewFrame m = new STViewFrame();
        DefaultListModel stackModel = new DefaultListModel();
        List<ST> stack = st.getEnclosingInstanceStack();
        for (ST s : stack) stackModel.addElement(s.getName());
        m.stack.setModel(stackModel);
        m.stack.addListSelectionListener(
            new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    System.out.println("touched "+e.getFirstIndex());
                }
            }
        );

        DefaultListModel attrModel = new DefaultListModel();
        Map<String,Object> attrs = st.getAttributes();
        for (String a : attrs.keySet()) {
            attrModel.addElement(a+" = "+attrs.get(a));
        }
        m.attributes.setModel(attrModel);

        m.output.setText(sw.toString());
        m.template.setText(st.code.template);
        m.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        m.pack();
        m.setVisible(true);
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
