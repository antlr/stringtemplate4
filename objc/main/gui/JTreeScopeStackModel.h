#import <ANTLR/ANTLR.h>
#import <Foundation/Foundation.h>
#import "InstanceScope.h"
#import "Interpreter.h"
#import "ST.h"
#import "AddAttributeEvent.h"
#import "TreeModelListener.h"
#import "TreeModel.h"
#import "TreePath.h"

@interface StringTree : CommonTree {
  NSMutableString *text;
}

@property(nonatomic, retain) NSMutableString *text;
+ (StringTree *) newStringTree:(NSString *)text;
- (id) initWithText:(NSString *)text;
- (BOOL) hasText;
- (NSString *) description;
@end

/**
 * From a scope, get stack of enclosing scopes in order from root down
 * to scope.  Then show each scope's (ST's) attributes as children.
 */

@interface JTreeScopeStackModel : NSObject <TreeModel> {
  CommonTree *root;
}

@property(nonatomic, retain, readonly) id root;
+ (JTreeScopeStackModel *)newModel:(InstanceScope *)scope;
- (id) initWithScope:(InstanceScope *)scope;
- (void) addAttributeDescriptions:(ST *)st node:(StringTree *)node;
- (id) getChild:(id)parent index:(NSInteger)i;
- (NSInteger) getChildCount:(id)parent;
- (BOOL) isLeaf:(id)node;
- (NSInteger) getIndexOfChild:(id)parent child:(id)child;
- (void) valueForPathChanged:(TreePath *)treePath obj:(id)obj;
- (void) addTreeModelListener:(TreeModelListener *)treeModelListener;
- (void) removeTreeModelListener:(TreeModelListener *)treeModelListener;
@end
