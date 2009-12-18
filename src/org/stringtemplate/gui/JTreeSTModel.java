/*
 [The "BSD licence"]
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
package org.stringtemplate.gui;

import org.stringtemplate.debug.InterpEvent;
import org.stringtemplate.debug.DebugST;

import javax.swing.tree.*;
import javax.swing.event.TreeModelListener;

public class JTreeSTModel implements TreeModel {
    Wrapper root;

	public static class Wrapper {
		public DebugST st;
		public Wrapper(DebugST st) { this.st = st; }

		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Wrapper wrapper = (Wrapper) o;
			if (st != null ? !st.equals(wrapper.st) : wrapper.st != null) return false;
			return true;
		}
		public int hashCode() { return st != null ? st.hashCode() : 0; }
		public String toString() {
			if ( st.isSubtemplate() ) return "{...}";
            return st.toString()+" @ "+st.newSTEvent.getFileName()+":"+st.newSTEvent.getLine();
		}
	}
	
    public JTreeSTModel(DebugST root) { this.root = new Wrapper(root); }

    public int getChildCount(Object parent) {
        DebugST st = ((Wrapper) parent).st;
        return st.interpEvents.size();
    }

    public int getIndexOfChild(DebugST parent, DebugST child){
        return getIndexOfChild(new Wrapper(parent), new Wrapper(child));
    }

    public int getIndexOfChild(Object parent, Object child){
        if ( parent==null ) return -1;
        DebugST parentST = ((Wrapper) parent).st;
        DebugST childST = ((Wrapper) child).st;
        int i = 0;
        for (InterpEvent e : parentST.interpEvents) {
            if ( e.self == childST ) return i;
            i++;
        }
        return -1;
    }

    public Object getChild(Object parent, int index){
        DebugST st = ((Wrapper) parent).st;
        return new Wrapper(st.interpEvents.get(index).self);
    }

    public boolean isLeaf(Object node) {
        return getChildCount(node)==0;
    }

    public Object getRoot() { return root; }

    public void valueForPathChanged(TreePath treePath, Object o) {
    }

    public void addTreeModelListener(TreeModelListener treeModelListener) {
    }

    public void removeTreeModelListener(TreeModelListener treeModelListener) {
    }

}
