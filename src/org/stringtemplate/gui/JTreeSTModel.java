package org.stringtemplate.gui;

import org.stringtemplate.ST;
import org.stringtemplate.debug.InterpEvent;
import org.stringtemplate.debug.STDebugInfo;

import javax.swing.tree.*;
import javax.swing.event.TreeModelListener;

public class JTreeSTModel implements TreeModel {
    Wrapper root;

	public static class Wrapper {
		public ST st;
		public Wrapper(ST st) { this.st = st; }

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
            STDebugInfo info = st.getDebugInfo();
            if ( info!=null )
                return st.toString()+" @ "+
                       info.newSTEvent.getFileName()+":"+info.newSTEvent.getLine();
			return st.toString();
		}
	}
	
    public JTreeSTModel(ST root) { this.root = new Wrapper(root); }

    public int getChildCount(Object parent) {
		ST st = getST(parent);
        return st.getDebugInfo().interpEvents.size();
    }

    public int getIndexOfChild(Object parent, Object child){
        if ( parent==null ) return -1;
		ST parentST = getST(parent);
		ST childST = getST(child);
        int i = 0;
        for (InterpEvent e : parentST.getDebugInfo().interpEvents) {
            if ( e.self == childST ) return i;
            i++;
        }
        return -1;
    }

    public Object getChild(Object parent, int index){
		ST st = getST(parent);
        return new Wrapper(st.getDebugInfo().interpEvents.get(index).self);
    }

    public boolean isLeaf(Object node) {
        return getChildCount(node)==0;
    }

    public Object getRoot() {
        return root;
    }

    public void valueForPathChanged(TreePath treePath, Object o) {
    }

    public void addTreeModelListener(TreeModelListener treeModelListener) {
    }

    public void removeTreeModelListener(TreeModelListener treeModelListener) {
    }

	public static ST getST(Object o) {
		if ( o instanceof Wrapper ) return ((Wrapper)o).st;
		else return (ST)o;
	}
}
