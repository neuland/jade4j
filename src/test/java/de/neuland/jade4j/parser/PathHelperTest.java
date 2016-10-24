package de.neuland.jade4j.parser;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class PathHelperTest extends ParserTest {

    PathHelper pathHelper = new PathHelper();
    @Test
    public void shouldResolvePath() throws Exception {
        String result = pathHelper.resolvePath("kek/index.jade","../_layout.jade", "");
        assertEquals("_layout.jade",result);

    }
    @Test
    public void shouldResolvePath3() throws Exception {
        String result = pathHelper.resolvePath("test/index.jade","../_layout.jade", "test");
        assertEquals("_layout.jade",result);

    }
    @Test
    public void shouldResolvePath2() throws Exception {
        String result = pathHelper.resolvePath("kek/index","../_layout", "");
        assertEquals("_layout.jade",result);

    }
    @Test
    public void shouldResolvePathWindows() throws Exception {
        String result = pathHelper.resolvePath("C:\\kek\\index.jade","../_layout.jade", "");
        assertEquals("C:/_layout.jade",result);

    }
}
