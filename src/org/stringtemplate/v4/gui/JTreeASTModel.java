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

import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.runtime.tree.TreeAdaptor;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

// TODO: copied from ANTLR v4; rm when upgraded to v4
public class JTreeASTModel implements TreeModel {
    TreeAdaptor adaptor;
    Object root;

    public JTreeASTModel(TreeAdaptor adaptor, Object root) {
        this.adaptor = adaptor;
        this.root = root;
    }

    public JTreeASTModel(Object root) {
        this.adaptor = new CommonTreeAdaptor();
        this.root = root;
    }

	@Override
    public int getChildCount(Object parent) {
        return adaptor.getChildCount(parent);
    }

	@Override
    public int getIndexOfChild(Object parent, Object child){
        if ( parent==null ) return -1;
        return adaptor.getChildIndex(child);
    }

	@Override
    public Object getChild(Object parent, int index){
        return adaptor.getChild(parent, index);
    }

	@Override
    public boolean isLeaf(Object node) {
        return getChildCount(node)==0;
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
