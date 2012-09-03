package de.neuland.jade4j.parser;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import java.util.HashMap;

import org.junit.Test;

import de.neuland.jade4j.Jade4J;
import de.neuland.jade4j.TestFileHelper;

public class MixinsTest extends ParserTest {


    @Test
    public void shouldReturnABlockWithTokens2() throws Exception {
        String path = TestFileHelper.getResourcePath("/parser/mixins_with_attributes.jade");
        String render = Jade4J.render(path, new HashMap<String, Object>());
        assertThat(render,is("<div class=\"templates\"><div class=\"block positif\">content</div></div>"));
        System.out.println(render);
    }

}
