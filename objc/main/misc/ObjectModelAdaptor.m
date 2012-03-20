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
#import <Cocoa/Cocoa.h>
#import <ANTLR/ANTLR.h>
#import "STErrorListener.h"
#import "ObjectModelAdaptor.h"
#import "Misc.h"
#import "STException.h"


@implementation Field

@synthesize name;
@synthesize type;
@synthesize typeName;
@synthesize obj;

+ (id) newField:(NSString *)aName obj:(id)instObj
{
    return [[Field alloc] init:aName obj:instObj];
}

- (id) init:(NSString *)aName obj:instObj
{
    self=[super init];
    if ( self != nil ) {
        const char *tmp;
        tChar = '\0';
        pChar = '\0';
        name = aName;
        obj = instObj;
        tmp = property_getAttributes(class_getProperty([obj class], [name UTF8String]));
        attr = [NSString stringWithCString:tmp encoding:NSASCIIStringEncoding];
        if ([attr characterAtIndex:0] == 'T') {
            tChar = [attr characterAtIndex:1];
            if (tChar == '^') {
                pChar = '*';
                tChar = [attr characterAtIndex:2];
            }
            switch (tChar) {
                case 'c':
                    typeName = @"char";
                    break;
                case 'd':
                    typeName = @"double";
                    break;
                case 'f':
                    typeName = @"float";
                    break;
                case 'i':
                    typeName = @"int";
                    break;
                case 'l':
                    typeName = @"long";
                    break;
                case 's':
                    typeName = @"short";
                    break;
                case 'u':
                    typeName = @"unsigned";
                    break;
                case 'v':
                    typeName = @"void";
                    break;
                case '{':
                    typeName = @"struct/union";
                    break;
                case '?':
                    typeName = @"pointer to func";
                    break;
                case '@':
                    typeName = @"id";
                    break;
            }
            attr = typeName;
            if ( pChar == '*' ) attr = [NSString stringWithFormat:@"%@ %c", typeName, pChar];
        }
    }
    return self;
}

- (Class) getClass
{
    return [obj class];
}

- (id) getObj
{
    return obj;
}

- (objc_property_t) getProperty:(Class)c propertyName:(NSString *)propertyName
{
    const char *property = [propertyName cStringUsingEncoding:NSASCIIStringEncoding];
    //    const char *aName = class_getName(c);    //NSStringFromClass(c);
    //    name = [NSString stringWithFormat:@"%s", aName];
    name = NSStringFromClass(c);
    return class_getProperty(c, property);
}

@synthesize attr;
@synthesize tChar;
@synthesize pChar;
@end

@implementation OBJCMethod

@synthesize name;
@synthesize selString;
@synthesize obj;

+ (id) newOBJCMethod:(NSString *)methodName obj:(id)anObj selString:(NSString *)aSelString
{
    return [[OBJCMethod alloc] init:methodName obj:(id)anObj selString:aSelString];
}

+ (id) newOBJCMethod:(NSString *)methodName obj:(id)anObj sel:(SEL)aSel
{
    return [[OBJCMethod alloc] init:methodName obj:(id)anObj selString:NSStringFromSelector(aSel)];
}

- (id) init:(NSString *)methodName obj:(id)anObj selString:(NSString *)aSelString
{
    self=[super init];
    if ( self != nil ) {
        name = methodName;
        selString = aSelString;
        obj = anObj;
    }
    return self;
}

- (void) dealloc
{
#ifdef DEBUG_DEALLOC
    NSLog( @"called dealloc in OBJCMethod" );
#endif
    if ( name ) [name release];
    if ( selString ) [selString release];
    if ( obj ) [obj release];
    [super dealloc];
}

- (Class) getClass
{
    return [obj class];
}

- (id) getObj
{
    return obj;
}

- (id) invoke:(id)anObj
{
    SEL aSEL =  NSSelectorFromString(selString);
    return [obj performSelector:aSEL];
}

@end


@implementation ObjectModelAdaptor

@synthesize classAndPropertyToMemberCache;

/**
 * Cached exception to reuse since creation is expensive part.
 * Just in case people use "missing" to mean boolean false not error.
 */
static STNoSuchPropertyException *cachedException;

+ (id) newObjectModelAdaptor
{
    return [[ObjectModelAdaptor alloc] init];
}

- (id) init
{
    self=[super init];
    if ( self != nil ) {
        classAndPropertyToMemberCache = [[DoubleKeyMap alloc] init];
    }
    return self;
}

- (void) dealloc
{
#ifdef DEBUG_DEALLOC
    NSLog( @"called dealloc in ObjectModelAdaptor" );
#endif
    if ( classAndPropertyToMemberCache ) [classAndPropertyToMemberCache release];
    [super dealloc];
}

- (id) getProperty:(Interpreter *)interp who:(ST *)aWho obj:(id)anObj property:(id)aProperty propertyName:(NSString *)aPropertyName
{
    id value = nil;
    Class c = [anObj class];
    if (aProperty == nil) {
        return [self throwNoSuchProperty:[NSString stringWithFormat:@"%@.%@", NSStringFromClass(c), aPropertyName]];
    }
	// Look in cache for Member first
    id member = [classAndPropertyToMemberCache objectForKey1:NSStringFromClass(c) forKey2:aPropertyName];
    if (member != nil) {
        @try {
            SEL aSEL = NSSelectorFromString(member);
            return [anObj performSelector:aSEL];
            OBJCMethod *aMeth = [OBJCMethod newOBJCMethod:member obj:anObj sel:aSEL];
            if ([member getClass] == [OBJCMethod class])
                return [aMeth invoke:anObj];
            if ([member getClass] == [Field class])
                return [(Field *)member getObj];
        }
        @catch (NSException *e) {
            [self throwNoSuchProperty:[NSString stringWithFormat:@"%@.%@", NSStringFromClass(c), aPropertyName]];
        }
    }
    return [self lookupMethod:anObj propertyName:aPropertyName value:(id)value aClass:(Class) c];
}

- (id) lookupMethod:(id)anObj propertyName:(NSString *)aPropertyName value:(id)value aClass:(Class)c
{
    // try getXXX and isXXX properties, look up using reflection
    NSString *methodSuffix = [NSString stringWithFormat:@"%c%@",
                              toupper([aPropertyName characterAtIndex:0]),
                              [aPropertyName substringWithRange:NSMakeRange(1, [aPropertyName length]-1)]];
    SEL m = [Misc getMethod:aPropertyName];
    if ( m == nil || ![anObj respondsToSelector:m] ) {
        m = [Misc getMethod:[NSString stringWithFormat:@"get%@", methodSuffix]];;
        if ( m == nil || ![anObj respondsToSelector:m] ) {
            m = [Misc getMethod:[NSString stringWithFormat:@"is%@", methodSuffix]];
            if ( m == nil || ![anObj respondsToSelector:m] ) {
                m = [Misc getMethod:[NSString stringWithFormat:@"has%@", methodSuffix]];
            }
        }
    }
    @try {
        void *var;
        if ( m != nil ) {
            [classAndPropertyToMemberCache setObject:NSStringFromSelector(m) forKey1:NSStringFromClass(c) forKey2:aPropertyName];

            //[classAndPropertyToMemberCache setObject:c forKey1:aPropertyName forKey2:[m className]];
            value = [self convertToString:anObj propertyName:aPropertyName];
        }
        else {
            // try for a visible field
            Ivar f;
            f = object_getInstanceVariable(anObj, [aPropertyName cStringUsingEncoding:NSASCIIStringEncoding], &var);
            [classAndPropertyToMemberCache setObject:(id)f forKey1:NSStringFromClass(c) forKey2:aPropertyName];
            @try {
                //value = [Misc accessField:(Ivar)f obj:anObj value:value];
                value = object_getIvar(anObj, f);
            }
            @catch (IllegalAccessException *iae) {
                [self throwNoSuchProperty:[NSString stringWithFormat:@"%@.%@", NSStringFromClass(c), aPropertyName]];
            }
        }
    }
    @catch (NSException *e) {
        [self throwNoSuchProperty:[NSString stringWithFormat:@"%@.%@", NSStringFromClass(c), aPropertyName]];
    }
    return value;
}

- (NSString *)convertToString:(id)anObj propertyName:(NSString *)aPropertyName;
{
    NSString *retVal = @"";
    NSString *attr;
    const char *tmp;
    id value;
    char pChar, tChar;
    NSInteger iVal;
    double d;
    float f;
    SEL m = [Misc getMethod:aPropertyName];
    @try {
        value = [Misc invokeMethod:m obj:anObj value:value];
        tmp = property_getAttributes(class_getProperty([anObj class], [aPropertyName UTF8String]));
        attr = [NSString stringWithCString:tmp encoding:NSASCIIStringEncoding];
        if ([attr characterAtIndex:0] == 'T') {
            tChar = [attr characterAtIndex:1];
            if (tChar == '^') {
                pChar = '*';
                tChar = [attr characterAtIndex:2];
            }
            iVal = (NSInteger)value;
            switch (tChar) {
                case 'c':
                    if ( iVal >= 0 && iVal <= 0x20) {
                        switch ( iVal ) {
                            case 0: retVal = @"false"; break;
                            case 1: retVal = @"true"; break;
                            case '\n': retVal = @"\n"; break;
                            case '\r': retVal = @"\r"; break;
                            case '\t': retVal = @"\t"; break;
                            case ' ': retVal = @" "; break;
                            default:
                                retVal = [NSString stringWithFormat:@"0x%x", (char)iVal];
                                break;
                        }
                        break;
                    }
                    else
                        retVal = [NSString stringWithFormat:@"%c", (char)iVal];
                    break;
                case 'd':
                    //                d = (double)[anObj performSelector:m];
                    retVal = [NSString stringWithFormat:@"%f", d];
                    break;
                case 'f':
                    //                f = (float)[anObj performSelector:m];
                    retVal = [NSString stringWithFormat:@"%lf", f];
                    break;
                case 'i':
                    retVal = [NSString stringWithFormat:@"%d", (int)iVal];
                    break;
                case 'l':
                    retVal = [NSString stringWithFormat:@"%ld", (long)iVal];
                    break;
                case 's':
                    retVal = [NSString stringWithFormat:@"%d", (int)value];
                    break;
                case 'u':
                    retVal = [NSString stringWithFormat:@"%u", (int)value];
                    break;
                case 'v':
                    //typeName = @"void";
                    //retVal = (NSString *)[NSNull null];
                    retVal = nil;
                    break;
                case '{':
                    //typeName = @"struct/union";
                    break;
                case '?':
                    //typeName = @"pointer to func";
                    break;
                case '@':
                    if ( [value isKindOfClass:[NSString class]] ) return value;
                    else return [value description];
                    break;
            }
            //attr = typeName;
            //if ( pChar == '*' ) attr = [NSString stringWithFormat:@"%@ %c", typeName, pChar];
        }
    }
    @catch (STNoSuchPropertyException *e) {
        NSLog( @"No such property as \"%@\"", e.reason );
        [self throwNoSuchProperty:[NSString stringWithFormat:@"%@.%@", [anObj class], aPropertyName]];
    }
    return retVal;
}

- (id) throwNoSuchProperty:(NSString *)aPropertyName
{
    if (cachedException == nil)
        cachedException = [STNoSuchPropertyException newException:aPropertyName];
    @throw cachedException;
}

@end
