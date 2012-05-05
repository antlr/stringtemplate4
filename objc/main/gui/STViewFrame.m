#import "STViewFrame.h"

@implementation STViewFrame

- (id) init {
  if ( (self=[super init]) != nil ) {
    [self initComponents];
  }
  return self;
}

- (void) initComponents {
  overallSplitPane = [[NSSplitPView alloc] init];
  mainSplitPane = [[NSSplitPView alloc] init];
  topSplitPane = [[NSSplitPView alloc] init];
  treeScrollPane = [[NSScrollView alloc] init];
  tree = [[JTree alloc] init];
  scrollPane7 = [[NSScrollView alloc] init];
  output = [[JTextPane alloc] init];
  bottomSplitPane = [[NSSplitPView alloc] init];
  attributeScrollPane = [[NSScrollView alloc] init];
  attributes = [[JList alloc] init];
  tabbedPane1 = [[JTabbedPane alloc] init];
  panel1 = [[JPanel alloc] init];
  scrollPane3 = [[NSScrollView alloc] init];
  template = [[JTextPane alloc] init];
  scrollPane2 = [[NSScrollView alloc] init];
  ast = [[JTree alloc] init];
  scrollPane15 = [[NSScrollView alloc] init];
  bytecode = [[JTextPane alloc] init];
  scrollPane1 = [[NSScrollView alloc] init];
  trace = [[JTextPane alloc] init];
  errorScrollPane = [[NSScrollView alloc] init];
  errorList = [[JList alloc] init];
  Container *contentPane = [self contentPane];
  [contentPane setLayout:[[GridLayout alloc] init:1 param1:0 param2:0 param3:10]];
  {
    [overallSplitPane setOrientation:NSSplitPView.VERTICAL_SPLIT];
    [overallSplitPane setContinuousLayout:YES];
    [overallSplitPane setOneTouchExpandable:YES];
    [overallSplitPane setResizeWeight:0.9];
    {
      [mainSplitPane setOrientation:NSSplitPView.VERTICAL_SPLIT];
      [mainSplitPane setResizeWeight:0.8];
      [mainSplitPane setOneTouchExpandable:YES];
      [mainSplitPane setContinuousLayout:YES];
      {
        [topSplitPane setContinuousLayout:YES];
        [topSplitPane setResizeWeight:0.15];
        [topSplitPane setOneTouchExpandable:YES];
        {
          [treeScrollPane setViewportView:tree];
        }
        [topSplitPane setLeftComponent:treeScrollPane];
        {
          [scrollPane7 setViewportView:output];
        }
        [topSplitPane setRightComponent:scrollPane7];
      }
      [mainSplitPane setTopComponent:topSplitPane];
      {
        [bottomSplitPane setResizeWeight:0.15];
        [bottomSplitPane setOneTouchExpandable:YES];
        [bottomSplitPane setContinuousLayout:YES];
        {
          [attributeScrollPane setViewportView:attributes];
        }
        [bottomSplitPane setLeftComponent:attributeScrollPane];
        {
          {
            [panel1 setLayout:[[BoxLayout alloc] init:panel1 param1:BoxLayout.X_AXIS]];
            {
              [scrollPane3 setViewportView:template];
            }
            [panel1 add:scrollPane3];
            {
              [scrollPane2 setViewportView:ast];
            }
            [panel1 add:scrollPane2];
          }
          [tabbedPane1 addTab:@"template" param1:panel1];
          {
            [scrollPane15 setViewportView:bytecode];
          }
          [tabbedPane1 addTab:@"bytecode" param1:scrollPane15];
          {
            [scrollPane1 setViewportView:trace];
          }
          [tabbedPane1 addTab:@"trace" param1:scrollPane1];
        }
        [bottomSplitPane setRightComponent:tabbedPane1];
      }
      [mainSplitPane setBottomComponent:bottomSplitPane];
    }
    [overallSplitPane setTopComponent:mainSplitPane];
    {
      [errorScrollPane setViewportView:errorList];
    }
    [overallSplitPane setBottomComponent:errorScrollPane];
  }
  [contentPane add:overallSplitPane];
  [self pack];
  [self setLocationRelativeTo:[self owner]];
}

- (void) dealloc {
  [overallSplitPane release];
  [mainSplitPane release];
  [topSplitPane release];
  [treeScrollPane release];
  [tree release];
  [scrollPane7 release];
  [output release];
  [bottomSplitPane release];
  [attributeScrollPane release];
  [attributes release];
  [tabbedPane1 release];
  [panel1 release];
  [scrollPane3 release];
  [template release];
  [scrollPane2 release];
  [ast release];
  [scrollPane15 release];
  [bytecode release];
  [scrollPane1 release];
  [trace release];
  [errorScrollPane release];
  [errorList release];
  [super dealloc];
}

@end
