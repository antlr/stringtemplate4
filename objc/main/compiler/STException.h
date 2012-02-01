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
#import <ANTLR/Antlr.h>
#import "InstanceScope.h"

@interface STException : RuntimeException {
}
+ (id) newException:(NSString *)aReason;

- (id) init;
- (id) initWithName:(NSString *)aMsg reason:(NSString *)aCause;
- (id) initWithName:(NSString *)aMsg reason:(NSString *)aCause userInfo:(NSDictionary *)userInfo;
@end

@interface STNoSuchAttributeException : RuntimeException {
    InstanceScope *scope;
	NSString *attrName;
}

+ (id) newException:(NSString *)aReason;

- (id) initWithName:(NSString *)aName reason:(NSString *)aReason;
- (id) initWithName:(NSString *)aMsg reason:(NSString *)aCause userInfo:(NSDictionary *)userInfo;

- (NSString *) getMessage;

@property (retain) InstanceScope *scope;
@property (retain) NSString *attrName;

@end

@interface STNoSuchMethodException : RuntimeException
{
    NSString *methodName;
}

+ (id) newException:(NSString *)aReason;

- (id) initWithName:(NSString *)aName reason:(NSString *)aReason;
- (id) initWithName:(NSString *)aMsg reason:(NSString *)aCause userInfo:(NSDictionary *)userInfo;

@property (retain) NSString *methodName;
@end

@interface STNoSuchPropertyException : RuntimeException
{
    NSString *propertyName;
}

+ (id) newException:(NSString *)aReason;

- (id) initWithName:(NSString *)aName reason:(NSString *)aReason;
- (id) initWithName:(NSString *)aMsg reason:(NSString *)aCause userInfo:(NSDictionary *)userInfo;

@property (retain) NSString *propertyName;
@end

@interface FileNotFoundException : RuntimeException {
}

+ (id) newException:(NSString *)aReason;

- (id) initWithName:(NSString *)aName reason:(NSString *)aReason;
- (id) initWithName:(NSString *)aMsg reason:(NSString *)aCause userInfo:(NSDictionary *)userInfo;

@end

@interface IllegalAccessException : RuntimeException {
}

+ (id) newException:(NSString *)aReason;

- (id) initWithName:(NSString *)aName reason:(NSString *)aReason;
- (id) initWithName:(NSString *)aMsg reason:(NSString *)aCause userInfo:(NSDictionary *)userInfo;

@end

@interface MalformedURLException : RuntimeException {
}

+ (id) newException:(NSString *)aReason;

- (id) initWithName:(NSString *)aName reason:(NSString *)aReason;
- (id) initWithName:(NSString *)aMsg reason:(NSString *)aCause userInfo:(NSDictionary *)userInfo;

@end

@interface IOException : RuntimeException {
}

+ (id) newException:(NSString *)aReason;

- (id) initWithName:(NSString *)aName reason:(NSString *)aReason;
- (id) initWithName:(NSString *)aMsg reason:(NSString *)aCause userInfo:(NSDictionary *)userInfo;

@end
