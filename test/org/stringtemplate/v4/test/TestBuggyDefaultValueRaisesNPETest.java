package org.stringtemplate.v4.test;

import org.junit.Assert;
import org.junit.Test;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import org.stringtemplate.v4.misc.ErrorBuffer;

public class TestBuggyDefaultValueRaisesNPETest extends BaseTest {
	/**
	 * When the anonymous template specified as a default value for a formalArg
	 * contains a syntax error ST 4.0.2 emits a NullPointerException error
	 * (after the syntax error)
	 * 
	 * @throws Exception
	 */
	@Test
	public void testHandleBuggyDefaultArgument() throws Exception {
		String templates = "main(a={(<\"\")>}) ::= \"\"";
		writeFile(tmpdir, "t.stg", templates);

		final ErrorBuffer errors = new ErrorBuffer();
		STGroup group = new STGroupFile(tmpdir + "/t.stg");
		group.setListener(errors);

		ST st = group.getInstanceOf("main");
		String s = st.render();

		// Check the errors. This contained an "NullPointerException" before
		Assert.assertEquals(
				"t.stg 1:12: mismatched input ')' expecting RDELIM"+newline,
				errors.toString());
	}
}
