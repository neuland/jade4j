package de.neuland.jade4j.ognl;


public class OGNLExpressionTest {
/*
	private Map<String, Object> getDefaultModel() {
		Map<String, Object> defaultModel = new HashMap<String,Object>();
		defaultModel.put("booleanTrue", true);
		defaultModel.put("booleanFalse", false);
		defaultModel.put("stringHallo", "hallo!");
		return defaultModel;
	}
	
	private OgnlContext getNewContext() {
		OgnlContext context = new OgnlContext();
		context.clear();
		context.putAll(getDefaultModel());
		return context;
	}

	@Test
	public void testSimpleBoolean() {
		OgnlContext context = getNewContext();
		try {
			assertTrue((Boolean)Ognl.getValue("#booleanTrue", context, Ognl.getRoot(context)));
			assertFalse((Boolean)Ognl.getValue("#booleanFalse", context, Ognl.getRoot(context)));
		} catch (OgnlException e) {
			throw new RuntimeException("Expression could not be evaluated",e);
		}
	}

	@Test
	public void testSimpleString() {
		OgnlContext context = getNewContext();
		try {
			String result = (String)Ognl.getValue("#stringHallo", context, Ognl.getRoot(context));
			assertEquals("hallo!", result);
		} catch (OgnlException e) {
			throw new RuntimeException("Expression could not be evaluated",e);
		}
	}

	@Test
	public void testPropertyAccess() {
		OgnlContext context = getNewContext();
		Dummy dummy = new Dummy();
		dummy.setText("stefan");
		dummy.setBool(true);
		context.put("dummy", dummy);
		try {
			assertEquals("stefan", Ognl.getValue("#dummy.text", context, Ognl.getRoot(context)));
			assertTrue((Boolean)Ognl.getValue("#dummy.bool", context, Ognl.getRoot(context)));
		} catch (OgnlException e) {
			throw new RuntimeException("Expression could not be evaluated",e);
		}
	}

	@Test
	public void testBooleanExpression() {
		OgnlContext context = getNewContext();
		
		Dummy dummy = new Dummy();
		dummy.setText("stefan");
		context.put("dummy", dummy);
		try {
			Boolean result = (Boolean)Ognl.getValue("#dummy.text == 'stefan'", context, Ognl.getRoot(context));
			assertTrue(result);
		} catch (OgnlException e) {
			throw new RuntimeException("Expression could not be evaluated",e);
		}
	}
	
	@Test
	public void testOGNLProxyRootExpression() {
		OgnlContext context = getNewContext();
		
		Map<String,Object> root = new HashMap<String, Object>();
		
		Dummy dummy = new Dummy();
		dummy.setText("stefan");
		root.put("dummy", dummy);
		context.setRoot(root);
		
		try {
			Boolean result = (Boolean)Ognl.getValue("dummy.text == 'stefan'", context, root);
			assertTrue(result);
			
		} catch (OgnlException e) {
			throw new RuntimeException("Expression could not be evaluated",e);
		}
	}

	@Test
	public void testAssignment() {
		OgnlContext context = getNewContext();
		
		Map<String,Object> root = new HashMap<String, Object>();
		
		Dummy dummy = new Dummy();
		dummy.setText("stefan");
		root.put("dummy", dummy);
		context.setRoot(root);
		
		try {
			Ognl.getValue("dummy.text = 'michael'", context, root);
			Ognl.getValue("a = 'michael'", context, root);
			//assertTrue(result);
		} catch (OgnlException e) {
			throw new RuntimeException("Expression could not be evaluated",e);
		}
	}
	
	class Dummy {
		
		private boolean bool;
		private String text;
		public boolean isBool() {
			return bool;
		}
		public void setBool(boolean bool) {
			this.bool = bool;
		}
		public String getText() {
			return text;
		}
		public void setText(String text) {
			this.text = text;
		}
		
	}
	
	
	*/
	
	

}

/*
private List<String> criteria;
private List<Object> expressions;
private OgnlContext ctx;

public OGNLObjectFilter() {
	expressions = new ArrayList<Object>();
}

public boolean isFiltered(Object o, Map<String, Object> attribs) {
	OgnlContext context = new OgnlContext(ctx);
	if (attribs != null) context.putAll(attribs);
	for (Object expression : expressions) {
		Boolean value = true;
		try {
			if ( expression != null && context != null && o != null)
				value = (Boolean)Ognl.getValue(expression, context, o);
		} catch (OgnlException e) {
			throw new RuntimeException("Expression could not be evaluated",e);
		} 
		
		if (!value) {
			logger.trace("Filtering Object {} due to expression {}", new Object[]{o,expression.toString()} );
			return true;
		}
	}
	return false;
}

public void setContextAttributes(Map<String, Object> contextAttributes) {
	this.ctx.clear();
	this.ctx.putAll(contextAttributes);
}
*/