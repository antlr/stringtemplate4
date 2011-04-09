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
/*
 * Created by JFormDesigner on Sun Nov 29 12:38:59 PST 2009
 */

package org.stringtemplate.v4.gui;

import javax.swing.*;
import java.awt.*;

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
		toolBar1 = new JToolBar();
		treeContentSplitPane = new JSplitPane();
		treeAttributesSplitPane = new JSplitPane();
		treeScrollPane = new JScrollPane();
		tree = new JTree();
		attributeScrollPane = new JScrollPane();
		attributes = new JTree();
		outputTemplateSplitPane = new JSplitPane();
		scrollPane7 = new JScrollPane();
		output = new JTextPane();
		templateBytecodeTraceTabPanel = new JTabbedPane();
		panel1 = new JPanel();
		scrollPane3 = new JScrollPane();
		template = new JTextPane();
		scrollPane2 = new JScrollPane();
		ast = new JTree();
		scrollPane15 = new JScrollPane();
		bytecode = new JTextPane();
		scrollPane1 = new JScrollPane();
		trace = new JTextPane();
		errorScrollPane = new JScrollPane();
		errorList = new JList();

		//======== this ========
		Container contentPane = getContentPane();
		contentPane.setLayout(new GridBagLayout());
		((GridBagLayout)contentPane.getLayout()).columnWidths = new int[] {0, 0};
		((GridBagLayout)contentPane.getLayout()).rowHeights = new int[] {0, 0, 0, 0};
		((GridBagLayout)contentPane.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
		((GridBagLayout)contentPane.getLayout()).rowWeights = new double[] {0.0, 1.0, 0.0, 1.0E-4};
		contentPane.add(toolBar1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 0, 0), 0, 0));

		//======== treeContentSplitPane ========
		{
			treeContentSplitPane.setResizeWeight(0.25);

			//======== treeAttributesSplitPane ========
			{
				treeAttributesSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
				treeAttributesSplitPane.setResizeWeight(0.7);

				//======== treeScrollPane ========
				{
					treeScrollPane.setViewportView(tree);
				}
				treeAttributesSplitPane.setTopComponent(treeScrollPane);

				//======== attributeScrollPane ========
				{
					attributeScrollPane.setViewportView(attributes);
				}
				treeAttributesSplitPane.setBottomComponent(attributeScrollPane);
			}
			treeContentSplitPane.setLeftComponent(treeAttributesSplitPane);

			//======== outputTemplateSplitPane ========
			{
				outputTemplateSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
				outputTemplateSplitPane.setResizeWeight(0.7);

				//======== scrollPane7 ========
				{
					scrollPane7.setViewportView(output);
				}
				outputTemplateSplitPane.setTopComponent(scrollPane7);

				//======== templateBytecodeTraceTabPanel ========
				{

					//======== panel1 ========
					{
						panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));

						//======== scrollPane3 ========
						{
							scrollPane3.setViewportView(template);
						}
						panel1.add(scrollPane3);

						//======== scrollPane2 ========
						{
							scrollPane2.setViewportView(ast);
						}
						panel1.add(scrollPane2);
					}
					templateBytecodeTraceTabPanel.addTab("template", panel1);


					//======== scrollPane15 ========
					{
						scrollPane15.setViewportView(bytecode);
					}
					templateBytecodeTraceTabPanel.addTab("bytecode", scrollPane15);


					//======== scrollPane1 ========
					{
						scrollPane1.setViewportView(trace);
					}
					templateBytecodeTraceTabPanel.addTab("trace", scrollPane1);

				}
				outputTemplateSplitPane.setBottomComponent(templateBytecodeTraceTabPanel);
			}
			treeContentSplitPane.setRightComponent(outputTemplateSplitPane);
		}
		contentPane.add(treeContentSplitPane, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 0, 0), 0, 0));

		//======== errorScrollPane ========
		{
			errorScrollPane.setViewportView(errorList);
		}
		contentPane.add(errorScrollPane, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 0, 0), 0, 0));
		pack();
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner non-commercial license
	private JToolBar toolBar1;
	public JSplitPane treeContentSplitPane;
	public JSplitPane treeAttributesSplitPane;
	public JScrollPane treeScrollPane;
	protected JTree tree;
	protected JScrollPane attributeScrollPane;
	protected JTree attributes;
	public JSplitPane outputTemplateSplitPane;
	protected JScrollPane scrollPane7;
	public JTextPane output;
	public JTabbedPane templateBytecodeTraceTabPanel;
	private JPanel panel1;
	private JScrollPane scrollPane3;
	public JTextPane template;
	private JScrollPane scrollPane2;
	public JTree ast;
	protected JScrollPane scrollPane15;
	protected JTextPane bytecode;
	private JScrollPane scrollPane1;
	public JTextPane trace;
	public JScrollPane errorScrollPane;
	protected JList errorList;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
