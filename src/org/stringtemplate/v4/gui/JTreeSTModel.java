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

import org.stringtemplate.v4.Interpreter;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.StringRenderer;
import org.stringtemplate.v4.debug.EvalTemplateEvent;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public class JTreeSTModel implements TreeModel {
	public Interpreter interp;
	public Wrapper root;

	public static class Wrapper {
		EvalTemplateEvent event;
		public Wrapper(EvalTemplateEvent event) { this.event = event; }

		@Override
		public int hashCode() {
			return event.hashCode();
		}

		@Override
		public boolean equals(Object o) {
			//System.out.println(event+"=="+((Wrapper)o).event+" is "+(this.event == ((Wrapper)o).event));
			return this.event == ((Wrapper)o).event;
		}

		@Override
		public String toString() {
			ST st = event.scope.st;
			if ( st.isAnonSubtemplate() ) return "{...}";
			if ( st.debugState!=null && st.debugState.newSTEvent!=null ) {
				String label = st.toString()+" @ "+st.debugState.newSTEvent.getFileName()+":"+
					   st.debugState.newSTEvent.getLine();
				return "<html><b>" + StringRenderer.escapeHTML(label) + "</b></html>";
			}
			else {
				return st.toString();
			}
		}
	}

	public JTreeSTModel(Interpreter interp, EvalTemplateEvent root) {
		this.interp = interp;
		this.root = new Wrapper(root);
	}

	@Override
	public Object getChild(Object parent, int index) {
		EvalTemplateEvent e = ((Wrapper)parent).event;
		return new Wrapper(e.scope.childEvalTemplateEvents.get(index));
	}

	@Override
	public int getChildCount(Object parent) {
		EvalTemplateEvent e = ((Wrapper)parent).event;
		return e.scope.childEvalTemplateEvents.size();
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		EvalTemplateEvent p = ((Wrapper)parent).event;
		EvalTemplateEvent c = ((Wrapper)parent).event;
        int i = 0;
        for (EvalTemplateEvent e : p.scope.childEvalTemplateEvents) {
            if ( e.scope.st == c.scope.st ) {
//				System.out.println(i);
//				System.out.println("found "+e.self+" as child of "+parentST);
				return i;
			}
            i++;
        }
		return -1;
	}

	@Override
	public boolean isLeaf(Object node) {
		return getChildCount(node) == 0;
	}

	@Override
	public Object getRoot() { return root; }

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
