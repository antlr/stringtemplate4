//
//  Aggregate.h
//  ST4
//
//  Created by Alan Condit on 2/24/12.
//  Copyright 2012 Alan Condit. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <ANTLR/AMutableDictionary.h>

@interface Aggregate : NSObject {
    AMutableDictionary *props;
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
- (void) put:(NSString *)propName Object:(id) propValue;
- (id) get:(NSString *)propName;
- (NSString *)toString;
- (NSString *)description;

@property (retain) AMutableDictionary *props;
@end
