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
			"t(x,y) ::= \"<u1()>\"\n"+
			"u1() ::= \"<u2()>\"\n"+
			"u2() ::= \"<u3()>\"\n"+
			"u3() ::= \"<u4()>\"\n"+
			"u4() ::= \"<u5()>\"\n"+
			"u5() ::= \"<u6()>\"\n"+
			"u6() ::= \"<u7()>\"\n"+
			"u7() ::= \"<u8()>\"\n"+
			"u8() ::= \"<u9()>\"\n"+
			"u9() ::= \"<u10()>\"\n"+
			"u10() ::= \"<u11()>\"\n"+
			"u11() ::= \"<u12()>\"\n"+
			"u12() ::= \"<u13()>\"\n"+
			"u13() ::= \"<u14()>\"\n"+
			"u14() ::= \"<u15()>\"\n"+
			"u15() ::= \"<u16()>\"\n"+
			"u16() ::= \"<z()>\"\n"+
			"z() ::= \"<x><y>\"\n";
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
