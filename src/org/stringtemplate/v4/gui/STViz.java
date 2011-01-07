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
package org.stringtemplate.v4.gui;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import org.stringtemplate.v4.debug.AddAttributeEvent;
import org.stringtemplate.v4.debug.DebugST;
import org.stringtemplate.v4.debug.EvalTemplateEvent;
import org.stringtemplate.v4.debug.InterpEvent;
import org.stringtemplate.v4.misc.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.io.*;
import java.util.List;
import java.util.Map;

public class STViz {
	public DebugST currentST;
	public List<InterpEvent> allEvents;
	public JTreeSTModel tmodel;
    public List<STMessage> errors;
	public ErrorManager errMgr;

    public STViz(ErrorManager errMgr,
				 DebugST root, String output,
                 final List<InterpEvent> allEvents,
                 List<String> trace,
                 List<STMessage> errors)
    {
		this.errMgr = errMgr;
        // TODO move all this to JFrame so i can return it.
        currentST = root;

        this.allEvents = allEvents;
        this.errors = errors;

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

        m.output.setText(output);

        m.template.setText(currentST.impl.template);
        m.bytecode.setText(currentST.impl.disasm());
        m.trace.setText(Misc.join(trace.iterator(), "\n"));

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
        m.overallSplitPane.setBorder(null);

        // ADD ERRORS
        if ( errors==null || errors.size()==0 ) {
            m.errorScrollPane.setVisible(false); // don't show unless errors
        }
        else {
            final DefaultListModel errorListModel = new DefaultListModel();
            for (STMessage msg : errors) {
                errorListModel.addElement(msg);
            }
            m.errorList.setModel(errorListModel);
        }

        m.errorList.addListSelectionListener(
            new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    int minIndex = m.errorList.getMinSelectionIndex();
                    int maxIndex = m.errorList.getMaxSelectionIndex();
                    int i = minIndex;
                    while ( i <= maxIndex ) {
                        if (m.errorList.isSelectedIndex(i)) break;
                        i++;
                    }
                    ListModel model = m.errorList.getModel();
                    STMessage msg = (STMessage)model.getElementAt(i);
                    if ( msg instanceof STRuntimeMessage ) {
                        STRuntimeMessage rmsg = (STRuntimeMessage)msg;
                        Interval I = rmsg.self.impl.sourceMap[rmsg.ip];
                        currentST = (DebugST)msg.self;
                        update(m);
                        if ( I!=null ) { // highlight template
                            highlight(m.template, I.a, I.b);
                        }
                    }
                }
            }
        );

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
        m.bytecode.setText(currentST.impl.disasm());


		List<ST> pathST = currentST.getEnclosingInstanceStack(true);
		Object[] path = new Object[pathST.size()];
		int j = 0;
		for (ST s : pathST) path[j++] = new JTreeSTModel.Wrapper((DebugST)s);

		m.tree.setSelectionPath(new TreePath(path));

		if ( currentST.enclosingInstance!=null ) {
			int i = tmodel.getIndexOfChild((DebugST)currentST.enclosingInstance, currentST);
			InterpEvent e = ((DebugST)currentST.enclosingInstance).interpEvents.get(i);
			if ( e instanceof EvalTemplateEvent) {
				String txt = currentST.impl.template;
				m.template.setText(txt);
				if ( currentST.isAnonSubtemplate() ) {
					highlight(m.template, currentST.impl.embeddedStart, currentST.impl.embeddedStop);
				}
				highlight(m.output, e.start, e.stop);
			}
			else {
				m.template.setText(currentST.impl.template);
			}
		}
		else {
			String txt = currentST.impl.template;
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
			errMgr.internalError(tmodel.root.st, "bad highlight location", ble);
		}
	}

	protected void updateAttributes(final DebugST st, final STViewFrame m) {
		//System.out.println("add events="+ st.addAttrEvents);
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
			"method(type,name,locals,args,stats) ::= <<\n" +
			"public <type> <ick()> <name>(<args:{a| int <a>}; separator=\", \">) {\n" +
			"    <if(locals)>int locals[<locals>];<endif>\n"+
			"    <stats;separator=\"\\n\">\n" +
			"}\n" +
			">>\n"+
			"assign(a,b) ::= \"<a> = <b> <a,b:{foo}>;\"\n"+
			"return(x) ::= <<return <x>;>>\n" +
			"paren(x) ::= \"(<x>)\"\n";

        String tmpdir = System.getProperty("java.io.tmpdir");
        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
        group.debug = true;
        DebugST st = (DebugST)group.getInstanceOf("method");
        st.impl.dump();
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

//        st.inspect();
		st.render();
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
