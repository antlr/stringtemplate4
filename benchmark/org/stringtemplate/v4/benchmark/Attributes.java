package org.stringtemplate.v4.benchmark;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

public class Attributes {
	public static class User {
		public int id;
		public String name;
		public User(int id, String name) { this.id = id; this.name = name; }
		public String getName() { return name; }
	}

	public static final String tmpdir = System.getProperty("java.io.tmpdir");

	public void time2Args(int reps) {
		String templates =
			"t(x,y) ::= \"<x><y>\"\n";
		Misc.writeFile(tmpdir, "t.stg", templates);
		STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
		ST st = group.getInstanceOf("t");
		st.add("x", 1);
		st.add("y", 2);

		for (int i = 0; i < reps; i++) {
			st.render();
		}
	}

	public void timeLotsOfArgs(int reps) {
		String templates =
			"t(a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z) ::=" +
				" \"<a>,<b>,<c>,<d>,<e>,<f>,<g>,<h>,<i>,<j>,<k>,<l>,<m>,<n>,<o>,<p>,<q>,<r>,<s>,<t>,<u>,<v>,<w>,<x>,<y>,<z>\"\n";
		Misc.writeFile(tmpdir, "t.stg", templates);
		STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
		ST st = group.getInstanceOf("t");
		st.add("x", 1);
		st.add("y", 2);

		for (int i = 0; i < reps; i++) {
			st.render();
		}
	}

	public void timeSimplePropsOfArgs(int reps) {
		String templates =
			"t(x) ::= \"<x.id><x.name><x.id><x.name><x.id><x.name>\"\n";
		Misc.writeFile(tmpdir, "t.stg", templates);
		STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
		ST st = group.getInstanceOf("t");
		st.add("x", new User(32,"parrt"));

		for (int i = 0; i < reps; i++) {
			st.render();
		}
	}

	public void timeDynamicAttributeLookup(int reps) {
		String templates =
			"t(x,y) ::= \"<u()>\"\n"+
			"u() ::= \"<x><y>\"\n";
		Misc.writeFile(tmpdir, "t.stg", templates);
		STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
		ST st = group.getInstanceOf("t");
		st.add("x", 1);
		st.add("y", 2);

		for (int i = 0; i < reps; i++) {
			st.render();
		}
	}

	public void timeDeepDynamicLookup(int reps) {
		String templates =
			"t(x,y) ::= \"<u1({eh})>\"\n"+
			"u1(a) ::= \"<u2({eh})>\"\n"+
			"u2(a) ::= \"<u3({eh})>\"\n"+
			"u3(a) ::= \"<u4({eh})>\"\n"+
			"u4(a) ::= \"<u5({eh})>\"\n"+
			"u5(a) ::= \"<u6({eh})>\"\n"+
			"u6(a) ::= \"<u7({eh})>\"\n"+
			"u7(a) ::= \"<u8({eh})>\"\n"+
			"u8(a) ::= \"<u9({eh})>\"\n"+
			"u9(a) ::= \"<u10({eh})>\"\n"+
			"u10(a) ::= \"<u11({eh})>\"\n"+
			"u11(a) ::= \"<u12({eh})>\"\n"+
			"u12(a) ::= \"<u13({eh})>\"\n"+
			"u13(a) ::= \"<u14({eh})>\"\n"+
			"u14(a) ::= \"<u15({eh})>\"\n"+
			"u15(a) ::= \"<u16({eh})>\"\n"+
			"u16(a) ::= \"<z({eh})>\"\n"+
			"z(a) ::= \"<x><y>\"\n";
		Misc.writeFile(tmpdir, "t.stg", templates);
		STGroup group = new STGroupFile(tmpdir+"/"+"t.stg");
		ST st = group.getInstanceOf("t");
		st.add("x", 1);
		st.add("y", 2);

		for (int i = 0; i < reps; i++) {
			st.render();
		}
	}

}
