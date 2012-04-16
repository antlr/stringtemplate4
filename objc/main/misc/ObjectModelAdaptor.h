/*
 * [The "BSD license"]
 *  Copyright (c) 2011 Terence Parr and Alan Condit
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
#import "ModelAdaptor.h"
#import "ST.h"
#import <objc/runtime.h>

@class DoubleKeyMap;

typedef enum {
    TP_CHAR,
    TP_SHORT,
    TP_INT,
    TP_LONG,
    TP_FLOAT,
    TP_DOUBLE,
    TP_STRING,
    TP_OBJECT
} typeDefEnum;

@interface Field : NSObject {
    NSString *name;
    NSInteger type;
    NSString *typeName;
    NSString *attr;
    char tChar;
    char pChar;
    id obj;
}

+ (id) newField:(NSString *)aName obj:(id)instObj;
- (id) init:(NSString *)aName obj:instObj;
- (Class) getClass;
- (id) getObj;

@property (retain) NSString *name;
@property (assign) NSInteger type;
@property (retain) NSString *typeName;
@property (retain) NSString *attr;
@property (assign) char tChar;
@property (assign) char pChar;
@property (retain) id obj;

@end

@interface OBJCMethod : NSObject {
    NSString *name;
    NSString *selString;
    id obj;
}

+ (id) newOBJCMethod:(NSString *)methodName obj:(id)anObj sel:(SEL)aSel;
+ (id) newOBJCMethod:(NSString *)methodName obj:(id)anObj selString:(NSString *)aSelString;
- (id) init:(NSString *)methodName obj:(id)anObj selString:(NSString *)aSelString;

- (void) dealloc;
- (id) invoke:(id)obj;
- (id) getObj;
//- (objc_property_t) getProperty:(Class)c propertyName:(NSString *)propertyName;

@property (retain) NSString *name;
@property (retain) NSString *selString;
@property (retain) id obj;

@end

@interface ObjectModelAdaptor : NSObject <ModelAdaptor> {
    
    /**
     * Cache exact attribute type and property name reflection Member object
     */
    DoubleKeyMap *classAndPropertyToMemberCache;
}

+ (id) newModelAdaptor;

- (id) init;

- (void) dealloc;
- (id) getProperty:(Interpreter *)interp who:(ST *)aWho obj:(id)anObj property:(id)aProperty propertyName:(NSString *)aPropertyName;
- (id) lookupMethod:(id)anObj propertyName:(NSString *)aPropertyName value:(id)value aClass:(Class)c;
- (NSString *)convertToString:(id)anObj propertyName:(NSString *)aPropertyName;
- (id) throwNoSuchProperty:(NSString *)aPropertyName;

@property (retain) DoubleKeyMap *classAndPropertyToMemberCache;

@end
