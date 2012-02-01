grammar t1.g;

a	: b
	| ID
	;

b	: {p1}? ID
	| {p2}? ID
	;

ID 	:	['a'..'z'|'A'..'Z']+;