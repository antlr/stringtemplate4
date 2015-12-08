/*
 * [The "BSD license"]
 *  Copyright (c) 2011 Terence Parr
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.stringtemplate.v4.gui;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.stringtemplate.v4.InstanceScope;
import org.stringtemplate.v4.Interpreter;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import org.stringtemplate.v4.STGroupString;
import org.stringtemplate.v4.debug.EvalExprEvent;
import org.stringtemplate.v4.debug.EvalTemplateEvent;
import org.stringtemplate.v4.debug.InterpEvent;
import org.stringtemplate.v4.misc.ErrorManager;
import org.stringtemplate.v4.misc.Interval;
import org.stringtemplate.v4.misc.Misc;
import org.stringtemplate.v4.misc.STMessage;
import org.stringtemplate.v4.misc.STRuntimeMessage;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.tree.TreePath;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class STViz {
	protected static final String WINDOWS_LINE_ENDINGS = "WINDOWS_LINE_ENDINGS";

	//public ST currentST; // current ST selected in template tree
	public EvalTemplateEvent root;
	public InterpEvent currentEvent;
	public InstanceScope currentScope;
	public List<InterpEvent> allEvents;
	public JTreeSTModel tmodel;
	public ErrorManager errMgr;
	public Interpreter interp;
	public String output;
	public List<String> trace;
	public List<STMessage> errors;

	public STViewFrame viewFrame;

	private final AtomicInteger updateDepth = new AtomicInteger();

    public STViz(ErrorManager errMgr,
				 EvalTemplateEvent root,
				 String output,
				 Interpreter interp,
                 List<String> trace,
                 List<STMessage> errors)
    {
		this.errMgr = errMgr;
		this.currentEvent = root;
		this.currentScope = root.scope;
		this.output = output;
		this.interp = interp;
        this.allEvents = interp.getEvents();
		this.trace = trace;
        this.errors = errors;
	}

	public void open() {
        viewFrame = new STViewFrame();
        updateStack(currentScope, viewFrame);
        updateAttributes(currentScope, viewFrame);

		List<InterpEvent> events = currentScope.events;
		tmodel = new JTreeSTModel(interp, (EvalTemplateEvent)events.get(events.size()-1));
        viewFrame.tree.setModel(tmodel);
        viewFrame.tree.addTreeSelectionListener(
            new TreeSelectionListener() {
				@Override
                public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
					int depth = updateDepth.incrementAndGet();
					try {
						if (depth != 1) {
							return;
						}

						currentEvent = ((JTreeSTModel.Wrapper)viewFrame.tree.getLastSelectedPathComponent()).event;
						currentScope = currentEvent.scope;
						updateCurrentST(viewFrame);
					}
					finally {
						updateDepth.decrementAndGet();
					}
                }
            }
        );

		JTreeASTModel astModel = new JTreeASTModel(new CommonTreeAdaptor(), currentScope.st.impl.ast);
		viewFrame.ast.setModel(astModel);
		viewFrame.ast.addTreeSelectionListener(
			new TreeSelectionListener() {
				@Override
				public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
					int depth = updateDepth.incrementAndGet();
					try {
						if (depth != 1) {
							return;
						}

						TreePath path = treeSelectionEvent.getNewLeadSelectionPath();
						if ( path==null ) return;
						CommonTree node = (CommonTree)treeSelectionEvent.getNewLeadSelectionPath().getLastPathComponent();
						//System.out.println("select AST: "+node);
						CommonToken a = (CommonToken)currentScope.st.impl.tokens.get(node.getTokenStartIndex());
						CommonToken b = (CommonToken)currentScope.st.impl.tokens.get(node.getTokenStopIndex());
						highlight(viewFrame.template, a.getStartIndex(), b.getStopIndex());
					}
					finally {
						updateDepth.decrementAndGet();
					}
				}
			}
		);

		// Track selection of attr but do nothing for now
//        viewFrame.attributes.addListSelectionListener(
//            new ListSelectionListener() {
//                public void valueChanged(ListSelectionEvent e) {
//                    int minIndex = viewFrame.attributes.getMinSelectionIndex();
//                    int maxIndex = viewFrame.attributes.getMaxSelectionIndex();
//                    for (int i = minIndex; i <= maxIndex; i++) {
//                        if (viewFrame.attributes.isSelectedIndex(i)) {
//                            //System.out.println("index="+i);
//                        }
//                    }
//                }
//            }
//        );

        CaretListener caretListenerLabel = new CaretListener() {
			@Override
            public void caretUpdate(CaretEvent e) {
				int depth = updateDepth.incrementAndGet();
				try {
					if (depth != 1) {
						return;
					}

					int dot = toEventPosition((JTextComponent)e.getSource(), e.getDot());
					currentEvent = findEventAtOutputLocation(allEvents, dot);
					if ( currentEvent==null ) currentScope = tmodel.root.event.scope;
					else currentScope = currentEvent.scope;

					// update tree view of template hierarchy
					// compute path from root to currentST, create TreePath for tree widget
					List<EvalTemplateEvent> stack = Interpreter.getEvalTemplateEventStack(currentScope, true);
					//System.out.println("\nselect path="+stack);
					Object[] path = new Object[stack.size()];
					int j = 0;
					for (EvalTemplateEvent s : stack) {
						path[j++] = new JTreeSTModel.Wrapper(s);
					}
					TreePath p = new TreePath(path);
					viewFrame.tree.setSelectionPath(p);
					viewFrame.tree.scrollPathToVisible(p);
					updateCurrentST(viewFrame);
				}
				finally {
					updateDepth.decrementAndGet();
				}
			}
		};

		viewFrame.output.addCaretListener(caretListenerLabel);

        // ADD ERRORS
        if ( errors==null || errors.size()==0 ) {
            viewFrame.errorScrollPane.setVisible(false); // don't show unless errors
        }
        else {
            final DefaultListModel errorListModel = new DefaultListModel();
            for (STMessage msg : errors) {
                errorListModel.addElement(msg);
            }
            viewFrame.errorList.setModel(errorListModel);
        }

        viewFrame.errorList.addListSelectionListener(
            new ListSelectionListener() {
				@Override
                public void valueChanged(ListSelectionEvent e) {
					int depth = updateDepth.incrementAndGet();
					try {
						if (depth != 1) {
							return;
						}

						int minIndex = viewFrame.errorList.getMinSelectionIndex();
						int maxIndex = viewFrame.errorList.getMaxSelectionIndex();
						int i = minIndex;
						while ( i <= maxIndex ) {
							if (viewFrame.errorList.isSelectedIndex(i)) break;
							i++;
						}
						ListModel model = viewFrame.errorList.getModel();
						STMessage msg = (STMessage)model.getElementAt(i);
						if ( msg instanceof STRuntimeMessage ) {
							STRuntimeMessage rmsg = (STRuntimeMessage)msg;
							Interval I = rmsg.self.impl.sourceMap[rmsg.ip];
							currentEvent = null;
							currentScope = ((STRuntimeMessage) msg).scope;
							updateCurrentST(viewFrame);
							if ( I!=null ) { // highlight template
								highlight(viewFrame.template, I.a, I.b);
							}
						}
					}
					finally {
						updateDepth.decrementAndGet();
					}
                }
            }
        );

		Border empty = BorderFactory.createEmptyBorder();
		viewFrame.treeContentSplitPane.setBorder(empty);
		viewFrame.outputTemplateSplitPane.setBorder(empty);
		viewFrame.templateBytecodeTraceTabPanel.setBorder(empty);
		viewFrame.treeAttributesSplitPane.setBorder(empty);


		viewFrame.treeContentSplitPane.setOneTouchExpandable(true);
		viewFrame.outputTemplateSplitPane.setOneTouchExpandable(true);
		viewFrame.treeContentSplitPane.setDividerSize(10);
		viewFrame.outputTemplateSplitPane.setDividerSize(8);
		viewFrame.treeContentSplitPane.setContinuousLayout(true);
		viewFrame.treeAttributesSplitPane.setContinuousLayout(true);
		viewFrame.outputTemplateSplitPane.setContinuousLayout(true);

		viewFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		viewFrame.pack();
		viewFrame.setSize(900, 700);

        setText(viewFrame.output, output);
        setText(viewFrame.template, currentScope.st.impl.template);
        setText(viewFrame.bytecode, currentScope.st.impl.disasm());
        setText(viewFrame.trace, Misc.join(trace.iterator(), "\n"));

        viewFrame.setVisible(true);
    }

	public void waitForClose() throws InterruptedException {
		final Object lock = new Object();

		Thread t = new Thread() {
			@Override
			public void run() {
				synchronized (lock) {
					while (viewFrame.isVisible()) {
						try {
							lock.wait();
						} catch (InterruptedException e) {
						}
					}
				}
			}
		};

		t.start();

		viewFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				synchronized (lock) {
					viewFrame.setVisible(false);
					lock.notify();
				}
			}
		});

		t.join();
	}

	private void updateCurrentST(STViewFrame m) {
//		System.out.println("updateCurrentST(): currentScope.st="+currentScope.st);
		// update all views according to currentScope.st
		updateStack(currentScope, m); 					   // STACK
		updateAttributes(currentScope, m); 			 	   // ATTRIBUTES
        setText(m.bytecode, currentScope.st.impl.disasm()); // BYTECODE DIS.
		setText(m.template, currentScope.st.impl.template); // TEMPLATE SRC
		JTreeASTModel astModel = new JTreeASTModel(new CommonTreeAdaptor(), currentScope.st.impl.ast);
		viewFrame.ast.setModel(astModel);

		// highlight output text and, if {...} subtemplate, region in ST src
		// get last event for currentScope.st; it's the event that captures ST eval
		if (currentEvent instanceof EvalExprEvent) {
			EvalExprEvent exprEvent = (EvalExprEvent)currentEvent;
			highlight(m.output, exprEvent.outputStartChar, exprEvent.outputStopChar);
			highlight(m.template, exprEvent.exprStartChar, exprEvent.exprStopChar);
		}
		else {
			EvalTemplateEvent templateEvent;
			if (currentEvent instanceof EvalTemplateEvent) {
				templateEvent = (EvalTemplateEvent)currentEvent;
			}
			else {
				List<InterpEvent> events = currentScope.events;
				templateEvent = (EvalTemplateEvent)events.get(events.size() - 1);
			}

			if (templateEvent != null) {
				highlight(m.output, templateEvent.outputStartChar, templateEvent.outputStopChar);
			}

			if ( currentScope.st.isAnonSubtemplate() ) {
				Interval r = currentScope.st.impl.getTemplateRange();
				//System.out.println("currentScope.st src range="+r);
				//m.template.moveCaretPosition(r.a);
				highlight(m.template, r.a, r.b);
			}
		}
	}

	protected void setText(JEditorPane component, String text) {
		List<Integer> windowsLineEndingsList = new ArrayList<Integer>();
		for (int i = 0; i < text.length(); i += 2) {
			i = text.indexOf("\r\n", i);
			if (i < 0) {
				break;
			}

			windowsLineEndingsList.add(i);
		}

		int[] windowsLineEndings = new int[windowsLineEndingsList.size()];
		for (int i = 0; i < windowsLineEndingsList.size(); i++) {
			windowsLineEndings[i] = windowsLineEndingsList.get(i);
		}

		component.setText(text);
		component.getDocument().putProperty(WINDOWS_LINE_ENDINGS, windowsLineEndings);
	}

	protected int toComponentPosition(JTextComponent component, int position) {
		int[] windowsLineEndings = (int[])component.getDocument().getProperty(WINDOWS_LINE_ENDINGS);
		if (windowsLineEndings == null || windowsLineEndings.length == 0) {
			return position;
		}

		int index = Arrays.binarySearch(windowsLineEndings, position);
		if (index >= 0) {
			return position - index;
		}

		return position - (-index - 1);
	}

	protected int toEventPosition(JTextComponent component, int position) {
		int result = position;
		while (toComponentPosition(component, result) < position) {
			result++;
		}

		return result;
	}

	protected final void highlight(JTextComponent comp, int i, int j) {
		highlight(comp, i, j, true);
	}

	protected void highlight(JTextComponent comp, int i, int j, boolean scroll) {
		Highlighter highlighter = comp.getHighlighter();
		highlighter.removeAllHighlights();

		try {
			i = toComponentPosition(comp, i);
			j = toComponentPosition(comp, j);
			highlighter.addHighlight(i, j+1, DefaultHighlighter.DefaultPainter);
			if (scroll) {
				if (comp.getCaretPosition() < i || comp.getCaretPosition() > j) {
					comp.moveCaretPosition(i);
					comp.scrollRectToVisible(comp.modelToView(i));
				}
			}
		}
		catch (BadLocationException ble) {
			errMgr.internalError(tmodel.root.event.scope.st, "bad highlight location", ble);
		}
	}

	protected void updateAttributes(final InstanceScope scope, final STViewFrame m) {
		//System.out.println("updateAttributes: "+Interpreter.getEnclosingInstanceStackString(scope) );
		m.attributes.setModel( new JTreeScopeStackModel(scope) );
		m.attributes.setRootVisible(false);
		m.attributes.setShowsRootHandles(true);
		//System.out.println("add events="+ st.addAttrEvents);
//		ST st = scope.st;
//		final DefaultListModel attrModel = new DefaultListModel();
//		final Map<String,Object> attrs = st.getAttributes();
//		if ( attrs!=null ) {
//			for (String a : attrs.keySet()) {
//				if ( st.debugState!=null && st.debugState.addAttrEvents!=null ) {
//					List<AddAttributeEvent> events = st.debugState.addAttrEvents.get(a);
//					StringBuilder locations = new StringBuilder();
//					int i = 0;
//					if ( events!=null ) {
//						for (AddAttributeEvent ae : events) {
//							if ( i>0 ) locations.append(", ");
//							locations.append(ae.getFileName()+":"+ae.getLine());
//							i++;
//						}
//					}
//					if ( locations.length()>0 ) {
//						attrModel.addElement(a+" = "+attrs.get(a)+" @ "+locations.toString());
//					}
//					else {
//						attrModel.addElement(a+" = "+attrs.get(a));
//					}
//				}
//				else {
//					attrModel.addElement(a+" = "+attrs.get(a));
//				}
//			}
//		}
//		m.attributes.setModel(attrModel);
	}

	protected void updateStack(InstanceScope scope, STViewFrame m) {
		List<ST> stack = Interpreter.getEnclosingInstanceStack(scope, true);
		m.setTitle("STViz - ["+ Misc.join(stack.iterator()," ")+"]");
//        // also do source stack
//        StackTraceElement[] trace = st.newSTEvent.stack.getStackTrace();
//        StringWriter sw = new StringWriter();
//        for (StackTraceElement e : trace) {
//            sw.write(e.toString()+"\n");
//        }
    }

    public InterpEvent findEventAtOutputLocation(List<InterpEvent> events,
                                                 int charIndex)
    {
        for (InterpEvent e : events) {
			if (e.scope.earlyEval) {
				continue;
			}

            if ( charIndex>=e.outputStartChar && charIndex<=e.outputStopChar) return e;
        }
        return null;
	}

    public static void main(String[] args) throws IOException { // test rig
		if ( args.length>0 && args[0].equals("1") ) test1();
		else if ( args.length>0 && args[0].equals("2") ) test2();
		else if ( args.length>0 && args[0].equals("3") ) test3();
		else if ( args.length>0 && args[0].equals("4") ) test4();
	}

	public static void test1() throws IOException { // test rig
        String templates =
			"method(type,name,locals,args,stats) ::= <<\n" +
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
        ST st = group.getInstanceOf("method");
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

		STViz viz = st.inspect();
		System.out.println(st.render()); // should not mess up ST event lists
    }

	public static void test2() throws IOException { // test rig
        String templates =
			"t1(q1=\"Some\\nText\") ::= <<\n" +
			"<q1>\n" +
			">>\n" +
			"\n" +
			"t2(p1) ::= <<\n" +
			"<p1>\n" +
			">>\n" +
			"\n" +
			"main() ::= <<\n" +
			"START-<t1()>-END\n" +
			"\n" +
			"START-<t2(p1=\"Some\\nText\")>-END\n" +
			">>\n";

        String tmpdir = System.getProperty("java.io.tmpdir");
        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
        ST st = group.getInstanceOf("main");
        STViz viz = st.inspect();
    }

	public static void test3() throws IOException {
        String templates =
			"main() ::= <<\n" +
			"Foo: <{bar};format=\"lower\">\n" +
			">>\n";

        String tmpdir = System.getProperty("java.io.tmpdir");
        writeFile(tmpdir, "t.stg", templates);
        STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
        ST st = group.getInstanceOf("main");
        st.inspect();
    }

	public static void test4() throws IOException {
        String templates =
			"main(t) ::= <<\n" +
			"hi: <t>\n" +
			">>\n" +
			"foo(x,y={hi}) ::= \"<bar(x,y)>\"\n" +
			"bar(x,y) ::= << <y> >>\n" +
			"ignore(m) ::= \"<m>\"\n";

        STGroup group = new STGroupString(templates);
        ST st = group.getInstanceOf("main");
		ST foo = group.getInstanceOf("foo");
		st.add("t", foo);
		ST ignore = group.getInstanceOf("ignore");
		ignore.add("m", foo); // embed foo twice!
        st.inspect();
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
