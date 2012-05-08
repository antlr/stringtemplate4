//
//  MemBuffer.h
//  ANTLR
//
//  Created by Alan Condit on 6/9/10.
// [The "BSD licence"]
// Copyright (c) 2010 Alan Condit
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions
// are met:
// 1. Redistributions of source code must retain the above copyright
//    notice, this list of conditions and the following disclaimer.
// 2. Redistributions in binary form must reproduce the above copyright
//    notice, this list of conditions and the following disclaimer in the
//    documentation and/or other materials provided with the distribution.
// 3. The name of the author may not be used to endorse or promote products
//    derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
// IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
// OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
// IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
// INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
// NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
// DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
// THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
// THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

#import <Foundation/Foundation.h>

//#define GLOBAL_SCOPE       0
//#define LOCAL_SCOPE        1
#define BUFFSIZE         101

@interface MemBuffer : NSObject {
	//MemBuffer *fNext;
    NSInteger BuffSize;
    __strong NSMutableData *buffer;
    __strong char *ptrBuffer;
    NSInteger count;
    NSInteger ptr;
}

@property (assign) NSInteger BuffSize;
@property (retain) NSMutableData *buffer;
@property (assign) char *ptrBuffer;
@property (assign, getter=getCount, setter=setCount:) NSInteger count;
@property (assign) NSInteger ptr;

// Contruction/Destruction
+(MemBuffer *)newMemBuffer;
+(MemBuffer *)newMemBufferWithLen:(NSInteger)cnt;
-(id)init;
-(id)initWithLen:(NSInteger)cnt;
-(void)dealloc;

// Instance Methods
- (id) copyWithZone:(NSZone *)aZone;
/* clear -- reinitialize the maplist array */
- (void) clear;

- (NSInteger)count;
- (NSInteger)length;
- (NSInteger)size;

- (void) push:(char) v;
- (char) pop;
- (char) peek;

- (void) addChar:(char) v;
- (void) addCharsFromArray:(MemBuffer *)anArray;
- (void) insertChar:(short)aChar atIndex:(NSInteger)idx;
- (char) charAtIndex:(NSInteger)idx;
- (void) removeAllChars;

- (short) shortAtIndex:(NSInteger)idx;
- (void) insertShort:(short)aVal atIndex:(NSInteger)idx;

- (void) ensureCapacity:(NSInteger) index;
- (NSString *) description;
- (void) memcpy:(NSInteger)src dest:(NSInteger)dest length:(NSInteger)len;

@end
