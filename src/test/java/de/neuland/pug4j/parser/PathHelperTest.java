package de.neuland.pug4j.parser;

import org.apache.commons.io.FilenameUtils;
import org.junit.Test;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;

public class PathHelperTest extends ParserTest {

    PathHelper pathHelper = new PathHelper();
    @Test
    public void shouldResolvePath() throws Exception {
        String result = pathHelper.resolvePath("kek/index.jade","../_layout.jade", "jade");
        assertEquals("_layout.jade",result);

    }
    @Test
    public void shouldResolvePath4() throws Exception {
        String result = pathHelper.resolvePath("kek/test/index.jade","../_layout.jade", "jade");
        assertEquals("kek/_layout.jade",result);

    }
    @Test
    public void shouldResolvePath5() throws Exception {
        String result = pathHelper.resolvePath("index.jade","../_layout.jade", "jade");
        assertNull(result);

    }
    @Test
    public void shouldResolvePath6() throws Exception {
        String result = pathHelper.resolvePath("kek/test/index.jade","/_layout.jade", "jade");
        assertEquals("_layout.jade",result);

    }
    @Test
    public void shouldResolvePath3() throws Exception {
        String result = pathHelper.resolvePath("test/index.jade","../_layout.jade",
                "jade");
        assertEquals("_layout.jade",result);

    }
    @Test
    public void shouldResolvePath2() throws Exception {
        String result = pathHelper.resolvePath("kek/index","../_layout", "jade");
        assertEquals("_layout.jade",result);

    }
    @Test
    public void shouldResolvePathWindows() throws Exception {
        String result = pathHelper.resolvePath("C:\\kek\\index.jade","../_layout.jade", "jade");
        assertEquals(FilenameUtils.separatorsToSystem("C:\\_layout.jade"),result);

    }
}
