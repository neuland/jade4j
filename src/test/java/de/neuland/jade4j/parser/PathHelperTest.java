package de.neuland.jade4j.parser;

import org.apache.commons.io.FilenameUtils;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class PathHelperTest extends ParserTest {

    PathHelper pathHelper = new PathHelper();
    @Test
    public void shouldResolvePath() throws Exception {
        String result = pathHelper.resolvePath("kek/index.jade","../_layout.jade", "jade");
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
        String result = pathHelper.resolvePath("C:\\kek\\index.jade","../_layout.jade",
                "jade");
        assertEquals(FilenameUtils.separatorsToSystem("C:\\_layout.jade"),result);

    }
}
