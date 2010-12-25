package org.stringtemplate.v4.benchmark;

import org.stringtemplate.v4.ST;

public class WriteFixedTemplates {
	public void timeSingle(int reps) {
		String template =
			"A smallish string to write out";
		ST st = new ST(template);
		for (int i = 0; i < reps; i++) {
			st.render();
		}
	}

	public void timeSingleBigger(int reps) {
		StringBuilder buf = new StringBuilder();
		for (int i=1; i<=1000; i++) buf.append("some text");
		String template = buf.toString();
		ST st = new ST(template);
		for (int i = 0; i < reps; i++) {
			st.render();
		}
	}
}
