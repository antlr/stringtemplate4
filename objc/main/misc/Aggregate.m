//
//  Aggregate.m
//  ST4
//
//  Created by Alan Condit on 2/24/12.
//  Copyright 2012 Alan Condit. All rights reserved.
//

#import "Aggregate.h"


/** An automatically created aggregate of properties.
 *
 *  I often have lists of things that need to be formatted, but the list
 *  items are actually pieces of data that are not already in an object.  I
 *  need ST to do something like:
 *
 *  Ter=3432
 *  Tom=32234
 *  ....
 *
 *  using template:
 *
 *  $items:{it.name$=$it.type$}$
 *
 *  This example will call getName() on the objects in items attribute, but
 *  what if they aren't objects?  I have perhaps two parallel arrays
 *  instead of a single array of objects containing two fields.  One
 *  solution is allow Maps to be handled like properties so that it.name
 *  would fail getName() but then see that it's a Map and do
 *  it.get("name") instead.
 *
 *  This very clean approach is espoused by some, but the problem is that
 *  it's a hole in my separation rules.  People can put the logic in the
 *  view because you could say: "go get bob's data" in the view:
 *
 *  Bob's Phone: $db.bob.phone$
 *
 *  A view should not be part of the program and hence should never be able
 *  to go ask for a specific person's data.
 *
 *  After much thought, I finally decided on a simple solution.  I've
 *  added setAttribute variants that pass in multiple property values,
 *  with the property names specified as part of the name using a special
 *  attribute name syntax: "name.{propName1,propName2,...}".  This
 *  object is a special kind of HashMap that hopefully prevents people
 *  from passing a subclass or other variant that they have created as
 *  it would be a loophole.  Anyway, the ASTExpr.getObjectProperty()
 *  method looks for Aggregate as a special case and does a get() instead
 *  of getPropertyName.
 */
@implementation Aggregate

@synthesize props;

+ newAggregate
{
    return [[Aggregate alloc] init];
}
/** Allow StringTemplate to add values, but prevent the end
 *  user from doing so.
 */

- (id)init
{
    self = [super init];
    if (self) {
        // Initialization code here.
        props = [AMutableDictionary dictionaryWithCapacity:5];
    }
    
    return self;
}

- (void)dealloc
{
    if ( props ) [props release];
    [super dealloc];
}

- (void) put:(NSString *)propName Object:(id) propValue
{
    [props setObject:propValue forKey:propName];
}

- (id) get:(NSString *)propName
{
    return [props objectForKey:propName];
}

- (NSString *)toString
{
    return [props description];
}

- (NSString *)description
{
    NSString *desc = [props description];
    if ( desc == nil ) desc = @"props=<nil>";
    return desc;
}

@end
