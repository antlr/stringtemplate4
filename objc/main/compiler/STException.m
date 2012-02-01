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
#import "STException.h"

@implementation STException

+ (id) newException:(NSString *)aReason
{
    return [[STException alloc] initWithName:@"ST Exception" reason:aReason userInfo:nil];
}

- (id) init
{
    self=[super initWithName:@"ST Exception" reason:nil userInfo:nil];
    return self;
}

- (id) initWithName:(NSString *)aName reason:(NSString *)aReason
{
    self=[super initWithName:aName reason:aReason userInfo:nil];
    return self;
}

- (id) initWithName:(NSString *)aName reason:(NSString *)aReason userInfo:(NSDictionary *)userInfo
{
    self=[super initWithName:aName reason:aReason userInfo:userInfo];
    return self;
}

@end

@implementation STNoSuchAttributeException

+ (id) newException:(NSString *)aReason
{
    return [[STNoSuchAttributeException alloc] initWithName:@"ST No Such Attribute Exception" reason:aReason];
}

- (id) initWithName:(NSString *)aName reason:(NSString *)aReason
{
    self=[super initWithName:aName reason:aReason userInfo:nil];
    if (  self != nil ) {
    }
    return self;
}

- (id) initWithName:(NSString *)aName reason:(NSString *)aReason userInfo:(NSDictionary *)userInfo
{
    self=[super initWithName:aName reason:aReason userInfo:userInfo];
    if (  self != nil ) {
    }
    return self;
}

- (NSString *) getMessage
{
    return [NSString stringWithFormat:@"from template %@ no attribute %@ is visible", [scope.st getName], attrName];
}

@synthesize scope;
@synthesize attrName;

@end

@implementation STNoSuchMethodException

+ (id) newException:(NSString *)aReason
{
    return [[STNoSuchMethodException alloc] initWithName:@"ST No Such Method Exception" reason:aReason];
}

- (id) initWithName:(NSString *)aName reason:(NSString *)aReason
{
    self=[super initWithName:aName reason:aReason userInfo:nil];
    if (  self != nil ) {
        methodName = aReason;
    }
    return self;
}

- (id) initWithName:(NSString *)aName reason:(NSString *)aReason userInfo:(NSDictionary *)userInfo
{
    self=[super initWithName:aName reason:aReason userInfo:userInfo];
    if (  self != nil ) {
        methodName = aReason;
    }
    return self;
}

@synthesize methodName;
@end

@implementation STNoSuchPropertyException

+ (id) newException:(NSString *)aReason
{
    return [[STNoSuchPropertyException alloc] initWithName:@"ST No Such Property Exception" reason:aReason];
}

- (id) initWithName:(NSString *)aName reason:(NSString *)aReason
{
    self=[super initWithName:aName reason:aReason userInfo:nil];
    if (  self != nil ) {
        propertyName = aReason;
    }
    return self;
}

- (id) initWithName:(NSString *)aName reason:(NSString *)aReason userInfo:(NSDictionary *)userInfo
{
    self=[super initWithName:aName reason:aReason userInfo:userInfo];
    if (  self != nil ) {
        propertyName = aReason;
    }
    return self;
}

@synthesize propertyName;
@end

@implementation FileNotFoundException

+ (id) newException:(NSString *)aReason
{
    return [[FileNotFoundException alloc] initWithName:@"File Not Found Exception" reason:aReason userInfo:nil];
}

- (id) initWithName:(NSString *)aName reason:(NSString *)aReason
{
    self=[super initWithName:aName reason:aReason userInfo:nil];
    return self;
}

- (id) initWithName:(NSString *)aName reason:(NSString *)aReason userInfo:(NSDictionary *)userInfo
{
    if ( (self=[super initWithName:aName reason:aReason userInfo:userInfo]) != nil ) {
    }
    return self;
}

@end

@implementation IllegalAccessException

+ (id) newException:(NSString *)aReason
{
    return [[IllegalAccessException alloc] initWithName:@"Illegal Access Exception" reason:aReason userInfo:nil];
}

- (id) initWithName:(NSString *)aName reason:(NSString *)aReason
{
    self=[super initWithName:aName reason:aReason userInfo:nil];
    return self;
}

- (id) initWithName:(NSString *)aName reason:(NSString *)aReason userInfo:(NSDictionary *)userInfo
{
    self=[super initWithName:aName reason:aReason userInfo:userInfo];
    return self;
}

@end

@implementation MalformedURLException

+ (id) newException:(NSString *)aReason
{
    return [[MalformedURLException alloc] initWithName:@"Malformed URL Exception" reason:aReason userInfo:nil];
}

- (id) initWithName:(NSString *)aName reason:(NSString *)aReason
{
    self=[super initWithName:aName reason:aReason userInfo:nil];
    return self;
}

- (id) initWithName:(NSString *)aName reason:(NSString *)aReason userInfo:(NSDictionary *)userInfo
{
    self=[super initWithName:aName reason:aReason userInfo:userInfo];
    return self;
}

@end

@implementation IOException

+ (id) newException:(NSString *)aReason
{
    return [[IOException alloc] initWithName:@"IO Exception" reason:aReason userInfo:nil];
}

- (id) initWithName:(NSString *)aName reason:(NSString *)aReason
{
    self=[super initWithName:aName reason:aReason userInfo:nil];
    return self;
}

- (id) initWithName:(NSString *)aName reason:(NSString *)aReason userInfo:(NSDictionary *)userInfo
{
    self=[super initWithName:aName reason:aReason userInfo:userInfo];
    return self;
}

@end

