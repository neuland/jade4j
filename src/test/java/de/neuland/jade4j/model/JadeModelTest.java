package de.neuland.jade4j.model;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class JadeModelTest {

	private JadeModel model;

	@Before
	public void setup() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("hello", "world");
		map.put("foo", "bar");
		model = new JadeModel(map);
	}

	@Test
	public void scope() throws Exception {
		assertEquals("world", model.get("hello"));
		model.pushScope();
		model.put("hello", "new world");
		assertEquals("new world", model.get("hello"));
		model.popScope();
		assertEquals("world", model.get("hello"));
	}
	
	@Test
	public void defaults() throws Exception {
		Map<String, Object> defaults = new HashMap<String, Object>();
		defaults.put("hello", "world");

		model = new JadeModel(defaults);
		model.put("new", true);
		
		assertFalse(defaults.containsKey("new"));
		assertTrue(model.containsKey("new"));
		assertEquals(model.get("hello"), "world");
	}
}
