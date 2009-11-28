package org.stringtemplate.gui;

import org.stringtemplate.ST;
import org.stringtemplate.Interpreter;

import javax.swing.tree.*;
import javax.swing.event.TreeModelListener;

public class JTreeSTModel implements TreeModel {
    ST root;
    public JTreeSTModel(ST root) { this.root = root; }

    public int getChildCount(Object parent) {
        ST st = (ST)parent;
        return st.events.size();
    }

    public int getIndexOfChild(Object parent, Object child){
        ST st = (ST)parent;
        int i = 0;
        for (Interpreter.DebugEvent e : st.events) {
            if ( e.self == child ) return i;
            i++;
        }
        return -1;
    }

    public Object getChild(Object parent, int index){
        ST st = (ST)parent;
        return st.events.get(index).self;
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
}
