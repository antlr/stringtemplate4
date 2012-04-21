//
//  Aggregate.h
//  ST4
//
//  Created by Alan Condit on 2/24/12.
//  Copyright 2012 Alan Condit. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <ANTLR/LinkedHashMap.h>

@interface Aggregate : NSObject {
    LinkedHashMap *props;
	/** Allow StringTemplate to add values, but prevent the end
	 *  user from doing so.
	 */
}

+ newAggregate;
/** Allow StringTemplate to add values, but prevent the end
 *  user from doing so.
 */

- (id)init;
- (void) dealloc;
- (void) put:(NSString *)propName value:(id) propValue;
- (id) get:(NSString *)propName;
- (NSString *)description;
- (NSString *)toString;

@property (retain) LinkedHashMap *props;
@end
