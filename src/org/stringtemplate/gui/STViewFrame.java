/*
 * Created by JFormDesigner on Sun Nov 29 12:38:59 PST 2009
 */

package org.stringtemplate.gui;

import java.awt.*;
import javax.swing.*;

/**
 * @author Terence Parr
 */
public class STViewFrame extends JFrame {
    public STViewFrame() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        topSplitPane = new JSplitPane();
        treeScrollPane = new JScrollPane();
        tree = new JTree();
        scrollPane7 = new JScrollPane();
        output = new JTextPane();
        bottomSplitPane = new JSplitPane();
        attributeScrollPane = new JScrollPane();
        attributes = new JList();
        tabbedPane1 = new JTabbedPane();
        scrollPane13 = new JScrollPane();
        template = new JTextPane();
        scrollPane14 = new JScrollPane();
        stacktrace = new JTextPane();
        scrollPane15 = new JScrollPane();
        bytecode = new JTextPane();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new GridLayout(2, 0, 0, 10));

        //======== topSplitPane ========
        {
            topSplitPane.setContinuousLayout(true);
            topSplitPane.setResizeWeight(0.15);

            //======== treeScrollPane ========
            {
                treeScrollPane.setViewportView(tree);
            }
            topSplitPane.setLeftComponent(treeScrollPane);

            //======== scrollPane7 ========
            {
                scrollPane7.setViewportView(output);
            }
            topSplitPane.setRightComponent(scrollPane7);
        }
        contentPane.add(topSplitPane);

        //======== bottomSplitPane ========
        {
            bottomSplitPane.setResizeWeight(0.15);
            bottomSplitPane.setOneTouchExpandable(true);
            bottomSplitPane.setContinuousLayout(true);

            //======== attributeScrollPane ========
            {
                attributeScrollPane.setViewportView(attributes);
            }
            bottomSplitPane.setLeftComponent(attributeScrollPane);

            //======== tabbedPane1 ========
            {

                //======== scrollPane13 ========
                {
                    scrollPane13.setViewportView(template);
                }
                tabbedPane1.addTab("template", scrollPane13);


                //======== scrollPane14 ========
                {
                    scrollPane14.setViewportView(stacktrace);
                }
                tabbedPane1.addTab("stack trace", scrollPane14);


                //======== scrollPane15 ========
                {
                    scrollPane15.setViewportView(bytecode);
                }
                tabbedPane1.addTab("bytecode", scrollPane15);

            }
            bottomSplitPane.setRightComponent(tabbedPane1);
        }
        contentPane.add(bottomSplitPane);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    protected JSplitPane topSplitPane;
    protected JScrollPane treeScrollPane;
    protected JTree tree;
    protected JScrollPane scrollPane7;
    protected JTextPane output;
    protected JSplitPane bottomSplitPane;
    protected JScrollPane attributeScrollPane;
    protected JList attributes;
    protected JTabbedPane tabbedPane1;
    protected JScrollPane scrollPane13;
    protected JTextPane template;
    protected JScrollPane scrollPane14;
    protected JTextPane stacktrace;
    protected JScrollPane scrollPane15;
    protected JTextPane bytecode;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
