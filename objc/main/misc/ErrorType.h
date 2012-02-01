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

#import <ANTLR/ANTLR.h>

/**
 * All the errors that can happen and how to generate a message
 */

typedef enum {
    NO_SUCH_TEMPLATE,
    CANT_SET_ATTRIBUTE,
    NO_IMPORTED_TEMPLATE,
    NO_SUCH_ATTRIBUTE,
    REF_TO_IMPLICIT_ATTRIBUTE_OUT_OF_SCOPE,
    MISSING_FORMAL_ARGUMENTS,
    NO_SUCH_PROPERTY,
    MAP_ARGUMENT_COUNT_MISMATCH,
    ARGUMENT_COUNT_MISMATCH,
    EXPECTING_STRING,
    WRITER_CTOR_ISSUE,
    CANT_IMPORT,
    SYNTAX_ERROR,
    TEMPLATE_REDEFINITION,
    EMBEDDED_REGION_REDEFINITION,
    REGION_REDEFINITION,
    MAP_REDEFINITION,
    ALIAS_TARGET_UNDEFINED,
    TEMPLATE_REDEFINITION_AS_MAP,
    LEXER_ERROR,
    NO_DEFAULT_VALUE,
    NO_SUCH_FUNCTION,
    NO_SUCH_REGION,
    NO_SUCH_OPTION,
    INVALID_TEMPLATE_NAME,
    ANON_ARGUMENT_MISMATCH,
    REQUIRED_PARAMETER_AFTER_OPTIONAL,
    INTERNAL_ERROR,
    WRITE_IO_ERROR,
    CANT_LOAD_GROUP_FILE,
    NUM_OF_ERRORENUMS
} ErrorTypeEnum;

// extern static NSString *ErrorType_Data[NUM_OF_ERRORENUMS];

@interface ErrorType : NSObject {
    NSString *message;
    AMutableDictionary *msgs;
}

+ (NSString *) NO_SUCH_TEMPLATE;
+ (NSString *) CANT_SET_ATTRIBUTE;
+ (NSString *) NO_IMPORTED_TEMPLATE;
+ (NSString *) NO_SUCH_ATTRIBUTE;
+ (NSString *) REF_TO_IMPLICIT_ATTRIBUTE_OUT_OF_SCOPE;
+ (NSString *) MISSING_FORMAL_ARGUMENTS;
+ (NSString *) NO_SUCH_PROPERTY;
+ (NSString *) MAP_ARGUMENT_COUNT_MISMATCH;
+ (NSString *) ARGUMENT_COUNT_MISMATCH;
+ (NSString *) EXPECTING_STRING;
+ (NSString *) WRITER_CTOR_ISSUE;
+ (NSString *) CANT_IMPORT;

// COMPILE-TIME SYNTAX/SEMANTIC ERRORS
+ (NSString *) SYNTAX_ERROR;
+ (NSString *) TEMPLATE_REDEFINITION;
+ (NSString *) EMBEDDED_REGION_REDEFINITION;
+ (NSString *) REGION_REDEFINITION;
+ (NSString *) MAP_REDEFINITION;
+ (NSString *) ALIAS_TARGET_UNDEFINED;
+ (NSString *) TEMPLATE_REDEFINITION_AS_MAP;
+ (NSString *) LEXER_ERROR;
+ (NSString *) NO_DEFAULT_VALUE;
+ (NSString *) NO_SUCH_FUNCTION;
+ (NSString *) NO_SUCH_REGION;
+ (NSString *) NO_SUCH_OPTION;
+ (NSString *) INVALID_TEMPLATE_NAME;
+ (NSString *) ANON_ARGUMENT_MISMATCH;
+ (NSString *) REQUIRED_PARAMETER_AFTER_OPTIONAL;
// INTERNAL ERRORS
+ (NSString *) INTERNAL_ERROR;
+ (NSString *) WRITE_IO_ERROR;
+ (NSString *) CANT_LOAD_GROUP_FILE;
+ (NSString *) ErrorNum:(NSInteger)anErr;

+ (id) newErrorType;
+ (id) newErrorTypeWithErrNum:(NSInteger) msgNum;
+ (id) newErrorTypeWithMsg:(NSString *) aMsg;

- (id) init;
- (id) initWithErrNum:(NSInteger) aNum;
- (id) initWithMsg:(NSString *) m;
- (void) dealloc;

- (NSInteger) ErrorTypeValueOf:(NSString *)text;
- (NSString *) description;
- (NSString *) description:(NSInteger) value;

// getters and setters

@property (retain) NSString *message;
@property (retain) AMutableDictionary *msgs;

@end
