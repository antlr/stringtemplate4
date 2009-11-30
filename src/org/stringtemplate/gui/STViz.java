package org.stringtemplate.gui;

import org.stringtemplate.*;
import org.stringtemplate.debug.InterpEvent;
import org.stringtemplate.debug.AddAttributeEvent;
import org.stringtemplate.debug.EvalTemplateEvent;
import org.stringtemplate.debug.DebugST;
import org.stringtemplate.misc.Misc;

import javax.swing.*;
import javax.swing.tree.TreePath;
import javax.swing.text.Highlighter;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.event.*;
import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.awt.*;

public class STViz {
	public DebugST currentST;
	public List<InterpEvent> allEvents;
	public JTreeSTModel tmodel;

    public STViz(DebugST root, String output, final List<InterpEvent> allEvents) {
        // TODO move all this to JFrame so i can return it.
        currentST = root;

        this.allEvents = allEvents;

        final STViewFrame m = new STViewFrame();
        updateStack(currentST, m);
        updateAttributes(currentST, m);

        tmodel = new JTreeSTModel(currentST);
        m.tree.setModel(tmodel);
        m.tree.addTreeSelectionListener(
            new TreeSelectionListener() {
                public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
                    /*
		if ( o instanceof Wrapper ) return ((Wrapper)o).st;
                    else return (DebugST)o;
                     */
                    currentST = ((JTreeSTModel.Wrapper) m.tree.getLastSelectedPathComponent()).st;
                    update(m);
                }
            }
        );


        m.output.setText(output);

        m.template.setText(currentST.code.getTemplate());
        m.bytecode.setText(currentST.code.disasm());

        updateStack(currentST, m);

        CaretListener caretListenerLabel = new CaretListener() {
            public void caretUpdate(CaretEvent e) {
                int dot = e.getDot();
                InterpEvent de = findEventAtOutputLocation(allEvents, dot);
                if ( de==null ) currentST = tmodel.root.st;
                else currentST = de.self;
                update(m);
            }
        };

        m.output.addCaretListener(caretListenerLabel);

        m.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        m.pack();
        m.setSize(800,600);
        m.topSplitPane.setBorder(null);
        //m.topSplitPane.setResizeWeight(0.15);
        m.bottomSplitPane.setBorder(null);
        //m.bottomSplitPane.setResizeWeight(0.15);
        m.treeScrollPane.setPreferredSize(new Dimension(120,400));
        m.bottomSplitPane.setPreferredSize(new Dimension(120,200));
        m.setVisible(true);
    }

	private void update(STViewFrame m) {
		updateStack(currentST, m);
		updateAttributes(currentST, m);
        m.bytecode.setText(currentST.code.disasm());


		List<ST> pathST = currentST.getEnclosingInstanceStack(true);
		Object[] path = new Object[pathST.size()];
		int j = 0;
		for (ST s : pathST) path[j++] = new JTreeSTModel.Wrapper((DebugST)s);

		m.tree.setSelectionPath(new TreePath(path));

		if ( currentST.enclosingInstance!=null ) {
			int i = tmodel.getIndexOfChild((DebugST)currentST.enclosingInstance, currentST);
			InterpEvent e = ((DebugST)currentST.enclosingInstance).interpEvents.get(i);
			if ( e instanceof EvalTemplateEvent) {
				String txt = currentST.code.getTemplate();
				m.template.setText(txt);
				if ( currentST.isSubtemplate() ) {
					highlight(m.template, currentST.code.embeddedStart, currentST.code.embeddedStop);
				}
				highlight(m.output, e.start, e.stop);
			}
			else {
				m.template.setText(currentST.code.getTemplate());
			}
		}
		else {
			String txt = currentST.code.getTemplate();
			m.template.setText(txt);
			highlight(m.output, 0, txt.length());
		}
	}

	protected void highlight(JTextComponent comp, int i, int j) {
		Highlighter highlighter = comp.getHighlighter();
		highlighter.removeAllHighlights();

		try {
			highlighter.addHighlight(i, j+1, DefaultHighlighter.DefaultPainter);
		}
		catch (BadLocationException ble) {
			System.out.println("can't highlight");
		}
	}

	protected void updateAttributes(final DebugST st, final STViewFrame m) {
		System.out.println("add events="+ st.addAttrEvents);
		final DefaultListModel attrModel = new DefaultListModel();
		final Map<String,Object> attrs = st.getAttributes();
		/*
		class Pair {
			public Object a, b;
			public Pair(Object a, Object b) {this.a=a; this.b=b;}
			public String toString() { return a.toString()+" = "+b; }
		}
		 */
		if ( attrs!=null ) {
            for (String a : attrs.keySet()) {
            if (st.addAttrEvents !=null ) {
                List<AddAttributeEvent> events = st.addAttrEvents.get(a);
                StringBuilder locations = new StringBuilder();
                int i = 0;
                if ( events!=null ) {
                    for (AddAttributeEvent ae : events) {
                        if ( i>0 ) locations.append(", ");
                        locations.append(ae.getFileName()+":"+ae.getLine());
                        i++;
                    }
                }
                attrModel.addElement(a+" = "+attrs.get(a)+" @ "+locations.toString());
            }
            else {
                attrModel.addElement(a+" = "+attrs.get(a));
            }
        }
        }
        m.attributes.setModel(attrModel);
		m.attributes.addListSelectionListener(
			new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					int minIndex = m.attributes.getMinSelectionIndex();
					int maxIndex = m.attributes.getMaxSelectionIndex();
					for (int i = minIndex; i <= maxIndex; i++) {
						if (m.attributes.isSelectedIndex(i)) {
							//System.out.println("index="+i);
						}
					}
				}
			}
		);
	}

	protected void updateStack(DebugST st, STViewFrame m) {
		List<ST> stack = st.getEnclosingInstanceStack(true);
		m.setTitle("STViz - ["+ Misc.join(stack.iterator()," ")+"]");
        // also do source stack
        StackTraceElement[] trace = st.newSTEvent.stack.getStackTrace();
        StringWriter sw = new StringWriter();
        for (StackTraceElement e : trace) {
            sw.write(e.toString()+"\n");
        }
        //PrintWriter pw = new PrintWriter(sw);
        //st.newSTEvent.printStackTrace(pw);
        //pw.close();
        m.stacktrace.setText(sw.toString());
    }

    public InterpEvent findEventAtOutputLocation(List<InterpEvent> events,
                                                 int charIndex)
    {
        for (InterpEvent e : events) {
            if ( charIndex>=e.start && charIndex<=e.stop ) return e;
        }
        return null;
	}

    public static void main(String[] args) throws IOException { // test rig
        String templates =
            "method(type,name,args,stats) ::= <<\n" +
                "public <type> <name>(<args:{a| int <a>}; separator=\", \">) {\n" +
                "    <if(locals)>int locals[<locals>];<endif>\n"+
                "    <stats;separator=\"\\n\">\n" +
                "}\n" +
                ">>\n"+
                "assign(a,b) ::= \"<a> = <b>;\"\n"+
                "return(x) ::= <<return <x>;>>\n" +
                "paren(x) ::= \"(<x>)\"\n";

        String tmpdir = System.getProperty("java.io.tmpdir");
        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
        group.setDebug(true);
        DebugST st = (DebugST)group.getInstanceOf("method");
        st.code.dump();
        st.add("type", "float");
        st.add("name", "foo");
        st.add("locals", 3);
        st.add("args", new String[] {"x", "y", "z"});
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

        st.inspect();
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
