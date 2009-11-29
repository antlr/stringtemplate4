package org.stringtemplate.gui;

import org.stringtemplate.*;

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

/** */
public class STViz {
	public static ST currentST;

	public static List<Interpreter.DebugEvent> allEvents;

	public static JTreeSTModel tmodel;

	public static void main(String[] args) throws IOException {
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
		ST st = group.getInstanceOf("method");
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

		currentST = st;

		StringWriter sw = new StringWriter();
		Interpreter interp = new Interpreter(group, new AutoIndentWriter(sw));
		interp.setDebug(true);
		interp.exec(st);
		allEvents = interp.getEvents();

		final STViewFrame m = new STViewFrame();
		updateStack(currentST, m);
		updateAttributes(currentST, m);

		tmodel = new JTreeSTModel(st);
		m.tree.setModel(tmodel);
		m.tree.addTreeSelectionListener(
			new TreeSelectionListener() {
				public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
					currentST = JTreeSTModel.getST(m.tree.getLastSelectedPathComponent());
					update(m);
				}
			}
		);

		m.output.setText(sw.toString());

		m.template.setText(st.code.getTemplate());

		CaretListener caretListenerLabel = new CaretListener() {
			public void caretUpdate(CaretEvent e) {
				int dot = e.getDot();
				Interpreter.DebugEvent de = findEventAtOutputLocation(allEvents, dot);
				if ( de==null ) currentST = tmodel.root.st;
				else currentST = de.self;
				update(m);
			}
		};

		m.output.addCaretListener(caretListenerLabel);

		m.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		m.pack();
		m.setVisible(true);
	}

	private static void update(STViewFrame m) {
		updateStack(currentST, m);
		updateAttributes(currentST, m);

		List<ST> pathST = currentST.getEnclosingInstanceStack(true);
		Object[] path = new Object[pathST.size()];
		int j = 0;
		for (ST s : pathST) path[j++] = new JTreeSTModel.Wrapper(s);

		m.tree.setSelectionPath(new TreePath(path));

		if ( currentST.enclosingInstance!=null ) {
			int i = tmodel.getIndexOfChild(currentST.enclosingInstance, currentST);
			Interpreter.DebugEvent e = currentST.enclosingInstance.events.get(i);
			if ( e instanceof Interpreter.EvalTemplateEvent ) {
				Interpreter.EvalTemplateEvent et = (Interpreter.EvalTemplateEvent)e;
				String txt = currentST.code.getTemplate();
				m.template.setText(txt);
				if ( currentST.isSubtemplate() ) {
					highlight(m.template, currentST.code.embeddedStart, currentST.code.embeddedStop);
				}
				highlight(m.output, et.start, et.stop);
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

	protected static void highlight(JTextComponent comp, int i, int j) {
		Highlighter highlighter = comp.getHighlighter();
		highlighter.removeAllHighlights();

		try {
			highlighter.addHighlight(i, j+1, DefaultHighlighter.DefaultPainter);
		}
		catch (BadLocationException ble) {
			System.out.println("can't highlight");
		}
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
		List<ST> stack = st.getEnclosingInstanceStack(true);
		m.setTitle("STViz - ["+Misc.join(stack.iterator()," ")+"]");
	}

	public static Interpreter.DebugEvent findEventAtOutputLocation(List<Interpreter.DebugEvent> events,
																   int charIndex)
	{
		for (Interpreter.DebugEvent e : events) {
			if ( charIndex>=e.start && charIndex<=e.stop ) return e;
		}
		return null;
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
