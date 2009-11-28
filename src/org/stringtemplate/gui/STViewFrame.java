package org.stringtemplate.gui;

import java.awt.*;
import javax.swing.*;
import com.jgoodies.forms.layout.*;
/*
 * Created by JFormDesigner on Fri Nov 27 13:25:07 PST 2009
 */



/**
 * @author Jean Bovet
 */
public class STViewFrame extends JFrame {
	public STViewFrame() {
		initComponents();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		menuBar1 = new JMenuBar();
		menu1 = new JMenu();
		menuItem1 = new JMenuItem();
		menu2 = new JMenu();
		menuItem2 = new JMenuItem();
		scrollPane1 = new JScrollPane();
		output = new JTextPane();
		scrollPane5 = new JScrollPane();
		tree = new JTree();
		scrollPane3 = new JScrollPane();
		stack = new JList();
		scrollPane4 = new JScrollPane();
		attributes = new JList();
		scrollPane2 = new JScrollPane();
		template = new JTextPane();
		CellConstraints cc = new CellConstraints();

		//======== this ========
		setTitle("Testing");
		Container contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"[50dlu,default,200dlu], $lcgap, [50dlu,min], $lcgap, 308dlu:grow",
			"$ugap, $lgap, 210dlu:grow, $lgap, 104dlu"));

		//======== menuBar1 ========
		{

			//======== menu1 ========
			{
				menu1.setText("File");

				//---- menuItem1 ----
				menuItem1.setText("Save");
				menu1.add(menuItem1);
			}
			menuBar1.add(menu1);

			//======== menu2 ========
			{
				menu2.setText("Edit");

				//---- menuItem2 ----
				menuItem2.setText("copy");
				menu2.add(menuItem2);
			}
			menuBar1.add(menu2);
		}
		setJMenuBar(menuBar1);

		//======== scrollPane1 ========
		{

			//---- output ----
			output.setText("void ");
			scrollPane1.setViewportView(output);
		}
		contentPane.add(scrollPane1, cc.xywh(3, 2, 3, 2));

		//======== scrollPane5 ========
		{
			scrollPane5.setViewportView(tree);
		}
		contentPane.add(scrollPane5, cc.xywh(1, 2, 1, 2));

		//======== scrollPane3 ========
		{
			scrollPane3.setViewportView(stack);
		}
		contentPane.add(scrollPane3, cc.xywh(1, 4, 1, 2));

		//======== scrollPane4 ========
		{
			scrollPane4.setViewportView(attributes);
		}
		contentPane.add(scrollPane4, cc.xywh(3, 4, 1, 2));

		//======== scrollPane2 ========
		{

			//---- template ----
			template.setText("tests");
			scrollPane2.setViewportView(template);
		}
		contentPane.add(scrollPane2, cc.xywh(5, 4, 1, 2));
		pack();
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	protected JMenuBar menuBar1;
	protected JMenu menu1;
	protected JMenuItem menuItem1;
	protected JMenu menu2;
	protected JMenuItem menuItem2;
	protected JScrollPane scrollPane1;
	protected JTextPane output;
	private JScrollPane scrollPane5;
	protected JTree tree;
	protected JScrollPane scrollPane3;
	protected JList stack;
	protected JScrollPane scrollPane4;
	protected JList attributes;
	protected JScrollPane scrollPane2;
	protected JTextPane template;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
