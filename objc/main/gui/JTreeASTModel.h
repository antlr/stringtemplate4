#import <ANTLR/ANTLR.h>
#import "TreeModelListener.h"
#import "TreeModel.h"
#import "TreePath.h"

@interface JTreeASTModel : NSObject <TreeModel> {
  TreeAdaptor *adaptor;
  id root;
}

@property(nonatomic, retain, readonly) id root;
- (id) init:(CommonTreeAdaptor *)adaptor root:(id)root;
- (id) initWithRoot:(id)root;
- (NSInteger) getChildCount:(id)parent;
- (NSInteger) getIndexOfChild:(id)parent child:(id)child;
- (id) getChild:(id)parent index:(NSInteger)index;
- (BOOL) isLeaf:(id)node;
- (void) valueForPathChanged:(TreePath *)treePath obj:(id)obj;
- (void) addTreeModelListener:(TreeModelListener *)treeModelListener;
- (void) removeTreeModelListener:(TreeModelListener *)treeModelListener;
@end
