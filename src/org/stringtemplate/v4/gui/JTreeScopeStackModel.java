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

import org.antlr.runtime.tree.CommonTree;
import org.stringtemplate.v4.InstanceScope;
import org.stringtemplate.v4.Interpreter;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.StringRenderer;
import org.stringtemplate.v4.debug.AddAttributeEvent;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** From a scope, get stack of enclosing scopes in order from root down
 *  to scope.  Then show each scope's (ST's) attributes as children.
 */
public class JTreeScopeStackModel implements TreeModel {
	CommonTree root;

	public static class StringTree extends CommonTree {
		String text;
		public StringTree(String text) {this.text = text;}

		@Override
		public boolean isNil() {
			return text==null;
		}

		@Override
		public String toString() {
			if ( !isNil() ) return text;
			return "nil";
		}
	}

	public JTreeScopeStackModel(InstanceScope scope) {
		root = new StringTree("Scope stack:");
		Set<String> names = new HashSet<String>();
		List<InstanceScope> stack = Interpreter.getScopeStack(scope, false);
		for (InstanceScope s : stack) {
			StringTree templateNode = new StringTree(s.st.getName());
			root.insertChild(0, templateNode);
			addAttributeDescriptions(s.st, templateNode, names);
		}
		//System.out.println(root.toStringTree());
	}

	public void addAttributeDescriptions(ST st, StringTree node, Set<String> names) {
		Map<String, Object> attrs = st.getAttributes();
		if ( attrs==null ) return;
		for (String a : attrs.keySet()) {
			String descr;
			if ( st.debugState!=null && st.debugState.addAttrEvents!=null ) {
				List<AddAttributeEvent> events = st.debugState.addAttrEvents.get(a);
				StringBuilder locations = new StringBuilder();
				int i = 0;
				if ( events!=null ) {
					for (AddAttributeEvent ae : events) {
						if ( i>0 ) locations.append(", ");
						locations.append(ae.getFileName()+":"+ae.getLine());
						i++;
					}
				}
				if ( locations.length()>0 ) {
					descr = a+" = "+attrs.get(a)+" @ "+locations.toString();
				}
				else {
					descr = a + " = " +attrs.get(a);
				}
			}
			else {
				descr = a + " = " +attrs.get(a);
			}

			if (!names.add(a)) {
				StringBuilder builder = new StringBuilder();
				builder.append("<html><font color=\"gray\">");
				builder.append(StringRenderer.escapeHTML(descr));
				builder.append("</font></html>");
				descr = builder.toString();
			}

			node.addChild( new StringTree(descr) );
		}
	}

	@Override
	public Object getRoot() {
		return root;
	}

	@Override
	public Object getChild(Object parent, int i) {
		StringTree t = (StringTree)parent;
		return t.getChild(i);
	}

	@Override
	public int getChildCount(Object parent) {
		StringTree t = (StringTree)parent;
		return t.getChildCount();
	}

	@Override
	public boolean isLeaf(Object node) {
		return getChildCount(node) == 0;
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		StringTree c = (StringTree)child;
		return c.getChildIndex();
	}

	@Override
	public void valueForPathChanged(TreePath treePath, Object o) {
	}

	@Override
	public void addTreeModelListener(TreeModelListener treeModelListener) {
	}

	@Override
	public void removeTreeModelListener(TreeModelListener treeModelListener) {
	}
}
