#import "Interpreter.h"
#import "ST.h"
#import "EvalTemplateEvent.h"
#import "TreeModelListener.h"
#import "TreeModel.h"
#import "TreePath.h"

@interface Wrapper : NSObject {
  EvalTemplateEvent *event;
}

- (id) newWrapper:(EvalTemplateEvent *)event;
- (id) initWithEvent:(EvalTemplateEvent *)event;
- (BOOL) isEqualTo:(id)obj;
- (NSInteger) hash;
- (NSString *) description;
@end

@interface JTreeSTModel : NSObject <TreeModel> {
  Interpreter *interp;
  Wrapper *root;
}

@property(nonatomic, retain) Wrapper *root;
@property (retain, getter=getInterp, setter=setInterp:) Interpreter *interp;
+ (JTreeSTModel *) newJTSTModel:(Interpreter *)interp root:(EvalTemplateEvent *)root;
- (id) init:(Interpreter *)interp root:(EvalTemplateEvent *)root;
- (id) getChild:(id)parent index:(NSInteger)index;
- (NSInteger) getChildCount:(id)parent;
- (NSInteger) getIndexOfChild:(id)parent child:(id)child;
- (BOOL) isLeaf:(id)node;
- (void) valueForPathChanged:(TreePath *)treePath obj:(id)obj;
- (void) addTreeModelListener:(TreeModelListener *)treeModelListener;
- (void) removeTreeModelListener:(TreeModelListener *)treeModelListener;
@end
