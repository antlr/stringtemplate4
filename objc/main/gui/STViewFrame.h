
/**
 * @author Terence Parr
 */

@interface STViewFrame : JFrame {
  JSplitPane *overallSplitPane;
  JSplitPane *mainSplitPane;
  JSplitPane *topSplitPane;
  JScrollPane *treeScrollPane;
  JTree *tree;
  JScrollPane *scrollPane7;
  JTextPane *output;
  JSplitPane *bottomSplitPane;
  JScrollPane *attributeScrollPane;
  JList *attributes;
  JTabbedPane *tabbedPane1;
  JPanel *panel1;
  JScrollPane *scrollPane3;
  JTextPane *template;
  JScrollPane *scrollPane2;
  JTree *ast;
  JScrollPane *scrollPane15;
  JTextPane *bytecode;
  JScrollPane *scrollPane1;
  JTextPane *trace;
  JScrollPane *errorScrollPane;
  JList *errorList;
}

- (id) init;
@end
