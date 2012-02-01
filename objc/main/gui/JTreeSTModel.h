#import "Interpreter.h"
//#import "DebugST.h"
#import "InterpEvent.h"
#import "TreeModelListener.h"
#import "TreeModel.h"
#import "TreePath.h"

@interface Wrapper : NSObject {
  DebugST *st;
}

- (id) initWithSt:(DebugST *)st;
- (BOOL) isEqualTo:(id)obj;
- (NSInteger) hash;
- (NSString *) toString;
- (NSString *) description;
@end

@interface JTreeSTModel : NSObject <TreeModel> {
  Wrapper *root;
  Interpreter *interp;
}

@property(retain, getter=getRoot, setter=setRoot:) Wrapper *root;
@property (retain, getter=getInterp, setter=setInterp:) Interpreter *interp;
- (id) init:(Interpreter *)interp root:(DebugST *)root;
- (NSInteger) getChildCount:(id)parent;
- (NSInteger) getIndexOfChildDBG:(DebugST *)parent child:(DebugST *)child;
- (NSInteger) getIndexOfChild:(id)parent child:(id)child;
- (id) getChild:(id)parent index:(NSInteger)index;
- (BOOL) isLeaf:(id)node;
- (void) valueForPathChanged:(TreePath *)treePath obj:(id)obj;
- (void) addTreeModelListener:(TreeModelListener *)treeModelListener;
- (void) removeTreeModelListener:(TreeModelListener *)treeModelListener;
@end
