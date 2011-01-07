package org.stringtemplate.v4.test;

import org.junit.Test;
import org.stringtemplate.v4.ModelAdaptor;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import org.stringtemplate.v4.misc.STNoSuchPropertyException;
import org.stringtemplate.v4.misc.STRuntimeMessage;

import static org.junit.Assert.assertEquals;

public class TestModelAdaptors extends BaseTest {
	static class UserAdaptor implements ModelAdaptor {
		public Object getProperty(ST self, Object o, Object property, String propertyName)
			throws STNoSuchPropertyException
		{
			if ( propertyName.equals("id") ) return ((User)o).id;
			if ( propertyName.equals("name") ) return ((User)o).getName();
			throw new STNoSuchPropertyException(null, "User."+propertyName);
		}
	}

	static class UserAdaptorConst implements ModelAdaptor {
		public Object getProperty(ST self, Object o, Object property, String propertyName)
			throws STNoSuchPropertyException
		{
			if ( propertyName.equals("id") ) return "const id value";
			if ( propertyName.equals("name") ) return "const name value";
			throw new STNoSuchPropertyException(null, "User."+propertyName);
		}
	}

	static class SuperUser extends User {
		int bitmask;
		public SuperUser(int id, String name) {
			super(id, name);
			bitmask = 0x8080;
		}

		@Override
		public String getName() {
			return "super "+super.getName();
		}
	}

	@Test public void testSimpleAdaptor() throws Exception {
		String templates =
				"foo(x) ::= \"<x.id>: <x.name>\"\n";
		writeFile(tmpdir, "foo.stg", templates);
		STGroup group = new STGroupFile(tmpdir+"/foo.stg");
		group.registerModelAdaptor(User.class, new UserAdaptor());
		ST st = group.getInstanceOf("foo");
		st.add("x", new User(100, "parrt"));
		String expecting = "100: parrt";
		String result = st.render();
		assertEquals(expecting, result);
	}

	@Test public void testAdaptorAndBadProp() throws Exception {
		ErrorBufferAllErrors errors = new ErrorBufferAllErrors();
		String templates =
				"foo(x) ::= \"<x.qqq>\"\n";
		writeFile(tmpdir, "foo.stg", templates);
		STGroup group = new STGroupFile(tmpdir+"/foo.stg");
		group.setListener(errors);
		group.registerModelAdaptor(User.class, new UserAdaptor());
		ST st = group.getInstanceOf("foo");
		st.add("x", new User(100, "parrt"));
		String expecting = "";
		String result = st.render();
		assertEquals(expecting, result);

		STRuntimeMessage msg = (STRuntimeMessage)errors.errors.get(0);
		STNoSuchPropertyException e = (STNoSuchPropertyException)msg.cause;
		assertEquals("User.qqq", e.propertyName);
	}

	@Test public void testAdaptorCoversSubclass() throws Exception {
		String templates =
				"foo(x) ::= \"<x.id>: <x.name>\"\n";
		writeFile(tmpdir, "foo.stg", templates);
		STGroup group = new STGroupFile(tmpdir+"/foo.stg");
		group.registerModelAdaptor(User.class, new UserAdaptor());
		ST st = group.getInstanceOf("foo");
		st.add("x", new SuperUser(100, "parrt")); // create subclass of User
		String expecting = "100: super parrt";
		String result = st.render();
		assertEquals(expecting, result);
	}

	@Test public void testWeCanResetAdaptorCacheInvalidatedUponAdaptorReset() throws Exception {
		String templates =
				"foo(x) ::= \"<x.id>: <x.name>\"\n";
		writeFile(tmpdir, "foo.stg", templates);
		STGroup group = new STGroupFile(tmpdir+"/foo.stg");
		group.registerModelAdaptor(User.class, new UserAdaptor());
		group.getModelAdaptor(User.class); // get User, SuperUser into cache
		group.getModelAdaptor(SuperUser.class);

		group.registerModelAdaptor(User.class, new UserAdaptorConst());
		// cache should be reset so we see new adaptor
		ST st = group.getInstanceOf("foo");
		st.add("x", new User(100, "parrt"));
		String expecting = "const id value: const name value"; // sees UserAdaptorConst
		String result = st.render();
		assertEquals(expecting, result);
	}

	@Test public void testSeesMostSpecificAdaptor() throws Exception {
		String templates =
				"foo(x) ::= \"<x.id>: <x.name>\"\n";
		writeFile(tmpdir, "foo.stg", templates);
		STGroup group = new STGroupFile(tmpdir+"/foo.stg");
		group.registerModelAdaptor(User.class, new UserAdaptor());
		group.registerModelAdaptor(SuperUser.class, new UserAdaptorConst()); // most specific
		ST st = group.getInstanceOf("foo");
		st.add("x", new User(100, "parrt"));
		String expecting = "100: parrt";
		String result = st.render();
		assertEquals(expecting, result);

		st.remove("x");
		st.add("x", new SuperUser(100, "parrt"));
		expecting = "const id value: const name value"; // sees UserAdaptorConst
		result = st.render();
		assertEquals(expecting, result);
	}
}
